package kr.co.lee.howlstargram_kotlin.model

data class ContentDTO(
    var explain: String? = null,
    var imageUrl: String? = null,
    var uid: String? = null,
    var userId: String? = null,
    var timestamp: Long = 0,
    var favoriteCount: Int = 0,
    var favorites: MutableMap<String, Boolean> = HashMap(),
)
