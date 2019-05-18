package com.owletcare.androidtest.extensions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import android.widget.ImageView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import java.net.URL


suspend fun ImageView.loadWebImageAsync(urlString: String, lruCache: LruCache<String, Bitmap>) {
    val viewToUpdate = this
    val bitmap = withContext(Dispatchers.IO) {
        val lruPic = lruCache.get(urlString)
        if (lruPic != null) {
            lruPic
        } else {
            val inputStream = URL(urlString).openStream()
            BitmapFactory.decodeStream(inputStream)
        }
    }
    viewToUpdate.setImageBitmap(bitmap)
}

/**
 * Loads image using Picasso.  This would be my preferred way to do it since it is a reliable library and a very quick implementation.
 * I figured you might want me to, as my math teachers would say,  "show my work."
 */
fun ImageView.loadWebImageAsyncPicasso(urlString: String) {
    Picasso.get().load(urlString).into(this)
}
