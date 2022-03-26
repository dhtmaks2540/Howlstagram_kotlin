package kr.co.lee.howlstargram_kotlin.model

data class Comment(
    var uid: String? = null,
    var userId: String? = null,
    var comment: String? = null,
    var timestamp: Long = 0
)
