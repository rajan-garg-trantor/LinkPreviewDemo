package com.rajangarg.linkpreviewdemo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kedia.ogparser.OpenGraphResult
import com.rajangarg.linkpreviewdemo.databinding.ItemLinkPreviewBinding


class LinksListAdapter : RecyclerView.Adapter<LinksListAdapter.MainViewHolder>() {

    private var linksList = ArrayList<OpenGraphResult>()

    fun setLinksList(linksList: ArrayList<OpenGraphResult>) {
        this.linksList.clear()
        this.linksList.addAll(linksList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLinkPreviewBinding.inflate(inflater, parent, false)
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val link = linksList[position]
        holder.bind(link)
    }

    override fun getItemCount(): Int {
        return linksList.size
    }

    inner class MainViewHolder(private val binding: ItemLinkPreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(openGraphResult: OpenGraphResult) {
            binding.openGraphResult = openGraphResult
            binding.root.setOnClickListener {
                it.context.loadURL(openGraphResult.url)
            }
        }
    }
}