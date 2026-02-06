package edu.ivytech.rootbeer

import android.app.Application

class RootBeerApplication : Application() {
    override fun onCreate(){
        super.onCreate()
        RootBeerRepository.initialize(this)
    }
}