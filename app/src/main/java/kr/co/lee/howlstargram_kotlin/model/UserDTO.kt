package kr.co.lee.howlstargram_kotlin.model

data class UserDTO(
    var userEmail: String = "",
    var userName: String = "",
    var userNickName: String = "",
    var followerCount: Int = 0,
    val followers: MutableMap<String, Boolean> = HashMap(),
    var followingCount: Int = 0,
    val followings: MutableMap<String, Boolean> = HashMap()
)
