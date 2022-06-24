package kr.co.lee.howlstargram_kotlin.ui.grid

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kr.co.lee.howlstargram_kotlin.di.IoDispatcher
import kr.co.lee.howlstargram_kotlin.model.Content
import kr.co.lee.howlstargram_kotlin.model.ContentDTO
import kr.co.lee.howlstargram_kotlin.utilites.UiState
import javax.inject.Inject

class GridRepository @Inject constructor(
    private val fireStore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    // 게시글 호출하기
    fun getAllContents() = flow<UiState<List<Content>>> {
        val snapshot = fireStore.collection("images")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(30)
            .get().await()

        val contents = ArrayList<Content>()

        snapshot.documents.forEach { documentSnapshot ->
            val contentDTO = documentSnapshot.toObject(ContentDTO::class.java)
            contents.add(Content(contentDTO = contentDTO, contentUid = documentSnapshot.id))
        }

        emit(UiState.Success(contents))
    }.catch {
        emit(UiState.Failed(it.message.toString()))
    }.flowOn(ioDispatcher)
}