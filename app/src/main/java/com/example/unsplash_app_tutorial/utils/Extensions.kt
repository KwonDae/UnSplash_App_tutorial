package com.example.unsplash_app_tutorial.utils

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import com.example.unsplash_app_tutorial.utils.Constants.TAG
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import java.text.SimpleDateFormat
import java.util.*


//문자열이 json 형태인지, json 배열 형태인지
fun String?.isJsonObject():Boolean {
    return this?.startsWith("{") == true && this.endsWith("}")
}
//fun String?.isJsonObject():Boolean = this?.startsWith("{")==true && this.endsWith("}")

fun String?.isJsonArray():Boolean {
    return this?.startsWith("[") == true && this.endsWith("]")
}

// 날짜 포맷
fun Date.toStrings() : String {
    val format = SimpleDateFormat("HH:mm:ss")
    return format.format(this)
}

fun EditText.onMyTextChanged(completion: (Editable?) -> Unit) {
    this.addTextChangedListener(object: TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(editable: Editable?) {
            completion(editable)
        }

    })
}

// 에딧텍스트 텍스트 변경을 flow로 받기
@ExperimentalCoroutinesApi
fun EditText.textChangesToFlow(): Flow<CharSequence?> {

    // flow 콜백 받기
    return callbackFlow {
        val listener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Unit
            }

            override fun afterTextChanged(s: Editable?) {
                Unit
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
               Log.d(TAG, " - onTextChanged called / textChangesToFlow() 에 달려있는 텍스트 와쳐 / text  : $text" )
                text?.let {
                    trySend(it)
                }?:""
            }
        }
        // 위에서 설정한 리스너 달아주기
        addTextChangedListener(listener)

        // 콜백이 사라질 때 실행되는 메소드
        awaitClose {
            Log.d(TAG, " - textChangesToFlow() awaitClose 실행")
            removeTextChangedListener(listener)
        }
    }.onStart {
        Log.d(TAG, " - textChangesToFlow() called / onStart 발동")
        // Rx에서 onNext 와 동일
        // emit 으로 이벤트를 전달
        emit(text)
    }
}