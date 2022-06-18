package kr.co.lee.howlstargram_kotlin.utilites

import kr.co.lee.howlstargram_kotlin.model.Content
import kr.co.lee.howlstargram_kotlin.model.ContentDTO

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

// 좋아요 이벤트 인터페이스
interface FavoriteClickListener {
    fun click(contentUid: String, position: Int)
}

// 댓글 인터페이스
interface CommentClickListener {
    fun click(content: Content)
}

// 프로필 인터페이스
interface ProfileClickListener {
    fun click(destinationUid: String)
}

// 좋아요 인터페이스
interface LikeClickListener {
    fun click(favorites: Map<String, Boolean>)
}

// 팔로우 인터페이스
interface FollowClickListener {
    fun followClick(userUid: String, position: Int)
}

// 이미지 추가 인터페이스
interface BottomSheetClickListener {
    fun click(itemType: GalleryImageType)
}

// 이미지 클릭 인터페이스
interface ImageClickListener {
    fun click(contentDTO: ContentDTO?, contentUid: String?)
}