package com.inyongtisto.todoapp.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.inyongtisto.todoapp.R
import com.inyongtisto.todoapp.helper.MyAlert
import kotlinx.android.synthetic.main.activity_about_us.*

class AboutUsActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        MyAlert.alertDismis()

        val url = "https://wetheapp.com/"
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false
        webView.loadUrl(url)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                pd.visibility = View.VISIBLE
                view.loadUrl(url)
                return true
            }
            override fun onPageFinished(view: WebView, url: String) {
                pd.visibility = View.GONE
            }
        }
    }
}
