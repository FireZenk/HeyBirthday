package data.net

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import data.net.models.GiphyData
import okhttp3.OkHttpClient
import okhttp3.Request

class GiphyDataSource(private val clientId: String) {

    companion object {
        private const val URL = "https://api.giphy.com/v1/gifs/search"
    }

    private val client: OkHttpClient = OkHttpClient()
    private val gson: Gson = Gson()
    private val type = object : TypeToken<GiphyData>() {}.type

    fun searchImage(name: String): String? {
        val request = Request.Builder()
                .url("$URL?api_key=$clientId&q=$name&limit=1")
                .build()

        val response = client.newCall(request).execute()
        val jsonData = response.body()?.string()
        val giphyData = gson.fromJson<GiphyData>(jsonData, type)

        return if (giphyData.data.isEmpty()) {
             null
        } else {
            giphyData.data[0].embed_url
        }
    }
}