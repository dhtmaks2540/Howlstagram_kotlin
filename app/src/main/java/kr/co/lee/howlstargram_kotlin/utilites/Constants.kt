package kr.co.lee.howlstargram_kotlin.utilites

import android.view.View
import android.view.ViewGroup

const val DATABASE_NAME = "instagram-db"
const val PROFILE_URL = "profileUrl"
const val URI = "uri"
const val IMAGE_TYPE = "imageType"
const val USER_NAME = "userName"
const val USER_NICKNAME = "userNickName"
const val FOLLOWER = "follower"
const val FOLLOWING = "following"
const val TAB_TYPE = "tabType"
const val DESTINATION_UID = "destinationUid"
const val FOLLOW = "follow"
const val CONTENT = "content"
const val FAVORITES = "favorites"
const val CONTENT_DTO = "contentDTO"
const val CONTENT_UID = "contentUid"
const val USER = "user"
const val READ_EXTERNAL_STORAGE_PERMISSION = 10

// BottomNavigationView와 연결된 프래그먼트
enum class FragmentType(val fragmentTitle: String, val fragmentTag: String) {
    FRAGMENT1("fragment1", "DetailFragment"),
    FRAGMENT2("fragment2", "GridFragment"),
    FRAGMENT3("fragment3", "AlarmFragment"),
    FRAGMENT4("fragment4", "UserFragment");
}

enum class ImageType {
    PROFILE_TYPE,
    POST_TYPE;
}

enum class GalleryImageType(val title: String) {
    PHOTO("사진"),
    STORY("스토리");
}

enum class TabType {
    FOLLOWER_TAB,
    FOLLOWING_TAB
}

// View Enabled
fun View.forEachChildView(closure: (View) -> Unit) {
    closure(this)
    val groupView = this as? ViewGroup ?: return
    val size = groupView.childCount - 1
    for (i in 0..size) {
        groupView.getChildAt(i).forEachChildView(closure)
    }
}
