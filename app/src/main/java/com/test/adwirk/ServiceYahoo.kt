package com.test.adwirk

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log

class ServiceYahoo : Service() {


    private val TIME_10_MIN = 1 * 15 * 1000L
    private var mHandler = Handler()
    private var showYahoo = {
        YahooActivity.start(this)
        startHandler()
    }

    private fun startHandler() {
        mHandler.postDelayed(showYahoo, TIME_10_MIN)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startHandler()
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ServiceYahoo", "onDestroy")
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


}