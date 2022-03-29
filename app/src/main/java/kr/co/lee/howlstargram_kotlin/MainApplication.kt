package kr.co.lee.howlstargram_kotlin

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Hilt 최상위 컴포넌트 생성
@HiltAndroidApp
class MainApplication : Application()
