package data.net

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import data.net.models.ImgurData
import okhttp3.OkHttpClient
import okhttp3.Request

class ImgurDataSource(private val clientId: String) {

    companion object {
        private const val AUTH_KEY = "Authorization"
        private const val AUTH_VALUE = "Client-ID"
        private const val URL = "https://api.imgur.com/3/gallery/r/"
    }

    private val client: OkHttpClient = OkHttpClient()
    private val gson: Gson = Gson()
    private val type = object : TypeToken<ImgurData>() {}.type

    fun searchImage(name: String): String? {
        val request = Request.Builder()
                .addHeader(AUTH_KEY, "$AUTH_VALUE $clientId")
                .url("$URL$name")
                .build()

        val response = client.newCall(request).execute()
        val jsonData = response.body()?.string()
        val imgurData = gson.fromJson<ImgurData>(jsonData, type)

        return imgurData.data[0].link
    }
}