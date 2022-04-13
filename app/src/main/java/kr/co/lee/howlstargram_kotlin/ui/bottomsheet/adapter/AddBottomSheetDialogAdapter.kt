package kr.co.lee.howlstargram_kotlin.ui.bottomsheet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.co.lee.howlstargram_kotlin.databinding.ItemAddBottomSheetBinding

class AddBottomSheetDialogAdapter :
    RecyclerView.Adapter<AddBottomSheetDialogAdapter.AddBottomViewHolder>() {

    private val itemList = listOf("게시물", "스토리")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddBottomViewHolder {
        val binding =
            ItemAddBottomSheetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddBottomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddBottomViewHolder, position: Int) {
        holder.bindTo(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size

    inner class AddBottomViewHolder(val binding: ItemAddBottomSheetBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindTo(typeString: String) {
            binding.apply {
                type = typeString
            }
        }
    }
}