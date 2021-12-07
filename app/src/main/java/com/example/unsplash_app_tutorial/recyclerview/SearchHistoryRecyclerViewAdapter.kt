package com.example.unsplash_app_tutorial.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.unsplash_app_tutorial.R
import com.example.unsplash_app_tutorial.model.SearchData

class SearchHistoryRecyclerViewAdapter : RecyclerView.Adapter<SearchHistoryItemViewHolder>() {

    private var historyDataList = ArrayList<SearchData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryItemViewHolder {
        return SearchHistoryItemViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.layout_search_history_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SearchHistoryItemViewHolder, position: Int) {
        holder.bindWithView(historyData = historyDataList[position])
    }

    override fun getItemCount(): Int {
        return historyDataList.size
    }

    fun submitList(historyDataList : ArrayList<SearchData>) {
        this.historyDataList = historyDataList
    }
}