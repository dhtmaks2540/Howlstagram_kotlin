package kr.co.lee.howlstargram_kotlin.model

data class FollowerAndFollowing(
    val follower: Map<String, Boolean>,
    val following: Map<String, Boolean>
)
