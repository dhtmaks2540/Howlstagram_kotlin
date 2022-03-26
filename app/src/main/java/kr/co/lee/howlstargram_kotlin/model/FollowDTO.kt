package kr.co.lee.howlstargram_kotlin.model

data class FollowDTO(
    val followerCount: Int = 0,
    val followers: Map<String, Boolean>,
    val followingCount: Int = 0,
    val followings: Map<String, Boolean>
)
