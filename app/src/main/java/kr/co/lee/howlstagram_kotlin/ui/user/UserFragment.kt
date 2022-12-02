package kr.co.lee.howlstagram_kotlin.ui.user

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
import kr.co.lee.howlstagram_kotlin.R
import kr.co.lee.howlstagram_kotlin.base.BaseFragment
import kr.co.lee.howlstagram_kotlin.databinding.FragmentUserBinding
import kr.co.lee.howlstagram_kotlin.ui.gallery.GalleryActivity
import kr.co.lee.howlstagram_kotlin.ui.grid.GridItemDecoration
import kr.co.lee.howlstagram_kotlin.ui.login.LoginActivity
import kr.co.lee.howlstagram_kotlin.ui.main.MainActivity
import kr.co.lee.howlstagram_kotlin.ui.profile.ProfileEditActivity
import kr.co.lee.howlstagram_kotlin.utilites.*

@AndroidEntryPoint
class UserFragment : BaseFragment<FragmentUserBinding>(R.layout.fragment_user) {
    private val viewModel: UserViewModel by viewModels()
    private val recyclerAdapter: UserRecyclerAdapter by lazy {
        UserRecyclerAdapter(
            resources.displayMetrics.widthPixels / 3
        )
    }
    private val userItemDecoration: GridItemDecoration by lazy {
        GridItemDecoration()
    }

    private lateinit var profileLauncher: ActivityResultLauncher<Intent>
    private lateinit var profileEditLauncher: ActivityResultLauncher<Intent>
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
                viewModel.logout()
                startActivity(Intent(activity, LoginActivity::class.java))
                activity?.finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun init() {
        navController = findNavController()

        binding.apply {
            vm = viewModel
            adapter = recyclerAdapter
            handler = this@UserFragment
            itemDecoration = userItemDecoration

            // 새로고침
            refreshLayout.setOnRefreshListener {
                lifecycleScope.launch {
                    binding.layoutRoot.forEachChildView { it.isEnabled = false }
                    val job = viewModel.refresh()
                    job.join()
                    refreshLayout.isRefreshing = false
                    binding.layoutRoot.forEachChildView { it.isEnabled = true }
                }
            }

            // Launcher 초기화
            profileLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    if (it.resultCode == Activity.RESULT_OK) {
                        viewModel.refresh()
                    }
                }

            profileEditLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    if (it.resultCode == Activity.RESULT_OK) {
                        viewModel.refresh()
                    }
                }

            observeLiveData()
        }
    }

    // 툴바 초기화
    private fun setToolbar(isShowHome: Boolean) {
        val mainActivity = (activity as MainActivity)
        setHasOptionsMenu(!isShowHome)
        mainActivity.setSupportActionBar(binding.toolbar)
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(isShowHome)
        mainActivity.supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    // LiveData Observe
    private fun observeLiveData() {
        viewModel.uid.observe(viewLifecycleOwner) {
            if (it != "") {
                binding.isMyProfile = it == viewModel.currentUserId
            }
        }

        viewModel.userAndContent.observe(viewLifecycleOwner) {
            recyclerAdapter.submitList(it.contents)
        }

        viewModel.isMyProfile.observe(viewLifecycleOwner) {
            setToolbar(it)
        }
    }

    // 팔로잉  클릭
    fun followingClickListener() {
        val bundle = bundleOf(
            USER_NICKNAME to viewModel.userAndContent.value?.user?.userDTO?.userNickName,
            FOLLOWER to viewModel.userAndContent.value?.user?.userDTO?.followers,
            FOLLOWING to viewModel.userAndContent.value?.user?.userDTO?.followings,
            TAB_TYPE to TabType.FOLLOWING_TAB
        )

        // 팔로잉으로 이동
        navController.navigate(R.id.action_to_follow, bundle)
    }

    // 팔로워 클릭
    fun followerClickListener() {
        val bundle = bundleOf(
            USER_NICKNAME to viewModel.userAndContent.value?.user?.userDTO?.userNickName,
            FOLLOWER to viewModel.userAndContent.value?.user?.userDTO?.followers,
            FOLLOWING to viewModel.userAndContent.value?.user?.userDTO?.followings,
            TAB_TYPE to TabType.FOLLOWER_TAB
        )

        // 팔로워로 이동
        navController.navigate(R.id.action_to_follow, bundle)
    }

    // 팔로우 클릭
    fun followClickListener() {
        // 내 프로필이라면 -> 프로필 수정
        if (viewModel.uid.value == viewModel.currentUserId) {
            val intent = Intent(activity, ProfileEditActivity::class.java)
            intent.putExtra(PROFILE_URL, viewModel.userAndContent.value?.user?.profileUrl)
            intent.putExtra(
                USER_NAME,
                viewModel.userAndContent.value?.user?.userDTO?.userName
            )
            intent.putExtra(
                USER_NICKNAME,
                viewModel.userAndContent.value?.user?.userDTO?.userNickName
            )
            profileEditLauncher.launch(intent)
        } else { // 상대방 프로필이라면 -> 팔로우 요청
            lifecycleScope.launch {
                viewModel.requestFollow()
            }
        }
    }

    // 프로필 이미지 클릭
    fun profileClickListener() {
        // 내 프로필이라면 -> 프로필 이미지 수정
        if (viewModel.uid.value == viewModel.currentUserId) {
            val intent = Intent(activity, GalleryActivity::class.java)
            intent.putExtra(IMAGE_TYPE, ImageType.PROFILE_TYPE)
            profileLauncher.launch(intent)
        }
    }
}