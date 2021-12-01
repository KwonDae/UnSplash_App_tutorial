package com.example.unsplash_app_tutorial.model

import java.io.Serializable

// data클래슨느 매개변수를 설정해주면 매개변수가 들어가면 생성자 메소드를 만들지 않아도 자동으로 만들어 준다.
data class Photo(
    var thumbnail: String?,
    var author: String?,
    var createdAt: String?,
    var likesCount: Int
):Serializable {
}