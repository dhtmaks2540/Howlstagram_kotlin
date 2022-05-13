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
import kr.co.lee.howlstargram_kotlin.model.Content
import kr.co.lee.howlstargram_kotlin.model.ContentDTO
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(val fireStore: FirebaseFirestore, val firebaseAuth: FirebaseAuth): ViewModel() {
    private val _uid = MutableLiveData<String>()
    private val _contents = MutableLiveData<List<Content>>()

    val contents: LiveData<List<Content>> = _contents
    val uid: LiveData<String> = _uid

    init {
        _uid.postValue(firebaseAuth.currentUser!!.uid)
    }

    fun setFavoriteEvent(contentUid: String) {
        viewModelScope.launch {
            favoriteEvent(contentUid)
        }
    }

    fun loadImages() {
        viewModelScope.launch {
            val snapshot = fireStore.collection("images")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().await()

            launch {
                val contents = ArrayList<Content>()
                snapshot.documents.forEach { documentSnapshot ->
                    val item = documentSnapshot.toObject(ContentDTO::class.java)!!

                    val profileShot = fireStore.collection("profileImages").document(item.uid!!).get().await()
                    val commentShot = fireStore.collection("images").document(documentSnapshot.id).collection("comments").get().await()

                    val content = Content(item, documentSnapshot.id, profileShot.data?.get("image").toString(), commentShot.size())
                    contents.add(content)
                }
                _contents.postValue(contents)
            }
        }
    }

    private suspend fun favoriteEvent(contentUid: String) {
        withContext(Dispatchers.IO) {
            val tsDoc = fireStore.collection("images").document(contentUid)
            fireStore.runTransaction { transaction ->
                val contentDTO = transaction.get(tsDoc).toObject(ContentDTO::class.java)
                uid.value?.let {
                    if(contentDTO?.favorites?.containsKey(it)!!) {
                        contentDTO.favoriteCount = contentDTO.favoriteCount - 1
                        contentDTO.favorites.remove(it)
                    } else {
                        contentDTO.favoriteCount = contentDTO.favoriteCount + 1
                        contentDTO.favorites[it] = true
                    }
                }
                transaction.set(tsDoc, contentDTO!!)

            }
        }
    }
}