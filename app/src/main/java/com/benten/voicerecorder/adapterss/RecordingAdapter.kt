package com.benten.voicerecorder.adapterss

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.benten.voicerecorder.databinding.ItemRecordingBinding

class RecordingAdapter : RecyclerView.Adapter<RecordingAdapter.RecordingViewHolder>() {

    private val fileList = mutableListOf<String>()

    private var itemClickListener: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordingViewHolder {
        return RecordingViewHolder(
            ItemRecordingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    fun setItemClickListener(listener: ((String) -> Unit)) {
        itemClickListener = listener
    }

    override fun onBindViewHolder(holder: RecordingViewHolder, position: Int) {
        holder.bind(fileList[position], position)
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    fun updateAll(files: List<String>) {
        this.fileList.clear()
        fileList.addAll(files)
        notifyDataSetChanged()
    }

    inner class RecordingViewHolder(val binding: ItemRecordingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String, position: Int) {
            binding.tvRecordingName.text = item
            binding.root.setOnClickListener {
                itemClickListener?.invoke(item)
            }

        }
    }
}