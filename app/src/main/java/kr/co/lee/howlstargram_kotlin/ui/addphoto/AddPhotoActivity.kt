package kr.co.lee.howlstargram_kotlin.ui.addphoto

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseActivity
import kr.co.lee.howlstargram_kotlin.databinding.ActivityAddPhotoBinding
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity: BaseActivity<ActivityAddPhotoBinding>(R.layout.activity_add_photo) {

    private val addViewModel by viewModels<AddPhotoViewModel>()

    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore

    var photoUri: Uri? = null
    private val launcher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == RESULT_OK) {
                // This is path to the selected image
                photoUri = it.data?.data
                binding.ivAddphoto.setImageURI(photoUri)
            } else {
                // 사진 선택없이 나간다면 Activity 종료
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            viewModel = addViewModel

            // Image Upload 이벤트
            btnAddphotoUpload.setOnClickListener {
                contentUpload()
            }
        }

        init()
        observeLiveDate()
    }

    private fun init() {
        // FirebaseStorage 초기화
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()

        // 앨범 열기
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        launcher.launch(photoPickerIntent)
    }

    private fun observeLiveDate() {
        addViewModel.contentDTO.observe(this) {
            fireStore.collection("images").document().set(addViewModel.contentDTO.value!!)

            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    // 이미지 업로드 메소드
    private fun contentUpload() {
        // 파일 이름 생성
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "IMAGE_${timeStamp}_.png"

        val storageRef = storage.reference.child("images").child(imageFileName)

        photoUri?.let {
            // Callback Method
            storageRef.putFile(it).addOnSuccessListener {
                storageRef.downloadUrl.addOnCompleteListener { uri ->
                    addViewModel.setContentDTO(uri.toString(), auth.currentUser?.uid,
                        auth.currentUser?.email, addViewModel.description.value.toString())
                }
            }
        }
    }
}