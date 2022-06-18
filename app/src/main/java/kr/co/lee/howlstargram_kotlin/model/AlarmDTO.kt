package kr.co.lee.howlstargram_kotlin.model

/**
 * kind : 어떤 메시지의 종류인지 알수있는 변수
 * 0 : Like Alarm
 * 1 : Comment Alarm
 * 2 : Follow Alarm
 */
data class AlarmDTO(
    var destinationUid: String? = null,
    var userId: String? = null,
    var uid: String? = null,
    var kind: Int = 0,
    var message: String? = null,
    var timestamp: Long = 0
)
