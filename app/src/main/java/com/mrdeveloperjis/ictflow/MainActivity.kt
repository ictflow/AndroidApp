package com.mrdeveloperjis.ictflow

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import android.webkit.*

class MainActivity : ComponentActivity() {
    private lateinit var webView: WebView
    private var lastBackPressedTime: Long = 0

    // Manually set the current version here
    private val currentVersion = "1.4"

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
        checkForUpdates()
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

    private fun checkForUpdates() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://raw.githubusercontent.com/ictflow/AndroidApp/refs/heads/main/version.json")
                val connection = url.openConnection() as HttpURLConnection
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val result = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    result.append(line)
                }
                reader.close()

                val jsonObject = JSONObject(result.toString())
                val latestVersion = jsonObject.getString("latestVersion")
                val updateUrl = jsonObject.getString("updateUrl")

                // Compare with the manually specified current version
                if (currentVersion != latestVersion) {
                    runOnUiThread {
                        // Show update dialog
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle("Update Available")
                            .setMessage("A new version is available. Please update to the latest version.")
                            .setPositiveButton("Update") { _, _ ->
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl))
                                startActivity(browserIntent)
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
