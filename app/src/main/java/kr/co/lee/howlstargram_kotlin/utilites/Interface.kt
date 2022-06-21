package kr.co.lee.howlstargram_kotlin.utilites

import kr.co.lee.howlstargram_kotlin.model.Content
import kr.co.lee.howlstargram_kotlin.model.ContentDTO

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