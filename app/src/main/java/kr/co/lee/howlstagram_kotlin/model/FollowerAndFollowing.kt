package kr.co.lee.howlstagram_kotlin.model

data class FollowerAndFollowing(
    val follower: Map<String, Boolean>,
    val following: Map<String, Boolean>
)
