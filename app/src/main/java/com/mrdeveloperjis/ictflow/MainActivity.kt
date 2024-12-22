package com.mrdeveloperjis.ictflow

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import android.widget.FrameLayout

class MainActivity : ComponentActivity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create a FrameLayout to hold the WebView
        val layout = FrameLayout(this)
        layout.fitsSystemWindows = true

        // Initialize the WebView
        webView = WebView(this)
        webView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        layout.addView(webView)

        // Set the layout as the content view
        setContentView(layout)

        // Configure WebView settings
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true // Enable JavaScript for your site

        webView.webViewClient = WebViewClient() // Open links in the WebView
        webView.loadUrl("https://mrdeveloperjis.github.io/ict/") // Load your website
    }

    // Handle back button for WebView navigation
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack() // Navigate to the previous page
        }
    }
}
