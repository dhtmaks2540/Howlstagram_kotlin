package kr.co.lee.howlstargram_kotlin.model

data class Content(
    var contentDTO: ContentDTO? = null,
    var contentUid: String? = null,
    var profileUrl: String? = null,
    var commentSize: Int = 0
)