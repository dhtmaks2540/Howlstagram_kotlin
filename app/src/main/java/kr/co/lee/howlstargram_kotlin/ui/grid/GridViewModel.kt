package kr.co.lee.howlstargram_kotlin.ui.grid

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kr.co.lee.howlstargram_kotlin.di.IoDispatcher
import kr.co.lee.howlstargram_kotlin.model.ContentDTO
import javax.inject.Inject

@HiltViewModel
class GridViewModel @Inject constructor(private val fireStore: FirebaseFirestore, @IoDispatcher private val ioDispatcher: CoroutineDispatcher) : ViewModel() {
    private val _contentDTOs = MutableLiveData<List<ContentDTO>>()
    val contentDTOs: LiveData<List<ContentDTO>> = _contentDTOs

    fun loadContentDTO() {
        viewModelScope.launch {
            val result = loadContentDTOs()
            _contentDTOs.postValue(result)
        }
    }

    private suspend fun loadContentDTOs(): List<ContentDTO> {
        return withContext(ioDispatcher) {
            val snapshot = fireStore.collection("images")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().await()

            val contentDTOs = ArrayList<ContentDTO>()

            snapshot.documents.forEach { documentSnapshot ->
                val contentDTO = documentSnapshot.toObject(ContentDTO::class.java)
                contentDTOs.add(contentDTO!!)
            }

            contentDTOs
        }
    }
}