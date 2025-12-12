package io.github.vvb2060.ims.viewmodel

import android.app.Application
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import io.github.vvb2060.ims.LogcatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class LogcatViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "LogcatViewModel"
    }

    val logs = LogcatRepository.logs

    init {
        LogcatRepository.startLogcat()
    }

    fun clearLogs() {
        LogcatRepository.clearLogs()
    }

    fun exportLogFile() {
        viewModelScope.launch(Dispatchers.IO) {
            File(application.externalCacheDir, "turbo_ims.log").apply {
                writeText("TurboIms Logcat:\n")
                Runtime.getRuntime()
                    .exec(arrayOf("logcat", "-d")).inputStream.use { input ->
                        FileOutputStream(this, true).use {
                            input.copyTo(it)
                        }
                    }

                val authority = "${application.packageName}.logcat_fileprovider"
                val uri = FileProvider.getUriForFile(application, authority, this)
                application.startActivity(
                    Intent.createChooser(
                        Intent(Intent.ACTION_SEND)
                            .setType("text/plain")
                            .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            .putExtra(Intent.EXTRA_STREAM, uri),
                        "Export Logcat"
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        }
    }
}