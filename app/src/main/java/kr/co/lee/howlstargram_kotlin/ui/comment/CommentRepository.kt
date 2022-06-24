package kr.co.lee.howlstargram_kotlin.ui.comment

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kr.co.lee.howlstargram_kotlin.di.CurrentUserUid
import kr.co.lee.howlstargram_kotlin.di.IoDispatcher
import kr.co.lee.howlstargram_kotlin.model.Comment
import kr.co.lee.howlstargram_kotlin.model.CommentDTO
import kr.co.lee.howlstargram_kotlin.model.Content
import kr.co.lee.howlstargram_kotlin.model.User
import kr.co.lee.howlstargram_kotlin.model.UserDTO
import kr.co.lee.howlstargram_kotlin.utilites.UiState
import javax.inject.Inject

class CommentRepository @Inject constructor(
    private val fireStore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @CurrentUserUid private val currentUserUid: String,
) {
    // 내 정보 불러오기
    suspend fun loadMyInfo(): User {
        return withContext(ioDispatcher) {
            val userSnapshot = fireStore.collection("users")
                .document(currentUserUid)
                .get().await()

            val profileSnapshot = fireStore.collection("profileImages")
                .document(currentUserUid)
                .get().await()

            val item = userSnapshot.toObject(UserDTO::class.java)
            val user = User(
                userDTO = item!!,
                profileUrl = profileSnapshot.data?.get("image").toString(),
                currentUserUid
            )

            user
        }
    }

    // 댓글 목록 불러 오기
    fun getAllComments(content: Content) = flow<UiState<List<Comment>>> {
        val commentSnapShot = fireStore.collection("images")
            .document(content.contentUid!!)
            .collection("comments")
            .limit(20)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get().await()

        val comments = ArrayList<Comment>()

        // 작성자 글 삽입
        comments.add(
            Comment(
                commentDTO = CommentDTO(
                    content.contentDTO?.uid,
                    content.contentDTO?.userNickName,
                    content.contentDTO?.explain,
                    content.contentDTO?.timestamp!!
                ),
                profileUrl = content.profileUrl
            )
        )

        // 댓글 불러오기
        commentSnapShot.documents.forEach { documentSnapshot ->
            val item = documentSnapshot.toObject(CommentDTO::class.java)

            val userSnapShot = fireStore.collection("users")
                .document(item?.uid!!)
                .get().await()

            val profileShot = fireStore.collection("profileImages")
                .document(item.uid!!)
                .get().await()

            val comment = Comment(
                commentDTO = item.copy(
                    userNickName = userSnapShot.data?.get("userNickName").toString()
                ),
                profileUrl = profileShot.data?.get("image").toString(),
                commentUid = documentSnapshot.id
            )
            comments.add(comment)
        }

        emit(UiState.Success(comments))
    }.catch {
        emit(UiState.Failed(it.message.toString()))
    }.flowOn(ioDispatcher)

    // 댓글 추가하기
    fun addComment(comment: Comment, contentUid: String) = flow<UiState<Comment>> {
        fireStore.collection("images")
            .document(contentUid)
            .collection("comments")
            .document().set(comment.commentDTO!!)

        emit(UiState.Success(comment))
    }.catch {
        emit(UiState.Failed(it.message.toString()))
    }.flowOn(ioDispatcher)
}
