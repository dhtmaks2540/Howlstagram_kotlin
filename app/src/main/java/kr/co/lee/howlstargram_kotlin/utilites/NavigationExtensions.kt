package kr.co.lee.howlstargram_kotlin.utilites

import android.content.Intent
<<<<<<< HEAD
import android.os.Build
=======
>>>>>>> 715bcee962913b23cb924692680a95a81d496ac4
import android.util.Log
import android.util.SparseArray
import androidx.core.util.forEach
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Manages the various graphs needed for a [BottomNavigationView].
 *
 * This sample is a workaround until the Navigation Component supports multiple back stacks.
 */
@Suppress("DEPRECATION")
fun BottomNavigationView.setupWithNavController(
    navGraphIds: List<Int>,
    fragmentManager: FragmentManager,
    containerId: Int,
    intent: Intent
): LiveData<NavController> {

    // Map of tags(Sparse Array : Int 값을 Objects에 매핑시켜주는 안드로이드의 독특한 Map)
    // 원시형 타입을 사용하여 ArrayMap, HashMap 보다 효율성이 뛰어나지만 인덱스 사이에 공간이 존재해 크기가 큼)
<<<<<<< HEAD
    // graphId, tag 
=======
    // graphId, tag
>>>>>>> 715bcee962913b23cb924692680a95a81d496ac4
    val graphIdToTagMap = SparseArray<String>()
    // 선택된 NavController를 나타내는 LiveData
    val selectedNavController = MutableLiveData<NavController>()

    var firstFragmentGraphId = 0

    // 각 NavGraph ID에 대한 NavHostFragment 첫 생성
    navGraphIds.forEachIndexed { index, navGraphId ->
        // Tag 획득
        val fragmentTag = getFragmentTag(index)

        // NavHostFragment 생성 또는 획득
        val navHostFragment = obtainNavHostFragment(
            fragmentManager,
            fragmentTag,
            navGraphId,
            containerId
        )

        // id 획득
        val graphId = navHostFragment.navController.graph.id

        // 첫 번째 navigation graph 설정
        if (index == 0) {
            firstFragmentGraphId = graphId
        }

        // map에 값 저장
        graphIdToTagMap.put(graphId, fragmentTag)

        // 현재 선택된 아이템인지 아닌지에 따라서 NavHostFragment를 Attach하거나 Detach
        if (this.selectedItemId == graphId) {
            // 현재 선택된 NavController 설정
            selectedNavController.value = navHostFragment.navController
            attachNavHostFragment(fragmentManager, navHostFragment, index == 0)
        } else {
            detachNavHostFragment(fragmentManager, navHostFragment)
        }
    }

    // 선택된 Fragment Tag
    var selectedItemTag = graphIdToTagMap[this.selectedItemId]
    // 첫 번째 Fragment Tag
    val firstFragmentTag = graphIdToTagMap[firstFragmentGraphId]
    // 첫 번째 Fragment인지 확인하는 변수
    var isOnFirstFragment = selectedItemTag == firstFragmentTag

    // Navigation Item 선택 리스너(뒤로가기, 선택 모두)
    setOnNavigationItemSelectedListener { item ->
        // 이미 Fragment Manager의 state가 저장된 상태라면 패스
        if (fragmentManager.isStateSaved) {
            false
        } else {
            // 새롭게 선택된 Fragment Tag
            val newlySelectedItemTag = graphIdToTagMap[item.itemId]
            Log.d("NAVIGATION EXTENSION", "ItemSelected : $item, selectedItemTag : ${selectedItemTag}, newlySelectedItemTag : ${newlySelectedItemTag}")
            // 새롭게 선택된 Fragment Tag라면
            if (selectedItemTag != newlySelectedItemTag) {
                // FirstFragment 백스택에서 제거
                fragmentManager.popBackStack(firstFragmentTag,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE)
                // 선택된 NavHostFragment
                val selectedFragment = fragmentManager.findFragmentByTag(newlySelectedItemTag)
                        as NavHostFragment

                // 현재 선택된 Fragment Tag가 첫 번째 Fragment Tag가 아니라면 -> 첫 번째 Fragment는 항상 백스택에 저장
                if (firstFragmentTag != newlySelectedItemTag) {
                    // 현재 선택된 Fragment attach 및 현재 FragmentManager의 기본 탐색 Fragment로 설정
                    fragmentManager.beginTransaction()
                        .setCustomAnimations(
                            androidx.navigation.ui.R.anim.nav_default_enter_anim,
                            androidx.navigation.ui.R.anim.nav_default_exit_anim,
                            androidx.navigation.ui.R.anim.nav_default_pop_enter_anim,
                            androidx.navigation.ui.R.anim.nav_default_pop_exit_anim)
                        .attach(selectedFragment)
                        .setPrimaryNavigationFragment(selectedFragment)
                        .apply {
                            // 현재 선택된 Fragment 제외 나머지 Fragment Detach
                            graphIdToTagMap.forEach { _, fragmentTagIter ->
                                if (fragmentTagIter != newlySelectedItemTag) {
                                    detach(fragmentManager.findFragmentByTag(firstFragmentTag)!!)
                                }
                            }
                        }
                        .addToBackStack(firstFragmentTag) // 첫 번째 Fragment 백스택에 저장
                        .setReorderingAllowed(true)
                        .commit()
                }
                // 선택된 Fragment에 따른 변수변경
                selectedItemTag = newlySelectedItemTag
                isOnFirstFragment = selectedItemTag == firstFragmentTag
                selectedNavController.value = selectedFragment.navController
                true
            } else {
                false
            }
        }
    }

    // 재선택 리스너(현재 그래프의 시작 destination으로 이동)
    setupItemReselected(graphIdToTagMap, fragmentManager)

    // Handle deep link
    setupDeepLinks(navGraphIds, fragmentManager, containerId, intent)

    // Finally, ensure that we update our BottomNavigationView when the back stack changes
    // BackStack 변경 리스너
    fragmentManager.addOnBackStackChangedListener {
        // 첫 번째 Fragment가 아니고 첫 번째 Fragment가 BackStack에 존재하지 않는다면 BottomNavigationView 첫 번째 Fragment로 변경
        if (!isOnFirstFragment && !fragmentManager.isOnBackStack(firstFragmentTag)) {
            this.selectedItemId = firstFragmentGraphId
        }

        // 현재 destination이 유효하지 않은 경우 graph 재설정 (happens when the back stack is popped after using the back button).
        selectedNavController.value?.let { controller ->
            if (controller.currentDestination == null) {
                controller.navigate(controller.graph.id)
            }
        }
    }
    return selectedNavController
}

// DeepLink 설정 메서드
private fun BottomNavigationView.setupDeepLinks(
    navGraphIds: List<Int>,
    fragmentManager: FragmentManager,
    containerId: Int,
    intent: Intent
) {
    navGraphIds.forEachIndexed { index, navGraphId ->
        val fragmentTag = getFragmentTag(index)

        // NavHostFragment 찾기 또는 생성
        val navHostFragment = obtainNavHostFragment(
            fragmentManager,
            fragmentTag,
            navGraphId,
            containerId
        )
        // Handle Intent
        if (navHostFragment.navController.handleDeepLink(intent)
            && selectedItemId != navHostFragment.navController.graph.id) {
            this.selectedItemId = navHostFragment.navController.graph.id
        }
    }
}

// 같은 아이템 재선택 리스너(시작 destination으로 이동)
@Suppress("DEPRECATION")
private fun BottomNavigationView.setupItemReselected(
    graphIdToTagMap: SparseArray<String>,
    fragmentManager: FragmentManager
) {
    setOnNavigationItemReselectedListener { item ->
        val newlySelectedItemTag = graphIdToTagMap[item.itemId]
        val selectedFragment = fragmentManager.findFragmentByTag(newlySelectedItemTag)
                as NavHostFragment
        val navController = selectedFragment.navController
        navController.popBackStack(
            navController.graph.startDestination, false
        )
    }
}

// 현재 UI에서 NavHostFragment Detach
private fun detachNavHostFragment(
    fragmentManager: FragmentManager,
    navHostFragment: NavHostFragment
) {
    fragmentManager.beginTransaction()
        .detach(navHostFragment)
        .commitNow()
}

// 현재 UI에서 NavHostFragment Attach
private fun attachNavHostFragment(
    fragmentManager: FragmentManager,
    navHostFragment: NavHostFragment,
    isPrimaryNavFragment: Boolean
) {
    fragmentManager.beginTransaction()
        .attach(navHostFragment)
        .apply {
            // 첫 번째 NavHostFragment라면 주요 Navigation Fragment로 설정
            if (isPrimaryNavFragment) {
                setPrimaryNavigationFragment(navHostFragment)
            }
        }
        .commitNow()

}

// NavHostFragment 획득
private fun obtainNavHostFragment(
    fragmentManager: FragmentManager,
    fragmentTag: String,
    navGraphId: Int,
    containerId: Int
): NavHostFragment {
    // NavHostFragment를 획득한 후 존재한다면(null이 아니라면) return
    val existingFragment = fragmentManager.findFragmentByTag(fragmentTag) as NavHostFragment?
    existingFragment?.let { return it }

    // NavHostFragment가 없다면 생성 후 반환
    val navHostFragment = NavHostFragment.create(navGraphId)
    fragmentManager.beginTransaction()
        .add(containerId, navHostFragment, fragmentTag)
        .commitNow()
    return navHostFragment
}

// 현재 백스택에 있는지 체크하는 메서드
private fun FragmentManager.isOnBackStack(backStackName: String): Boolean {
    val backStackCount = backStackEntryCount
    for (index in 0 until backStackCount) {
        if (getBackStackEntryAt(index).name == backStackName) {
            return true
        }
    }
    return false
}

// Fragment Tag 획득
private fun getFragmentTag(index: Int) = "bottomNavigation#$index"