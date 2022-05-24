package kr.co.lee.howlstargram_kotlin.ui.comment

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseActivity
import kr.co.lee.howlstargram_kotlin.databinding.ActivityCommentBinding
import kr.co.lee.howlstargram_kotlin.model.Content
import kr.co.lee.howlstargram_kotlin.ui.main.MainActivity
import javax.inject.Inject

@AndroidEntryPoint
class CommentActivity @Inject constructor(): BaseActivity<ActivityCommentBinding>(R.layout.activity_comment) {

    private val commentViewModel: CommentViewModel by viewModels()
    private lateinit var recyclerAdapter: CommentRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun init() {
        binding.apply {
            viewModel = commentViewModel

            tvUploadComment.setOnClickListener {
                commentViewModel.addComment()
                etCommentMessage.setText("")
            }
        }

        setAdapter()
        getIntentData()
        initToolbar()
        observeLiveData()
    }

    private fun setAdapter() {
        recyclerAdapter = CommentRecyclerAdapter()

        recyclerAdapter.setClickListener(object : CommentRecyclerAdapter.UserClickListener {
            override fun click(userId: String, destinationUid: String, profileUrl: String) {
                val intent = Intent(this@CommentActivity, MainActivity::class.java)
                startActivity(intent)
            }
        })
    }

    private fun observeLiveData() {
        commentViewModel.content.observe(this) {
            commentViewModel.loadComments(contentUid = it.contentUid!!)
        }

        commentViewModel.comments.observe(this) {
            recyclerAdapter.setItems(it)
            binding.recyclerview.adapter = recyclerAdapter
        }

        commentViewModel.comment.observe(this) {
            recyclerAdapter.addItems(it)
        }

        commentViewModel.uid.observe(this) {
            commentViewModel.loadProfileUrl(it)
        }
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun getIntentData() {
        val content = intent.getParcelableExtra<Content>("content")
        commentViewModel.setContent(content = content!!)
    }
}