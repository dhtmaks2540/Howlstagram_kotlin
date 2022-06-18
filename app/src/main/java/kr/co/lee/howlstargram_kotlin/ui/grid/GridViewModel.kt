package kr.co.lee.howlstargram_kotlin.ui.grid

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kr.co.lee.howlstargram_kotlin.di.IoDispatcher
import kr.co.lee.howlstargram_kotlin.model.Content
import kr.co.lee.howlstargram_kotlin.model.ContentDTO
import javax.inject.Inject

@HiltViewModel
class GridViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _contents = MutableLiveData<List<Content>>()
    val contents: LiveData<List<Content>> = _contents

    fun loadContentDTO(): Job {
        val job = viewModelScope.launch {
            val result = loadContents()
            _contents.postValue(result)
        }

        return job
    }

    // 글 불러오기
    private suspend fun loadContents(): List<Content> {
        return withContext(ioDispatcher) {
            val snapshot = fireStore.collection("images")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(20)
                .get().await()

            val contents = ArrayList<Content>()

            snapshot.documents.forEach { documentSnapshot ->
                val contentDTO = documentSnapshot.toObject(ContentDTO::class.java)
                contents.add(Content(contentDTO = contentDTO, contentUid = documentSnapshot.id))
            }

            contents
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }
}