package com.mrdeveloperjis.ictflow

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class UpdateChecker(private val context: Context) : AsyncTask<Void, Void, String?>() {

    companion object {
        //// UPDATE IT TIME TO TIME ////
        private const val CURRENT_VERSION = "1.3"
        private const val VERSION_URL = "https://raw.githubusercontent.com/ictflow/AndroidApp/refs/heads/main/version.json"
    }

    override fun doInBackground(vararg params: Void?): String? {
        return try {
            val url = URL(VERSION_URL)
            val connection = url.openConnection() as HttpURLConnection
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val result = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                result.append(line)
            }
            reader.close()
            result.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        if (result != null) {
            try {
                val jsonObject = JSONObject(result)
                val latestVersion = jsonObject.getString("latestVersion")
                val updateUrl = jsonObject.getString("updateUrl")

                // Compare with current version
                if (CURRENT_VERSION != latestVersion) {
                    showUpdateDialog(updateUrl)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showUpdateDialog(updateUrl: String) {
        AlertDialog.Builder(context)
            .setTitle("Update Available")
            .setMessage("A new version is available. Please update to the latest version.")
            .setPositiveButton("Update") { _, _ ->
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl))
                context.startActivity(browserIntent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
