package kr.co.lee.howlstargram_kotlin.ui.detail

import com.google.firebase.auth.FirebaseAuth
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseFragment
import kr.co.lee.howlstargram_kotlin.databinding.FragmentDetailBinding

class DetailViewFragment: BaseFragment<FragmentDetailBinding>(R.layout.fragment_detail) {

    lateinit var uid: String

    override fun initView() {
        binding.apply {
        }

        uid = FirebaseAuth.getInstance().currentUser?.uid!!
    }

}