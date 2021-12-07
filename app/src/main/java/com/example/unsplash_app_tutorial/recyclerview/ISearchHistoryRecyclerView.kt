package com.example.unsplash_app_tutorial.recyclerview

interface ISearchHistoryRecyclerView {

    // 검색 아이템 삭제 버튼 클릭
    fun onSearchItemDeleteBtnClicked(position: Int)

    // 검색 아이템 클릭
    fun onSearchItemClicked(position: Int)
}