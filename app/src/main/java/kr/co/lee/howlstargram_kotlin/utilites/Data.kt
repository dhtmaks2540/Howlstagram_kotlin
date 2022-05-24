package kr.co.lee.howlstargram_kotlin.utilites

enum class PageType(val pageTitle: String, val pageTag: String) {
    PAGE1("Page1", "DetailFragment"),
    PAGE2("Page2", "GridFragment"),
    PAGE3("Page3", "AddPhoto"),
    PAGE4("Page4", "AlarmFragment"),
    PAGE5("Page5", "UserFragment");
}

enum class ImageType {
    PROFILE_TYPE,
    POST_TYPE;
}