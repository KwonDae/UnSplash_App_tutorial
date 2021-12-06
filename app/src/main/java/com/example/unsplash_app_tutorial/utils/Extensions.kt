package com.example.unsplash_app_tutorial.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
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