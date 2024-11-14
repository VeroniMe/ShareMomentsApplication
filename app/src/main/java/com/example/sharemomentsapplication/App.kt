package com.example.sharemomentsapplication

import android.app.Application
import utilities.ImageLoader

class App : Application(){
    override fun onCreate() {
        super.onCreate()
        ImageLoader.init(this)
    }


}