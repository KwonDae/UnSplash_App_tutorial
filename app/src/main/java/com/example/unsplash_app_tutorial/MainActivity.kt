package com.example.unsplash_app_tutorial

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.unsplash_app_tutorial.utils.Constants.TAG
import com.example.unsplash_app_tutorial.utils.SEARCH_TYPE
import com.example.unsplash_app_tutorial.utils.onMyTextChanged
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_button_search.*


class MainActivity : AppCompatActivity() {

    private var currentSearchType: SEARCH_TYPE = SEARCH_TYPE.PHOTO

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "MainActivity - onCreate() called")

        // 라디오 그룹 가져오기
        search_term_radio_group.setOnCheckedChangeListener { _, checkedId ->
            // switch 문
            when(checkedId) {
                R.id.photo_search_radio_button -> {
                    Log.d(TAG, "사진검색 버튼 클릭!")
                    search_term_text_layout.hint = "사진검색"
                    search_term_text_layout.startIconDrawable = resources.getDrawable(R.drawable.ic_baseline_add_a_photo_24, resources.newTheme())
                    this.currentSearchType = SEARCH_TYPE.PHOTO
                }

                R.id.user_search_radio_button -> {
                    Log.d(TAG, "사용자검색 버튼 클릭!")
                    search_term_text_layout.hint = "사용자검색"
                    search_term_text_layout.startIconDrawable = resources.getDrawable(R.drawable.ic_baseline_accessibility_24, resources.newTheme())
                    this.currentSearchType = SEARCH_TYPE.USER
                }
            }
            Log.d(TAG, "MainActivity - OnChechedChanged() called / currentSearchType : $currentSearchType")
        }

        // 텍스트가 변경이 되었을 때
        search_term_edit_text.onMyTextChanged {
            // 입력된 글자가 하나라도 있으면
            if(it.toString().count() > 0) {
                // 검색 버튼을 보여준다
                frame_search_button.visibility = View.VISIBLE
                search_term_text_layout.helperText = ""
                // 스크롤뷰를 올린다
                main_scrollView.scrollTo(0, 200)
            } else {
                frame_search_button.visibility = View.INVISIBLE
                search_term_text_layout.helperText = "검색어를 입력해주세요"
            }
            
            if(it.toString().count() == 12) {
                Log.d(TAG, "onCreate: 에러 띄우기")
                Toast.makeText(this, "검색어는 12자 까지만 입력 가능합니다.", Toast.LENGTH_SHORT).show()
            }
        }

        btn_search.setOnClickListener {
            Log.d(TAG, "onCreate: 검색 버튼이 클릭되었다. / currentSearchType $currentSearchType")
            this.handleSearchButtonUi()


        }


    }  // onCreate

    private fun handleSearchButtonUi() {
        btn_progress.visibility = View.VISIBLE
        btn_search.text = ""
        Handler().postDelayed({
            btn_search.visibility = View.INVISIBLE
            btn_search.text = "검색"
        }, 1500)
    }

}