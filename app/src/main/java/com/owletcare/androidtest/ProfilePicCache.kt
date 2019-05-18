package com.owletcare.androidtest

import android.graphics.Bitmap
import android.util.LruCache

object  ProfilePicCache {
    private var lru: LruCache<String, Bitmap> = LruCache(1024)
    fun getLru(): LruCache<String, Bitmap> {
        return lru
    }
}