package com.mrdeveloperjis.ictflow

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    private lateinit var webView: WebView
    private var lastBackPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = FrameLayout(this)
        layout.fitsSystemWindows = true

        webView = WebView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            settings.javaScriptEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url.toString()
                    val uri = Uri.parse(url)

                    // Define allowed internal domains
                    val allowedDomains = listOf(
                        "https://mrdeveloperjis.github.io/",
                        "https://ictflow.github.io/"
                    )

                    // Check if the URL belongs to an internal domain
                    if (allowedDomains.any { url.startsWith(it) }) {
                        return false // Let WebView handle it
                    }

                    // Handle external links
                    return when (uri.scheme) {
                        "tel" -> {
                            startActivity(Intent(Intent.ACTION_DIAL, uri))
                            true
                        }
                        "mailto" -> {
                            startActivity(Intent(Intent.ACTION_SENDTO, uri))
                            true
                        }
                        "http", "https" -> {
                            // Open external HTTP/HTTPS URLs in a browser
                            startActivity(Intent(Intent.ACTION_VIEW, uri))
                            true
                        }
                        else -> false // Let WebView handle unknown schemes
                    }
                }
            }
        }

        layout.addView(webView)
        setContentView(layout)

        webView.loadUrl("https://mrdeveloperjis.github.io/ict/")

        // Check for updates
        UpdateChecker(this).execute()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastBackPressedTime < 1000) {
                super.onBackPressed() // Exit the app
            } else {
                lastBackPressedTime = currentTime
                Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
