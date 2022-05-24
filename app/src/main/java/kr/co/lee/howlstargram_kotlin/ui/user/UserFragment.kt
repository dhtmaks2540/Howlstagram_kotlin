package kr.co.lee.howlstargram_kotlin.ui.user

import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseFragment
import kr.co.lee.howlstargram_kotlin.databinding.FragmentUserBinding
import kr.co.lee.howlstargram_kotlin.ui.gallery.GalleryActivity
import kr.co.lee.howlstargram_kotlin.ui.login.LoginActivity
import kr.co.lee.howlstargram_kotlin.ui.main.MainActivity
import kr.co.lee.howlstargram_kotlin.utilites.ImageType

@AndroidEntryPoint
class UserFragment : BaseFragment<FragmentUserBinding>(R.layout.fragment_user) {
    private val userViewModel: UserViewModel by viewModels()
    private val args by navArgs<UserFragmentArgs>()
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var recyclerAdapter: UserRecyclerAdapter

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
                findNavController().navigateUp()
            }

            R.id.action_add -> {

            }

            R.id.action_menu -> {

            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun init() {
        binding.apply {
            viewModel = userViewModel

            ivAccountProfile.setOnClickListener {
                if(userViewModel.myProfile.value!!) {
                    val intent = Intent(activity, GalleryActivity::class.java)
                    intent.putExtra("imageType", ImageType.PROFILE_TYPE)
                    launcher.launch(intent)
                }
            }

            btnAccountFollowSignout.setOnClickListener {
                if(userViewModel.myProfile.value == true) {
                    userViewModel.logout()
                    activity?.finish()
                    startActivity(Intent(activity, LoginActivity::class.java))
                } else {
                    userViewModel.requestFollow()
                }
            }



            recyclerAdapter = UserRecyclerAdapter(resources.displayMetrics.widthPixels / 3)
        }

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == AppCompatActivity.RESULT_OK) {
                println("SUCCESS!!!")
//                userViewModel.setProfileUrl()
            }
        }

        getArgsData()
        observeLiveData()
        setToolbar()
    }

    private fun setToolbar() {
        val mainActivity = (activity as MainActivity)
        setHasOptionsMenu(true)
        mainActivity.setSupportActionBar(binding.toolbar)
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mainActivity.supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun getArgsData() {
        val userId = args.userId
        val destinationUid = args.destinationUid
        val profileUrl = args.profileUrl
        userViewModel.setIntentData(
            userId = userId,
            uid = destinationUid,
            profileUrl = profileUrl
        )
    }

    private fun observeLiveData() {
        userViewModel.uid.observe(this) {
            if(it == "") {
                userViewModel.loadMyUid()
                return@observe
            }

            if (it == userViewModel.currentUserId.value) userViewModel.setIsMyProfile(true)
            else userViewModel.setIsMyProfile(false)

            if (userViewModel.profileUrl.value == "") userViewModel.loadMyProfileUrl()

            userViewModel.getFollowerAndFollowing(it)
            userViewModel.loadContentDTO()
        }

        userViewModel.contentDTOs.observe(this) {
            recyclerAdapter.setItems(it)
            binding.rcvAccont.adapter = recyclerAdapter
        }

        userViewModel.userDTO.observe(this) {
            if(it.followers.containsKey(userViewModel.uid.value)) {
                binding.tvAccountFollowerCount.text = (it.followerCount - 1).toString()
            } else {
                binding.tvAccountFollowerCount.text = (it.followerCount + 1).toString()
            }
        }
    }
}