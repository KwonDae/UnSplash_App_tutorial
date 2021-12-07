package com.example.unsplash_app_tutorial.recyclerview

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.unsplash_app_tutorial.R
import com.example.unsplash_app_tutorial.model.SearchData
import com.example.unsplash_app_tutorial.utils.Constants.TAG

class SearchHistoryRecyclerViewAdapter(searchHistoryRecyclerViewInterface: ISearchHistoryRecyclerView) :
    RecyclerView.Adapter<SearchHistoryItemViewHolder>() {

    private var historyDataList = ArrayList<SearchData>()

    private var iSearchHistoryRecyclerView: ISearchHistoryRecyclerView? = null

    init {
        Log.d(TAG, "SearchHistoryRecyclerViewAdapter -  called / ")
        this.iSearchHistoryRecyclerView = searchHistoryRecyclerViewInterface
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryItemViewHolder {
        return SearchHistoryItemViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.layout_search_history_item, parent, false)
            , this.iSearchHistoryRecyclerView!!
        )
    }

    override fun onBindViewHolder(holder: SearchHistoryItemViewHolder, position: Int) {
        holder.bindWithView(historyData = historyDataList[position])
    }

    override fun getItemCount(): Int {
        return historyDataList.size
    }

    fun submitList(historyDataList: ArrayList<SearchData>) {
        this.historyDataList = historyDataList
    }
}