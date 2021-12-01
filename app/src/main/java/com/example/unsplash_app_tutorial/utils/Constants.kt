package com.example.unsplash_app_tutorial.utils

object Constants {
    const val TAG : String = "로그"
}

enum class SEARCH_TYPE {
    PHOTO,
    USER
}

enum class RESPONSE_STATUS{
    OKAY,
    FAIL,
    NO_CONTENT
}

object API {
    const val BASE_URL : String = "https://api.unsplash.com/"

    const val CLIENT_ID : String = "ZITBqK0E9Y8sNvseR47Dv52o-q48Ov9i3AZe27isNQQ"

    const val SEARCH_PHOTO : String = "search/photos"
    const val SEARCH_USERS : String = "search/users"

}