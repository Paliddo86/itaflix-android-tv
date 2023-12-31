package com.animsh.moviem.util

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by animsh on 2/25/2021.
 */
class Constants {

    companion object {
        const val BASE_URL = "https://api.themoviedb.org/"
        const val API_KEY = "Your Api Key"
        const val IMAGE_ORIGINAL = "https://image.tmdb.org/t/p/original/"
        const val IMAGE_W500 = "https://image.tmdb.org/t/p/w500/"

        // Movie Database
        const val FAVORITE_MOVIES_TABLE = "favorite_movies_table"
        const val FAVORITE_DATABASE = "favorite_database"
        const val FAVORITE_TV_TABLE = "favorite_TV_table"

        fun hasInternetConnection(application: Application): Boolean {
            val connectivityManager = application.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }

        fun getLocalBitmapUri(bmp: Bitmap, context: Context, name: String): Uri? {
            var bmpUri: Uri? = null
            try {
                val file = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "$name.png"
                )
                val out = FileOutputStream(file)
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.close()
                bmpUri =
                    FileProvider.getUriForFile(context, context.packageName + ".provider", file)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return bmpUri
        }

        fun modeChecker(context: Context): Boolean {
            val nightModeFlags = context.resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK
            return when (nightModeFlags) {
                Configuration.UI_MODE_NIGHT_YES -> true
                Configuration.UI_MODE_NIGHT_NO -> false
                Configuration.UI_MODE_NIGHT_UNDEFINED -> false
                else -> false
            }
        }
    }
}