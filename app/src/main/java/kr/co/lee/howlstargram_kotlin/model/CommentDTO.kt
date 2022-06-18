package kr.co.lee.howlstargram_kotlin.model

data class CommentDTO(
    var uid: String? = null,
    var userNickName: String? = null,
    var comment: String? = null,
    var timestamp: Long = 0
)
