package kr.co.lee.howlstargram_kotlin.ui.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kr.co.lee.howlstargram_kotlin.di.CurrentUserUid
import kr.co.lee.howlstargram_kotlin.di.IoDispatcher
import kr.co.lee.howlstargram_kotlin.model.*
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @CurrentUserUid private val currentUserUid: String,
) : ViewModel() {
    val _commentContent = MutableLiveData<String>()
    val commentContent: LiveData<String> = _commentContent

    // 작성자 글
    private val _content = MutableLiveData<Content>()
    val content: LiveData<Content> = _content

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>> = _comments

    private val _comment = MutableLiveData<Comment>()
    val comment: LiveData<Comment> = _comment

    // 내 정보 불러오기
    fun loadMyInfo() {
        viewModelScope.launch(ioDispatcher) {
            val userSnapshot = fireStore.collection("users")
                .document(currentUserUid)
                .get().await()

            val profileSnapshot = fireStore.collection("profileImages")
                .document(currentUserUid)
                .get().await()

            val item = userSnapshot.toObject(UserDTO::class.java)
            val user = User(userDTO = item!!, profileUrl = profileSnapshot.data?.get("image").toString(), currentUserUid)

            _user.postValue(user)
        }
    }

    // 작성자 글 저장
    fun setInitData(content: Content?) {
        content?.let {
            viewModelScope.launch {
                _content.postValue(it)
            }
        }
    }

    // 댓글 불러오기
    fun loadComments(contentUid: String) {
        viewModelScope.launch(ioDispatcher) {
            val snapShot = fireStore.collection("images")
                .document(contentUid)
                .collection("comments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().await()

            val comments = ArrayList<Comment>()

            // 작성자 글 삽입
            comments.add(
                Comment(
                    commentDTO = CommentDTO(
                        content.value?.contentDTO?.uid,
                        content.value?.contentDTO?.userNickName,
                        content.value?.contentDTO?.explain,
                        content.value?.contentDTO?.timestamp!!
                    ),
                    profileUrl = content.value?.profileUrl
                )
            )

            // 댓글 불러오기
            snapShot.documents.forEach { documentSnapshot ->
                val item = documentSnapshot.toObject(CommentDTO::class.java)
                val profileShot = fireStore.collection("profileImages").document(item?.uid!!).get().await()
                val comment = Comment(commentDTO = item, profileUrl = profileShot.data?.get("image").toString(), commentUid = documentSnapshot.id)
                comments.add(comment)
            }
            _comments.postValue(comments)
        }
    }

    fun addComment() {
        viewModelScope.launch {
            insertComment()
        }
    }

    // 댓글 추가
    private suspend fun insertComment() {
        withContext(ioDispatcher) {
            val commentDTO = CommentDTO(currentUserUid, user.value?.userDTO?.userNickName, commentContent.value, System.currentTimeMillis())
            val profileShot = fireStore.collection("profileImages")
                .document(currentUserUid)
                .get().await()

            fireStore.collection("images")
                .document(content.value?.contentUid!!)
                .collection("comments")
                .document().set(commentDTO)

//            _comment.postValue(Comment(commentDTO = commentDTO, profileUrl = profileShot.data?.get("image").toString()))
            val newComments = comments.value?.toMutableList()
            newComments?.add(1, Comment(commentDTO = commentDTO, profileUrl = profileShot.data?.get("image").toString()))
            _comments.postValue(newComments!!)
        }
    }
}
