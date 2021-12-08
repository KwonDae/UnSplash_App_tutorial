package com.example.unsplash_app_tutorial.activities

import android.app.SearchManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unsplash_app_tutorial.R
import com.example.unsplash_app_tutorial.model.Photo
import com.example.unsplash_app_tutorial.model.SearchData
import com.example.unsplash_app_tutorial.recyclerview.ISearchHistoryRecyclerView
import com.example.unsplash_app_tutorial.recyclerview.PhotoGridRecyclerViewAdapter
import com.example.unsplash_app_tutorial.recyclerview.SearchHistoryRecyclerViewAdapter
import com.example.unsplash_app_tutorial.retrofit.RetrofitManager
import com.example.unsplash_app_tutorial.utils.Constants.TAG
import com.example.unsplash_app_tutorial.utils.RESPONSE_STATUS
import com.example.unsplash_app_tutorial.utils.SharedPrefManager
import com.example.unsplash_app_tutorial.utils.textChangesToFlow
import com.example.unsplash_app_tutorial.utils.toStrings
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_photo_collection.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class PhotoCollectionActivity : AppCompatActivity(),
    SearchView.OnQueryTextListener,
    CompoundButton.OnCheckedChangeListener,
    View.OnClickListener,
    ISearchHistoryRecyclerView {

    // 데이터
    private var photoList = ArrayList<Photo>()

    // 검색 기록 배열
    private var searchHistoryList = ArrayList<SearchData>()

    // 어답터
    private lateinit var photoGridRecyclerViewAdapter: PhotoGridRecyclerViewAdapter

    private lateinit var searchHistoryRecyclerViewAdapter: SearchHistoryRecyclerViewAdapter

    // 서치뷰
    private lateinit var mySearchView: SearchView

    // 서치뷰 에딧 텍스트
    private lateinit var mySearchViewEditText: EditText

    // 옵저버블 통합 제거를 위한 CompositeDisposable
//    private val myCompositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_collection)

        val bundle = intent.getBundleExtra("array_bundle")
        val searchTerm = intent.getStringExtra("search_term")

        Log.d(
            TAG,
            "PhotoCollectionActivity - onCreate called / searchTerm : $searchTerm, photoList.count() : ${photoList.size}"
        )

        search_history_mode_switch.setOnCheckedChangeListener(this)
        clear_search_history_button.setOnClickListener(this)

        top_app_bar.title = searchTerm

        // 액티비티에서 어떤 액션바를 사용할지 설정한다.
        setSupportActionBar(top_app_bar)

        photoList = bundle?.getSerializable("photo_array_list") as ArrayList<Photo>

        // 사진 리사이클러뷰 세팅
        this.photoCollectionRecyclerViewSetting(this.photoList)

        this.searchHistoryList = SharedPrefManager.getSearchHistoryList() as ArrayList<SearchData>

        this.searchHistoryList.forEach {
            Log.d(TAG, "저장된 검색 기록 - it.term : ${it.term}, it.timestamp : ${it.timestamp}");
        }

        search_history_mode_switch.isChecked = SharedPrefManager.checkHistoryMode()

        handleSearchViewUi()

        // 감섹 리사이클러뷰 세팅
        this.searchHistoryRecyclerViewSetting(this.searchHistoryList)

        if (searchTerm?.isNotEmpty()!!) {
            val term = searchTerm?.let {
                it
            } ?: ""
            this.insertSearchTermHistory(term)
        }

    }// onCreate

    override fun onDestroy() {
        /*
        Rx적용부분
         */
//        this.myCompositeDisposable.clear()
        super.onDestroy()
    }

    private fun searchHistoryRecyclerViewSetting(searchHistoryList: ArrayList<SearchData>) {
        Log.d(TAG, "PhotoCollectionActivity - searchHistoryRecyclerViewSetting called / ")

        //어댑터 준비
        this.searchHistoryRecyclerViewAdapter = SearchHistoryRecyclerViewAdapter(this)
        this.searchHistoryRecyclerViewAdapter.submitList(searchHistoryList)

        val myLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        myLinearLayoutManager.stackFromEnd = true

        search_history_recycler_view.apply {
            this.layoutManager = myLinearLayoutManager
            this.scrollToPosition(searchHistoryRecyclerViewAdapter.itemCount - 1)
            adapter = searchHistoryRecyclerViewAdapter
        }

    }

    // 그리드 사진 RecyclerView Setting
    private fun photoCollectionRecyclerViewSetting(photoList: ArrayList<Photo>) {
        Log.d(TAG, "PhotoCollectionActivity - photoCollectionRecyclerViewSetting called / ");

        this.photoGridRecyclerViewAdapter = PhotoGridRecyclerViewAdapter(null)

        this.photoGridRecyclerViewAdapter.submitList(photoList)

        my_photo_recycler_view.layoutManager =
            GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        my_photo_recycler_view.adapter = this.photoGridRecyclerViewAdapter

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.d(TAG, "PhotoCollectionActivity - onCreateOptionsMenu called / ");
        val inflater = menuInflater
        inflater.inflate(R.menu.top_app_bar_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        this.mySearchView = menu?.findItem(R.id.search_menu_item)?.actionView as SearchView

        this.mySearchView.apply {
            this.queryHint = "검색어를 입력해주세요"

            this.setOnQueryTextListener(this@PhotoCollectionActivity)

            this.setOnQueryTextFocusChangeListener { _, hasExpaned ->
                when (hasExpaned) {
                    true -> {
                        Log.d(TAG, "서치뷰 열림");
//                        linear_search_history_view.visibility = View.VISIBLE

                        handleSearchViewUi()
                    }
                    false -> {
                        Log.d(TAG, "서치뷰 닫힘")
                        linear_search_history_view.visibility = View.INVISIBLE
                    }
                }
            }
            // 서치뷰에서 EditText를 가져온다
            mySearchViewEditText = this.findViewById(androidx.appcompat.R.id.search_src_text)

            /* Rx 적용
            /**
             * RxJava debounce
             */
            // SearchView EditText 옵저버블 만들기
            val editTextChangeObservable = mySearchViewEditText.textChanges()

            val searchEditTextSubscription: Disposable =
                // 옵저버블 연산자 추가
                editTextChangeObservable
                    // 글자가 입력되고 나서 0.8초 후에 onNext 이벤트로 데이터 흘러보내기
                    .debounce(1000, TimeUnit.MILLISECONDS)
                    // IO 쓰레드에서 돌리겠다.
                    // Scheduler instance intended for IO-bound work.
                    // 네트워크 요청, 파일 읽기, 쓰기, 디비처리 등
                    .subscribeOn(Schedulers.io())
                    // 구독을 통해 이벤트 응답 받기
                    .subscribeBy(
                        onNext= {
                            Log.d("RX", "onNext : $it")
                            if(it.isNotEmpty()) {
                                searchPhotoApiCall(it.toString())
                            }
                        },
                        onComplete = {
                            Log.d("RX", "onComplete")
                        },
                        onError = {
                            Log.d("RX", "onError : ${it.message}")
                        }
                    )

            myCompositeDisposable.add(searchEditTextSubscription)

             */

            /*
            Coroutine
            Rx의 스케줄러와 비슷
            IO 스레드에서 돌리겠다
             */
            GlobalScope.launch(context = Dispatchers.IO) {

                // editText가 변경 되었을 때
                val editTextFlow = mySearchViewEditText.textChangesToFlow()

                editTextFlow
                    .debounce(2000)
                    .filter {
                        it?.length!! > 0
                    }
                    .onEach {
                        Log.d(TAG, "flow로 받는다 $it")
                    }
                    .launchIn(this)
            }

        }

        this.mySearchViewEditText.apply {
            this.filters = arrayOf(InputFilter.LengthFilter(12))
            this.setTextColor(Color.WHITE)
            this.setHintTextColor(Color.WHITE)
        }

        return true
    }

    // 서치뷰 검색어 입력 이벤트
    // 검색버튼이 클릭되었을 때
    override fun onQueryTextSubmit(query: String?): Boolean {
//        Log.d(TAG, "PhotoCollectionActivity - onQueryTextSubmit called / query: $query")
//
        if (!query.isNullOrEmpty()) {
            this.top_app_bar.title = query

            this.insertSearchTermHistory(query)
            this.searchPhotoApiCall(query)

        }
//
////        this.mySearchView.setQuery("", false)
////        this.mySearchView.clearFocus()
        this.top_app_bar.collapseActionView()

        return true
    }


    override fun onQueryTextChange(newText: String?): Boolean {
        Log.d(TAG, "PhotoCollectionActivity - onQueryTextChange called / newTezt: $newText")

        val userInputText = newText.let {
            it
        } ?: ""

        if (userInputText.count() == 12) {
            Toast.makeText(this, "검색어는 12자 까지만 입력 가능합니다.", Toast.LENGTH_SHORT).show()
        }

//        if(userInputText.length in 1..12) {
//            searchPhotoApiCall(userInputText)
//        }
        return true
    }

    override fun onCheckedChanged(switch: CompoundButton?, isChecked: Boolean) {
        when (switch) {
            search_history_mode_switch -> {
                if (isChecked) {
                    Log.d(TAG, "검색어 저장 기능 On")
                    SharedPrefManager.setSearchHistoryMode(isActivated = true)
                } else {
                    Log.d(TAG, "검색어 저장 기능 off")
                    SharedPrefManager.setSearchHistoryMode(isActivated = false)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            clear_search_history_button -> {
                Log.d(TAG, "검색 기록 삭제 버튼이 클릭되었다.");
                SharedPrefManager.clearSearchHistoryList()
                this.searchHistoryList.clear()
                this.searchHistoryRecyclerViewAdapter.notifyDataSetChanged()

                handleSearchViewUi()

            }
        }
    }

    // 검색 아이템 삭제 버튼 이벤트
    override fun onSearchItemDeleteBtnClicked(position: Int) {
        Log.d(
            TAG,
            "PhotoCollectionActivity - onSearchItemDeleteBtnClicked called / position : $position"
        )

        this.searchHistoryList.removeAt(position)
        SharedPrefManager.storeSearchHistoryList(searchHistoryList)
        this.searchHistoryRecyclerViewAdapter.notifyDataSetChanged()

        handleSearchViewUi()
    }

    // 검색 아이템 클릭 이벤트
    override fun onSearchItemClicked(position: Int) {
        Log.d(TAG, "PhotoCollectionActivity - onSearchItemClicked called / position : $position")
        // 해당 녀석의 검색어를 다시 호출
        val queryString = this.searchHistoryList[position].term

        searchPhotoApiCall(queryString)

        top_app_bar.title = queryString

        this.insertSearchTermHistory(searchTerm = queryString)

        this.top_app_bar.collapseActionView()

    }

    // 사진 검색 API 호출
    private fun searchPhotoApiCall(query: String) {
        RetrofitManager.instance.searchPhotos(searchTerm = query, completion = { status, list ->
            when (status) {
                RESPONSE_STATUS.OKAY -> {
                    Log.d(
                        TAG,
                        "PhotoCollectionActivity - searchPhotoApiCall called / list.size : ${list?.size}"
                    )

                    if (list != null) {
                        this.photoList.clear()
                        this.photoList = list
                        this.photoGridRecyclerViewAdapter.submitList(this.photoList)
                        this.photoGridRecyclerViewAdapter.notifyDataSetChanged()

                        top_app_bar.title = query

                    }
                }

                RESPONSE_STATUS.FAIL -> {
                    Toast.makeText(this, "$query 에 대한 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                    this.top_app_bar.collapseActionView()
                }
            }
        })
    }

    private fun handleSearchViewUi() {
        Log.d(
            TAG,
            "PhotoCollectionActivity - handleSearchViewUi called / size : ${this.searchHistoryList.size}"
        )

        if (this.searchHistoryList.size > 0) {
            search_history_recycler_view.visibility = View.VISIBLE
            search_history_recycler_view_label.visibility = View.VISIBLE
            clear_search_history_button.visibility = View.VISIBLE
        } else {
            search_history_recycler_view.visibility = View.INVISIBLE
            search_history_recycler_view_label.visibility = View.INVISIBLE
            clear_search_history_button.visibility = View.INVISIBLE
        }
    }

    //검색어 저장
    private fun insertSearchTermHistory(searchTerm: String) {
        Log.d(TAG, "PhotoCollectionActivity - insertSearchTermHistory called / ")

        if (SharedPrefManager.checkHistoryMode()) {

            // 중복 아이템 삭제
            var indexListToRemove = ArrayList<Int>()

            this.searchHistoryList.forEachIndexed { index, searchDataItem ->
                Log.d(TAG, "PhotoCollectionActivity - insertSearchTermHistory called / ")
                if (searchDataItem.term == searchTerm) {
                    indexListToRemove.add(index)
                }
            }

            indexListToRemove.forEach {
                this.searchHistoryList.removeAt(it)
            }

            // 새 아이템 넣기
            val newSearchData = SearchData(term = searchTerm, timestamp = Date().toStrings())
            this.searchHistoryList.add(newSearchData)

            SharedPrefManager.storeSearchHistoryList(this.searchHistoryList)
            this.searchHistoryRecyclerViewAdapter.notifyDataSetChanged()


        }
    }
}