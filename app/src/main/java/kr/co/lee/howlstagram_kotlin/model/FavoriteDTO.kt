package kr.co.lee.howlstagram_kotlin.model

data class FavoriteDTO(
    val userUid: String,
    val profileUrl: String,
    val userName: String,
    val userNickName: String,
    val isFollow: Boolean
)
