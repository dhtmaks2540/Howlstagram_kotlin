package kr.co.lee.howlstargram_kotlin.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kr.co.lee.howlstargram_kotlin.di.CurrentUserUid
import kr.co.lee.howlstargram_kotlin.di.IoDispatcher
import kr.co.lee.howlstargram_kotlin.model.Content
import kr.co.lee.howlstargram_kotlin.model.ContentDTO
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val detailRepository: DetailRepository,
    @CurrentUserUid val currentUserUid: String,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val _contents = MutableLiveData<List<Content>>()
    val contents: LiveData<List<Content>> = _contents

    fun setFavoriteEvent(contentUid: String, position: Int) {
        viewModelScope.launch {
            favoriteEvent(contentUid, position)
        }
    }

    // 게시글 불러오기
    fun loadContents(): Job {
        val job = viewModelScope.launch {
            val result = detailRepository.requestLoadImage()
            _contents.postValue(result)
        }

        return job
    }

    // 게시글 불러오기
//    fun loadImages(): Job {
//        val job = viewModelScope.launch {
//            val mySnapshot = fireStore.collection("users")
//                .document(currentUserUid)
//                .get().await()
//
//            val followings = mySnapshot.data?.get("followings") as Map<*, *>
//            val myFollowings = if(followings.keys.isNotEmpty()) {
//                followings
//            } else {
//                hashMapOf("key" to true)
//            }
//
//            val snapshot = fireStore.collection("images")
//                .orderBy("timestamp", Query.Direction.DESCENDING)
//                .whereIn("uid", myFollowings.keys.toList())
//                .limit(20)
//                .get().await()
//
//            withContext(ioDispatcher) {
//                val contents = ArrayList<Content>()
//                snapshot.documents.forEach { documentSnapshot ->
//                    val item = documentSnapshot.toObject(ContentDTO::class.java)!!
//
//                    val profileShot = fireStore.collection("profileImages").document(item.uid!!).get().await()
//                    val commentShot = fireStore.collection("images").document(documentSnapshot.id).collection("comments").get().await()
//
//                    val content = Content(item, documentSnapshot.id, profileShot.data?.get("image").toString(), commentShot.size())
//                    contents.add(content)
//                }
//                _contents.postValue(contents)
//            }
//        }
//
//        return job
//    }

    //     좋아요 이벤트
    private suspend fun favoriteEvent(contentUid: String, position: Int) {
        withContext(ioDispatcher) {
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
                val newContents = contents.value?.toMutableList()
                newContents?.set(position, newContents[position].copy(contentDTO = contentDTO))
                _contents.postValue(newContents!!)
            }
        }
    }
}