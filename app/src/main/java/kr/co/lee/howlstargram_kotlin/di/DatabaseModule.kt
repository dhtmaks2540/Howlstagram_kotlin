//package kr.co.lee.howlstargram_kotlin.di
//
//import android.content.Context
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import kr.co.lee.howlstargram_kotlin.model.AppDatabase
//import kr.co.lee.howlstargram_kotlin.model.SearchDAO
//import javax.inject.Singleton
//
//@InstallIn(SingletonComponent::class)
//@Module
//object DatabaseModule {
//
//    @Singleton
//    @Provides
//    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
//        return AppDatabase.getInstance(context)
//    }
//
//    @Provides
//    fun provideSearchDao(appDatabase: AppDatabase): SearchDAO {
//        return appDatabase.searchDao()
//    }
//}
