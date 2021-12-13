package com.example.unsplash_app_tutorial.utils

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.example.unsplash_app_tutorial.App
import com.example.unsplash_app_tutorial.model.SearchData
import com.example.unsplash_app_tutorial.utils.Constants.TAG
import com.google.gson.Gson

object SharedPrefManager {
    private const val SHARED_SEARCH_HISTORY = "shared_search_history"
    private const val KEY_SEARCH_HISTORY = "key_search_history"

    private const val SHARED_SEARCH_HISTORY_MODE = "shared_search_history_mode"
    private const val KEY_SEARCH_HISTORY_MODE = "key_search_history_mode"

    // 검색어 저장 모드 설정
    fun setSearchHistoryMode(isActivated: Boolean) {
        Log.d(TAG, "SharedPrefManager - setSearchHistoryMode called / ")

        val shared = App.instance.getSharedPreferences(SHARED_SEARCH_HISTORY_MODE, Context.MODE_PRIVATE)
//
//        val editor = shared.edit()
//
//        editor.putBoolean(KEY_SEARCH_HISTORY_MODE, isActivated)
//
//        editor.apply()

        shared.edit {
            putBoolean(KEY_SEARCH_HISTORY_MODE, isActivated)
        }

    }

    // 검색어 저장 모드 설정
    fun checkHistoryMode(): Boolean {
        // 쉐어드 가져오기
        val shared = App.instance.getSharedPreferences(SHARED_SEARCH_HISTORY_MODE, Context.MODE_PRIVATE)

        return shared.getBoolean(KEY_SEARCH_HISTORY_MODE, false)
    }

    //검색 목록을 저장
    fun storeSearchHistoryList(searchHistoryList: MutableList<SearchData>): Unit {
        Log.d(TAG, "SharedPrefManager - storeSearchHistoryList called ")

        // 매개변수로 들어온 배열을 -> 문자열로 변환
        val searchHistoryListString: String = Gson().toJson(searchHistoryList)
        Log.d(TAG, "SharedPrefManager - storeSearchHistoryList called / $searchHistoryListString");

        // 쉐어드 가져오기
        val shared = App.instance.getSharedPreferences(SHARED_SEARCH_HISTORY, Context.MODE_PRIVATE)

//        // 쉐어드 에디터 가져오기
//        val editor = shared.edit()
//
//        editor.putString(KEY_SEARCH_HISTORY, searchHistoryListString)
//
//        editor.apply()

        shared.edit {
            putString(KEY_SEARCH_HISTORY, searchHistoryListString)
        }


    }

    //검색 목록 가져오기
    fun getSearchHistoryList(): MutableList<SearchData> {
        // 쉐어드 가져오기
        val shared = App.instance.getSharedPreferences(SHARED_SEARCH_HISTORY, Context.MODE_PRIVATE)

        val storeSearchHistoryListString = shared.getString(KEY_SEARCH_HISTORY, "")!!

        var storedSearchHistoryList = ArrayList<SearchData>()

        // 검색 목록이 값이 있다면
        if (storeSearchHistoryListString.isNotEmpty()) {

            // 저장된 문자열을 Gson을 통해 객체 배열로 변경
            storedSearchHistoryList =
                Gson().fromJson(storeSearchHistoryListString, Array<SearchData>::class.java)
                    .toMutableList() as ArrayList<SearchData>
        }

        return storedSearchHistoryList
    }

    // 검색 목록 지우기
    fun clearSearchHistoryList() {

        // 쉐어드 가져오기
        val shared = App.instance.getSharedPreferences(SHARED_SEARCH_HISTORY, Context.MODE_PRIVATE)

        // 쉐어드 에디터 가져오기
//        val editor = shared.edit()
//
//        // 해당 데이터 지우기
//        editor.clear()
//
//        editor.apply()

        shared.edit {
            this.clear()
        }

    }


}