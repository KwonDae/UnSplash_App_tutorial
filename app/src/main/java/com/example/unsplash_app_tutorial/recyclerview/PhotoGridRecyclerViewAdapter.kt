package com.example.unsplash_app_tutorial.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.unsplash_app_tutorial.App
import com.example.unsplash_app_tutorial.R
import com.example.unsplash_app_tutorial.model.Photo

class PhotoGridRecyclerViewAdapter(photoList2: ArrayList<Photo>?) : RecyclerView.Adapter<PhotoItemViewHolder>() {

    private var photoList = ArrayList<Photo>()

    // 뷰홀더와 레이아웃 연결
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoItemViewHolder {
        //뷰홀더가 생성되었을때
        return PhotoItemViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.layout_photo_item, parent, false)
        )
    }

    //뷰가 묶였을 때 데이터를 뷰홀더에 넘겨준다.
    override fun onBindViewHolder(holder: PhotoItemViewHolder, position: Int) {
        holder.bindWithView(photoItem = photoList.get(position))
    }

    // 보여줄 목록의 갯수
    override fun getItemCount(): Int {
        return this.photoList.size
    }

    // 외부에서 어댑터에 데이터 배열을 넘겨준다.
    fun submitList(photoList: ArrayList<Photo>) {
        this.photoList = photoList
    }
}