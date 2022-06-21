package kr.co.lee.howlstargram_kotlin.ui.detail

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kr.co.lee.howlstargram_kotlin.di.CurrentUserUid
import kr.co.lee.howlstargram_kotlin.di.IoDispatcher
import kr.co.lee.howlstargram_kotlin.model.Content
import kr.co.lee.howlstargram_kotlin.model.ContentDTO
import kr.co.lee.howlstargram_kotlin.utilites.UiState
import javax.inject.Inject

class DetailRepository @Inject constructor(
    private val fireStore: FirebaseFirestore,
    @CurrentUserUid private val currentUserUid: String,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    // 게시글 불러오기
//    suspend fun requestLoadImage(): ArrayList<Content> {
//        return withContext(ioDispatcher) {
//            val mySnapShot = fireStore.collection("users")
//                .document(currentUserUid)
//                .get().await()
//
//            val items = mySnapShot.data?.get("followings") as HashMap<String, Boolean>
//            items[currentUserUid] = true
//            val myFollowings = if (items.keys.isNotEmpty()) {
//                items
//            } else {
//                hashMapOf("key" to true)
//            }
//
//            val snapshot = fireStore.collection("images")
//                .orderBy("timestamp", Query.Direction.DESCENDING)
//                .whereIn("uid", myFollowings.keys.toList())
//                .get().await()
//
//            val contents = ArrayList<Content>()
//
//            snapshot.documents.forEach { documentSnapshot ->
//                val item = documentSnapshot.toObject(ContentDTO::class.java)!!
//
//                val profileShot =
//                    fireStore.collection("profileImages").document(item.uid!!).get().await()
//                val commentShot = fireStore.collection("images").document(documentSnapshot.id)
//                    .collection("comments").get().await()
//
//                val content = Content(
//                    item,
//                    documentSnapshot.id,
//                    profileShot.data?.get("image").toString(),
//                    commentShot.size()
//                )
//                contents.add(content)
//            }
//
//            contents
//        }
//    }

    fun getAllContents() = flow<UiState<List<Content>>> {
        val mySnapShot = fireStore.collection("users")
            .document(currentUserUid)
            .get().await()

        val items = mySnapShot.data?.get("followings") as Map<*, *>
        val myFollowings = if (items.keys.isNotEmpty()) {
            items
        } else {
            hashMapOf(currentUserUid to true)
        }

        val snapshot = fireStore.collection("images")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .whereIn("uid", myFollowings.keys.toList())
            .limit(20)
            .get().await()

        val contents = ArrayList<Content>()

        snapshot.documents.forEach { documentSnapshot ->
            val item = documentSnapshot.toObject(ContentDTO::class.java)!!

            val profileShot =
                fireStore.collection("profileImages").document(item.uid!!).get().await()
            val commentShot = fireStore.collection("images").document(documentSnapshot.id)
                .collection("comments").get().await()

            val content = Content(
                item,
                documentSnapshot.id,
                profileShot.data?.get("image").toString(),
                commentShot.size()
            )
            contents.add(content)
        }

        emit(UiState.Success(contents))
    }.catch {
        emit(UiState.Failed(it.message.toString()))
    }.flowOn(ioDispatcher)

//    // 좋아요 이벤트
//    suspend fun requestFavoriteEvent(contents: List<Content>?, contentUid: String, position: Int) {
//        val newContents = contents?.toMutableList()
//
//        withContext(ioDispatcher) {
//            val tsDoc = fireStore.collection("images").document(contentUid)
//            fireStore.runTransaction { transaction ->
//                val contentDTO = transaction.get(tsDoc).toObject(ContentDTO::class.java)
//                if(contentDTO?.favorites?.containsKey(currentUserUid)!!) {
//                    contentDTO.favoriteCount = contentDTO.favoriteCount - 1
//                    contentDTO.favorites.remove(currentUserUid)
//                } else {
//                    contentDTO.favoriteCount = contentDTO.favoriteCount + 1
//                    contentDTO.favorites[currentUserUid] = true
//                }
//
//                transaction.set(tsDoc, contentDTO)
//                contentDTO
//            }.addOnSuccessListener {
//                newContents?.set(position, newContents[position].copy(contentDTO = it))
//            }
//            newContents!!
//        }
//
////        newContents!!.toList()
//
////        return withContext(ioDispatcher) {
////            val tsDoc = fireStore.collection("images").document(contentUid)
////            fireStore.runTransaction { transaction ->
////                val contentDTO = transaction.get(tsDoc).toObject(ContentDTO::class.java)
////                if(contentDTO?.favorites?.containsKey(currentUserUid)!!) {
////                    contentDTO.favoriteCount = contentDTO.favoriteCount - 1
////                    contentDTO.favorites.remove(currentUserUid)
////                } else {
////                    contentDTO.favoriteCount = contentDTO.favoriteCount + 1
////                    contentDTO.favorites[currentUserUid] = true
////                }
////
////                transaction.set(tsDoc, contentDTO)
////            }
////
////            newContents!!.toList()
////        }
}
