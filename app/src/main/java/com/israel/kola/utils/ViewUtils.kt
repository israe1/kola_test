package com.israel.kola.utils

import android.content.Context
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.israel.kola.R
import de.hdodenhof.circleimageview.CircleImageView

fun getProgressDrawable(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 3f
        centerRadius = 20f
        setTint(context.getColor(R.color.overlay_dark_20))
        start()
    }
}

fun ImageView.loadImage(uri: String?){
    clipToOutline = true
    val options = RequestOptions()
        .placeholder(getProgressDrawable(context))
        .error(R.drawable.logo)
    Glide.with(this.context)
        .setDefaultRequestOptions(options)
        .load(uri)
        .into(this)
}

fun CircleImageView.loadCircularImage(uri: String?){
    clipToOutline = true
    val options = RequestOptions()
        .placeholder(getProgressDrawable(context))
        .error(R.drawable.logo)
    Glide.with(this.context)
        .setDefaultRequestOptions(options)
        .load(uri)
        .into(this)
}

//fun reduceSizeForUpload(file: File): File?{
//    try {
//
//    }
//}