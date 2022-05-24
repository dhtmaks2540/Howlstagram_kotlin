package kr.co.lee.howlstargram_kotlin.ui.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kr.co.lee.howlstargram_kotlin.model.Comment
import kr.co.lee.howlstargram_kotlin.model.CommentDTO
import kr.co.lee.howlstargram_kotlin.model.Content
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(private val fireStore: FirebaseFirestore, private val firebaseAuth: FirebaseAuth): ViewModel() {

    private val _content = MutableLiveData<Content>()
    private val _comments = MutableLiveData<List<Comment>>()
    private val _userId = MutableLiveData<String>()
    private val _uid = MutableLiveData<String>()
    private val _comment = MutableLiveData<Comment>()
    private val _profileUrl = MutableLiveData<String>()
    val _commentContent = MutableLiveData<String>()

    val comments: LiveData<List<Comment>> = _comments
    val userId: LiveData<String> = _userId
    val uid: LiveData<String> = _uid
    val commentContent: LiveData<String> = _commentContent
    val comment: LiveData<Comment> = _comment
    val profileUrl: LiveData<String> = _profileUrl
    val content: LiveData<Content> = _content

    init {
        viewModelScope.launch(Dispatchers.Default) {
            _userId.postValue(firebaseAuth.currentUser?.email)
            _uid.postValue(firebaseAuth.currentUser?.uid)
        }
    }

    fun loadProfileUrl(uid: String) {
        viewModelScope.launch {
            val profileSnapshot = fireStore.collection("profileImages").document(uid).get().await()
            _profileUrl.postValue(profileSnapshot.data?.get("image").toString())
        }
    }

    // Intent로 넘어온 게시글 Uid 변수 값 지정
    fun setContent(content: Content) {
        viewModelScope.launch {
            _content.postValue(content)
        }
    }

    // 댓글 불러오기
    fun loadComments(contentUid: String) {
        viewModelScope.launch {
            val snapShot = fireStore.collection("images")
                .document(contentUid)
                .collection("comments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            launch(Dispatchers.IO) {
                val comments = ArrayList<Comment>()
                // 작성자
                comments.add(Comment(CommentDTO(content.value?.contentDTO?.uid, content.value?.contentDTO?.userId,
                    content.value?.contentDTO?.explain, content.value?.contentDTO?.timestamp!!), content.value?.profileUrl))

                snapShot.documents.forEach { documentSnapshot ->
                    val item = documentSnapshot.toObject(CommentDTO::class.java)
                    val profileShot = fireStore.collection("profileImages").document(item?.uid!!).get().await()
                    val comment = Comment(item, profileShot.data?.get("image").toString())
                    comments.add(comment)
                }
                _comments.postValue(comments)
            }
        }
    }

    fun addComment() {
        viewModelScope.launch {
            insertComment()
        }
    }

    // 댓글 추가
    private suspend fun insertComment() {
        withContext(Dispatchers.IO) {
            val commentDTO = CommentDTO(uid.value, userId.value, commentContent.value, System.currentTimeMillis())
            val profileShot = fireStore.collection("profileImages").document(uid.value!!).get().await()
            _comment.postValue(Comment(commentDTO, profileShot.data?.get("image").toString()))
            fireStore.collection("images").document(content.value?.contentUid!!).collection("comments").document().set(commentDTO)
        }
    }
}