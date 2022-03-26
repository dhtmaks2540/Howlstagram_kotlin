package kr.co.lee.howlstargram_kotlin.model

data class AlarmDTO (
    val destinationUid: String,
    val userId: String,
    val uid: String,
    // 어떤 메시지의 종류인지 알수있는 변수
    // 0 : Like Alarm
    // 1 : Comment Alarm
    // 2 : Follow Alarm
    val kind: Int = 0,
    val message: String,
    val timestamp: Long = 0
    )