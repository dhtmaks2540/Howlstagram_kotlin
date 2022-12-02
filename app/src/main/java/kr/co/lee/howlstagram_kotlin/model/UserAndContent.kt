package kr.co.lee.howlstagram_kotlin.model

data class UserAndContent(
    val user: User,
    val contents: List<ContentDTO>
)
