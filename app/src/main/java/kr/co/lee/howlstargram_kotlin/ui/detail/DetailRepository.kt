package kr.co.lee.howlstargram_kotlin.ui.detail

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Transaction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kr.co.lee.howlstargram_kotlin.di.CurrentUserUid
import kr.co.lee.howlstargram_kotlin.di.IoDispatcher
import kr.co.lee.howlstargram_kotlin.model.Content
import kr.co.lee.howlstargram_kotlin.model.ContentDTO
import kr.co.lee.howlstargram_kotlin.utilites.UiState
import org.joda.time.DateTime
import javax.inject.Inject

class DetailRepository @Inject constructor(
    private val fireStore: FirebaseFirestore,
    @CurrentUserUid private val currentUserUid: String,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    fun getAllContents() = flow<UiState<List<Content>>> {
        val mySnapShot = fireStore.collection("users")
            .document(currentUserUid)
            .get().await()

        val myFollowings = mySnapShot.data?.get("followings") as HashMap<String, Boolean>
        myFollowings[currentUserUid] = true

        val timeStamp = DateTime().minusDays(2).millis

        val snapshot = fireStore.collection("images")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .endAt(timeStamp)
            .whereIn("uid", myFollowings.keys.toList())
            .limit(20)
            .get().await()

        val contents = ArrayList<Content>()

        snapshot.documents.forEach { documentSnapshot ->
            val item = documentSnapshot.toObject(ContentDTO::class.java)!!

            val userSnapShot = fireStore.collection("users")
                .document(item.uid!!)
                .get().await()

            val profileSnapShot = fireStore.collection("profileImages")
                .document(item.uid!!)
                .get().await()

            val commentShot = fireStore.collection("images")
                .document(documentSnapshot.id)
                .collection("comments")
                .get().await()

            val content = Content(
                item.copy(
                    userName = userSnapShot.data?.get("userName").toString(),
                    userNickName = userSnapShot.data?.get("userNickName").toString()
                ),
                documentSnapshot.id,
                profileSnapShot.data?.get("image").toString(),
                commentShot.size()
            )
            contents.add(content)
        }

        emit(UiState.Success(contents))
    }.catch {
        emit(UiState.Failed(it.message.toString()))
    }.flowOn(ioDispatcher)

    suspend fun favoriteEvent(contentUid: String): Task<Transaction> {
        return withContext(ioDispatcher) {
            val tsDoc = fireStore.collection("images").document(contentUid)
            fireStore.runTransaction { transaction ->
                val contentDTO = transaction.get(tsDoc).toObject(ContentDTO::class.java)
                if (contentDTO?.favorites?.containsKey(currentUserUid)!!) {
                    contentDTO.favoriteCount = contentDTO.favoriteCount - 1
                    contentDTO.favorites.remove(currentUserUid)
                } else {
                    contentDTO.favoriteCount = contentDTO.favoriteCount + 1
                    contentDTO.favorites[currentUserUid] = true
                }

                transaction.set(tsDoc, contentDTO)
            }
        }
    }
}
