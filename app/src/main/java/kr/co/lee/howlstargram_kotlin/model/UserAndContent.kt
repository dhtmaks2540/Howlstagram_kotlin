package kr.co.lee.howlstargram_kotlin.model

data class UserAndContent(
    val user: User,
    val contents: List<ContentDTO>
)
