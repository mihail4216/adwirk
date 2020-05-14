package com.test.adwirk

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class WebViewActivity : AppCompatActivity() {

    companion object {
        private const val ALARM_ID = 223
        private const val EXTRA_URL = "EXTRA_URL"
        /**
         * Функция запуска активити
         * @param context - котекст по которому надо запустить активити
         * @param url - ссылка которую нужно открыть по дефолту
         */
        fun start(context: Context, url: String) {
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra(EXTRA_URL, url)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
        }
    }

    /**
     * Функция проверки запущен ли сервис
     * @param javaClass - класс сервиса для проверки
     */
    private fun Activity.isServiceRunning(javaClass: Class<ServiceYahoo>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningServices(Integer.MAX_VALUE)
            .any { it.service.className == javaClass.name }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initWebView()
        startAlarm()
    }

    /**
     * функция проверяет нет ли уже запущенного аларма и запускает аларм менеджер с частотой в 10 минут
     */
    private fun startAlarm() {
        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intentYahooButton = Intent(this, YahooActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            ALARM_ID,
            intentYahooButton,
            PendingIntent.FLAG_NO_CREATE
        )
        if (pendingIntent == null)
            manager.setRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + (AlarmManager.INTERVAL_HOUR / 6),
                AlarmManager.INTERVAL_HOUR / 6,  //60 минут / 6 = 10 минут
                PendingIntent.getActivity(
                    this,
                    ALARM_ID,
                    intentYahooButton,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )

    }

    /**
     * функция проверяет если подобные запущенные сервисы, если нет то запускает их
     */
    @Deprecated("")
    private fun checkRunningService() {
        if (!isServiceRunning(ServiceYahoo::class.java))
            startService(Intent(this, ServiceYahoo::class.java))
    }


    /**
     * функция инициализации WebView
     */
    private fun initWebView() {
        val url = intent.getStringExtra(EXTRA_URL)
        if (url != null)
            _webView.loadUrl(url)
        else
            _webView.loadUrl("https://www.google.ru/")

        _webView.settings.javaScriptEnabled = true
        _webView.webChromeClient = object : WebChromeClient() {}
        _webView.webViewClient = object : WebViewClient() {
            /*
                переопределяем функцию в которую приходит запрос
                и в соответствии от ссылки открываем либо в нашем вебвью, либо
                в других приложениях которые моут открыть ссылку
             */
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    if (request != null && request.url.host!!.contains("google"))
                        false
                    else {
                        request?.apply {
                            openWebPage(this.url.toString())
                        }
                        true
                    }
                else
                    false
            }


        }
    }

    /**
     * Функция создает интент и запускает активити
     * @param url - ссылка по которой нужно открыть приложение
     */
    fun openWebPage(url: String?) {
        url?.apply {
            val webPage: Uri = Uri.parse(this)
            val intent = Intent(Intent.ACTION_VIEW, webPage)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }
    }


    override fun onBackPressed() {
        if (_webView.canGoBack())//проверка есть ли стек переходов во вебвью
            _webView.goBack()// переходим назад
    }

}
