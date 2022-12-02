package kr.co.lee.howlstagram_kotlin.model

data class Comment(
    var commentDTO: CommentDTO? = null,
    var commentUid: String? = null,
    var profileUrl: String? = null,
)
