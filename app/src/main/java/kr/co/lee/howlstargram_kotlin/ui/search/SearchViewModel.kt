package kr.co.lee.howlstargram_kotlin.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kr.co.lee.howlstargram_kotlin.di.IoDispatcher
import kr.co.lee.howlstargram_kotlin.model.Content
import kr.co.lee.howlstargram_kotlin.model.User
import kr.co.lee.howlstargram_kotlin.model.UserDTO
import kr.co.lee.howlstargram_kotlin.utilites.UiState
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val fireStore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<User>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<User>>> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = UiState.Loading
    )

    val _textInput = MutableLiveData<String>()
    val textInput: LiveData<String> = _textInput

    fun findUsers(text: String): Job {
        val job = viewModelScope.launch {
            searchRepository.getAllUsers(text)
                .collect { state ->
                    _uiState.value = state
                }
        }

        return job
    }

    private suspend fun requestFindUsers(text: String): List<User> {
        return with(ioDispatcher) {
            val userShot = fireStore.collection("users").limit(20)
                .get().await()

            val userItems = ArrayList<User>()

            userShot.forEach { documentSnapshot ->
                val nickNameData = documentSnapshot.getString("userNickName")

                if(nickNameData != null && nickNameData.contains(text)) {
                    val item = documentSnapshot.toObject(UserDTO::class.java)
                    val profileShot = fireStore.collection("profileImages").document(documentSnapshot.id).get().await()

                    userItems.add(User(userDTO = item, profileUrl = profileShot.data?.get("image").toString(), userUid = documentSnapshot.id))
                }
            }

            userItems
        }
    }
}