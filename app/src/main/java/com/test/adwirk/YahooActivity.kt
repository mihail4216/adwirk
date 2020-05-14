package com.test.adwirk

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_yahoo.*

class YahooActivity : AppCompatActivity() {

    companion object {
        private const val URL_YAHOO = "https://www.yahoo.com"
        fun start(context: Context) {
            context.startActivity(
                Intent(context, YahooActivity::class.java)
                    .addFlags(FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_yahoo)
        _btnYahoo.setOnClickListener {
            WebViewActivity.start(this, URL_YAHOO)
            finish()
        }

    }
}