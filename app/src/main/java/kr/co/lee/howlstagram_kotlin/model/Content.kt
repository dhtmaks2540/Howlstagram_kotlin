package kr.co.lee.howlstagram_kotlin.model

import android.os.Parcel
import android.os.Parcelable

data class Content(
    var contentDTO: ContentDTO? = null,
    var contentUid: String? = null,
    var profileUrl: String? = null,
    var commentSize: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(ContentDTO::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(contentDTO, flags)
        parcel.writeString(contentUid)
        parcel.writeString(profileUrl)
        parcel.writeInt(commentSize)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Content> {
        override fun createFromParcel(parcel: Parcel): Content {
            return Content(parcel)
        }

        override fun newArray(size: Int): Array<Content?> {
            return arrayOfNulls(size)
        }
    }
}
