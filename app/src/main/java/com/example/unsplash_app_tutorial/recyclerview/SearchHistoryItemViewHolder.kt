package com.example.unsplash_app_tutorial.recyclerview

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.unsplash_app_tutorial.model.SearchData
import com.example.unsplash_app_tutorial.utils.Constants.TAG
import kotlinx.android.synthetic.main.layout_search_history_item.view.*
import java.text.SimpleDateFormat

class SearchHistoryItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
                                                    View.OnClickListener {


    private val titleTextView = itemView.search_history_title_text_view
    private val timeTextView = itemView.search_history_time_text_view
    private val deleteSearchBtn = itemView.close_image_view
    private val constraintSearchItem = itemView.constraint_search_item

    init {
        //this = View.OnClickListener 나 자신이다
        deleteSearchBtn.setOnClickListener(this)
        constraintSearchItem.setOnClickListener(this)
    }

    // 데이터와 뷰를 묶는다
    fun bindWithView(historyData: SearchData) {
        Log.d(TAG, "SearchHistoryItemViewHolder - bindWithView called / ");

        titleTextView.text = historyData.term
        timeTextView.text = historyData.timestamp
//        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//        val formatter = SimpleDateFormat("HH:mm:ss")
//        timeTextView.text = formatter.format(parser.parse(historyData.timestamp))
    }

    override fun onClick(v: View?) {
        Log.d(TAG, "SearchHistoryItemViewHolder - onClick called / ");
        when(v) {
            deleteSearchBtn -> {
                Log.d(TAG, "SearchHistoryItemViewHolder - onClick called / 검색 삭제 버튼 클릭 ");
            }
            constraintSearchItem -> {
                Log.d(TAG, "SearchHistoryItemViewHolder - onClick called / 검색 아이템 클릭");
            }
        }
    }
}