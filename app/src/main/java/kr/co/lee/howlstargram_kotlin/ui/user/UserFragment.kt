package kr.co.lee.howlstargram_kotlin.ui.user

import android.app.Activity
import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseFragment
import kr.co.lee.howlstargram_kotlin.databinding.FragmentUserBinding
import kr.co.lee.howlstargram_kotlin.ui.gallery.GalleryActivity
import kr.co.lee.howlstargram_kotlin.ui.login.LoginActivity
import kr.co.lee.howlstargram_kotlin.ui.main.MainActivity
import kr.co.lee.howlstargram_kotlin.ui.profile.ProfileEditActivity
import kr.co.lee.howlstargram_kotlin.utilites.*

@AndroidEntryPoint
class UserFragment : BaseFragment<FragmentUserBinding>(R.layout.fragment_user) {
    private val userViewModel: UserViewModel by viewModels()

    private lateinit var profileLauncher: ActivityResultLauncher<Intent>
    private lateinit var profileEditLauncher: ActivityResultLauncher<Intent>
    private lateinit var recyclerAdapter: UserRecyclerAdapter
    private lateinit var navController: NavController

    override fun initView() {
        init()
    }

    // 메뉴 초기화
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.user_menu, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                navController.navigateUp()
            }

            R.id.action_add -> {
                navController.navigate(R.id.action_to_bottom)
            }

            R.id.action_logout -> {
                userViewModel.logout()
                startActivity(Intent(activity, LoginActivity::class.java))
                activity?.finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun init() {
        navController = findNavController()

        binding.apply {
            viewModel = userViewModel

            // 프로필 이미지 클릭 이벤트
            ivAccountProfile.setOnClickListener {
                // 내 프로필이라면 -> 프로필 이미지 수정
                if(userViewModel.uid.value == userViewModel.currentUserId) {
                    val intent = Intent(activity, GalleryActivity::class.java)
                    intent.putExtra(IMAGE_TYPE, ImageType.PROFILE_TYPE)
                    profileLauncher.launch(intent)
                }
            }

            // Follow 버튼 클릭 
            btnAccountFollowSignout.setOnClickListener {
                // 내 프로필이라면 -> 프로필 수정
                if(userViewModel.uid.value == userViewModel.currentUserId) {
                    val intent = Intent(activity, ProfileEditActivity::class.java)
                    intent.putExtra(PROFILE_URL, userViewModel.user.value?.profileUrl)
                    intent.putExtra(USER_NAME, userViewModel.user.value?.userDTO?.userName)
                    intent.putExtra(USER_NICKNAME, userViewModel.user.value?.userDTO?.userNickName)
                    profileEditLauncher.launch(intent)
                } else { // 상대방 프로필이라면 -> 팔로우 요청
                    lifecycleScope.launch {
                        setUiFalse(false)

                        val job = userViewModel.requestFollow()
                        job.join()

                        setUiFalse(true)
                    }
                }
            }

            // 팔로워 버튼 클릭
            layoutFollower.setOnClickListener {
                val bundle = bundleOf(
                    USER_NICKNAME to userViewModel.user.value?.userDTO?.userNickName,
                    FOLLOWER to userViewModel.user.value?.userDTO?.followers,
                    FOLLOWING to userViewModel.user.value?.userDTO?.followings,
                    TAB_TYPE  to TabType.FOLLOWER_TAB
                )

                // 팔로워로 이동
                navController.navigate(R.id.action_to_follow, bundle)
            }

            // 팔로잉 버튼 클릭
            layoutFollowing.setOnClickListener {
                val bundle = bundleOf(
                    USER_NICKNAME to userViewModel.user.value?.userDTO?.userNickName,
                    FOLLOWER to userViewModel.user.value?.userDTO?.followers,
                    FOLLOWING to userViewModel.user.value?.userDTO?.followings,
                    TAB_TYPE  to TabType.FOLLOWING_TAB
                )

                // 팔로잉으로 이동
                navController.navigate(R.id.action_to_follow, bundle)
            }

            // 새로고침
            refreshLayout.setOnRefreshListener {
                lifecycleScope.launch {
                    setUiFalse(false)
                    val job = userViewModel.loadUser()
                    job.join()
                    refreshLayout.isRefreshing = false
                    setUiFalse(true)
                }
            }

            // RecyclerView Adapter 초기화
            recyclerAdapter = UserRecyclerAdapter(resources.displayMetrics.widthPixels / 3)
        }

        // Launcher 초기화
        profileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK) {
                val profileUrl = it.data?.getStringExtra(PROFILE_URL)
                userViewModel.updateProfileUrl(profileUrl)
            }
        }

        profileEditLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK) {
                userViewModel.loadUser()
            }
        }

        getBundleData()
        observeLiveData()
        setToolbar(true)
        setUiFalse(false)
    }

    // 툴바 초기화
    private fun setToolbar(isShowHome: Boolean) {
        val mainActivity = (activity as MainActivity)
        setHasOptionsMenu(!isShowHome)
        mainActivity.setSupportActionBar(binding.toolbar)
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(isShowHome)
        mainActivity.supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    // Bundle 데이터
    private fun getBundleData() {
        val destinationUid = arguments?.getString(DESTINATION_UID)
        userViewModel.setIntentData(uid = destinationUid)
    }

    // 팔로우, 팔로워 버튼 Clickable
    private fun setUiFalse(boolean: Boolean) {
        binding.apply {
            tvAccountFollowerCount.isClickable = boolean
            tvAccountFollowingCount.isClickable = boolean
            btnAccountFollowSignout.isClickable = boolean
        }
    }

    // LiveData Observe
    private fun observeLiveData() {
        userViewModel.uid.observe(this) {
            // 내 아이디라면
            if(it == "") {
                userViewModel.loadMyUid()
                setToolbar(false)
                return@observe
            }

            userViewModel.loadUser()
            binding.isMyProfile = it == userViewModel.currentUserId
            setUiFalse(true)
        }

        userViewModel.contentDTOs.observe(this) {
            recyclerAdapter.setItems(it)
            binding.rcvAccont.adapter = recyclerAdapter
        }

        userViewModel.userDTO.observe(this) {
            setUiFalse(true)
        }
    }
}