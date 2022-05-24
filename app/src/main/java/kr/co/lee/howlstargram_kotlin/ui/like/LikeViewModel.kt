package kr.co.lee.howlstargram_kotlin.ui.like

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kr.co.lee.howlstargram_kotlin.di.IoDispatcher
import kr.co.lee.howlstargram_kotlin.model.FavoriteDTO
import kr.co.lee.howlstargram_kotlin.model.UserDTO
import javax.inject.Inject

@HiltViewModel
class LikeViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher): ViewModel() {

    private val _favorites = MutableLiveData<Map<String, Boolean>>()
    val favorites: LiveData<Map<String, Boolean>> = _favorites

    private val _uid = MutableLiveData<String>()
    val uid: LiveData<String> = _uid

    private val _favoriteDTOs = MutableLiveData<List<FavoriteDTO>>()
    val favoriteDTOs: LiveData<List<FavoriteDTO>> = _favoriteDTOs

    fun setFavorites(favorites: Map<String, Boolean>?) {
        favorites?.let {
            viewModelScope.launch {
                _uid.postValue(auth.currentUser?.uid)
                _favorites.postValue(it)
            }
        }
    }

    fun loadFavorite(favorites: Map<String, Boolean>) {
        viewModelScope.launch {
            val result = loadFavorites(favorites)
            _favoriteDTOs.postValue(result)
        }
    }

    private suspend fun loadFavorites(favorites: Map<String, Boolean>): ArrayList<FavoriteDTO> {
        return withContext(ioDispatcher) {
            val favoriteDTOItems = ArrayList<FavoriteDTO>()
            favorites.forEach { (userId, _) ->
                val userSnapShot = fireStore.collection("users").document(userId).get().await()
                val profileSnapShot = fireStore.collection("profileImages").document(userId).get().await()
                val isFollow = (userSnapShot.data?.get("followers") as Map<*, *>).containsKey(uid.value)

                val userItem = userSnapShot.toObject(UserDTO::class.java)

                userItem?.let {
                    val favoriteDTO = FavoriteDTO(userUid = userId,
                        profileUrl = profileSnapShot.data?.get("image").toString(),
                        userName = it.userName,
                        userNickName = it.userNickName,
                        isFollow = isFollow)

                    favoriteDTOItems.add(favoriteDTO)
                }
            }

            favoriteDTOItems
        }
    }
}