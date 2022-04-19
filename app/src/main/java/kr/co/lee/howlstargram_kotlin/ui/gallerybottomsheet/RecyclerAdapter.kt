package kr.co.lee.howlstargram_kotlin.ui.gallerybottomsheet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.co.lee.howlstargram_kotlin.databinding.ItemAddBottomSheetBinding

class RecyclerAdapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    private val itemList = listOf("사진", "동영상")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemAddBottomSheetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindTo(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size

    inner class ViewHolder(val binding: ItemAddBottomSheetBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.layout.setOnClickListener {
                when(itemList[adapterPosition]) {
                    "사진" -> {
                        println("게시물!!")
                    }
                    "동영상" -> {
                        println("스토리!!!")
                    }
                }
            }
        }

        fun bindTo(typeString: String) {
            binding.apply {
                type = typeString
            }
        }
    }
}