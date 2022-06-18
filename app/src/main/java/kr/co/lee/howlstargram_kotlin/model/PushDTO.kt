package kr.co.lee.howlstargram_kotlin.model

data class PushDTO(
    // Push를 받는 사람
    val to: String,
    val notification: Notification
)

data class Notification(
    // Push 메시지
    val body: String,
    // Push 메시지의 제목
    val title: String
)
