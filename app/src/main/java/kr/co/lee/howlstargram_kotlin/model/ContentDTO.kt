package kr.co.lee.howlstargram_kotlin.model

import android.os.Parcel
import android.os.Parcelable

data class ContentDTO(
    var explain: String? = null,
    var imageUrl: String? = null,
    var uid: String? = null,
    var userId: String? = null,
    var userNickName: String? = null,
    var userName: String? = null,
    var timestamp: Long = 0,
    var favoriteCount: Int = 0,
    var favorites: MutableMap<String, Boolean> = HashMap(),
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readInt(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(explain)
        parcel.writeString(imageUrl)
        parcel.writeString(uid)
        parcel.writeString(userId)
        parcel.writeString(userNickName)
        parcel.writeString(userName)
        parcel.writeLong(timestamp)
        parcel.writeInt(favoriteCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ContentDTO> {
        override fun createFromParcel(parcel: Parcel): ContentDTO {
            return ContentDTO(parcel)
        }

        override fun newArray(size: Int): Array<ContentDTO?> {
            return arrayOfNulls(size)
        }
    }
}
