package com.example.journalentry.getapi

import com.example.journalentry.MainActivity
import com.example.journalentry.database.DatabaseManager
import com.example.journalentry.database.Entity
import com.example.journalentry.database.Sentence
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URLEncoder


class ApiManager(){
    companion object {
        fun createRetrofit(): Retrofit{
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor { chain ->
                val original = chain.request()

                // Request customization: add request headers
                val requestBuilder = original.newBuilder()
                    .addHeader("Authorization", "Bearer KGBCR3WTVZZI2FQFEKMHOI7S4OYROYDE")
                val request = requestBuilder.build()
                chain.proceed(request)
            }

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.wit.ai/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()
            return retrofit
        }
    }
    val db = DatabaseManager(MainActivity())
    fun getQuery(query: String) {
        val query = URLEncoder.encode(query,"UTF-8").replace("+", "%20")
        val url = "https://api.wit.ai/message?v=20200908&q=$query"
        val retrofit = createRetrofit()
        val jsonPlaceHolderApi = retrofit.create(
            JsonPlaceHolderApi::class.java
        )
        val call = jsonPlaceHolderApi.getPosts(url)
        val gson = Gson()
//        return gson.fromJson(call.request(),Post::class.java)
    }
    fun getRequest(query: String) {
        val url = "https://api.wit.ai/message?v=20200908&q=$query"
        val retrofit = createRetrofit()
        val jsonPlaceHolderApi = retrofit.create(
            JsonPlaceHolderApi::class.java
        )
        val call = jsonPlaceHolderApi.getPosts(url)
        call.enqueue(object : Callback<Post?> {
            override fun onResponse(call: Call<Post?>, response: Response<Post?>) {
                if (!response.isSuccessful) {
                    println("Code: " + response.code())
                    return
                }
                val post = response.body()
                if (post != null) {
                    updateDb(post,query)
                }
            }

            override fun onFailure(call: Call<Post?>, t: Throwable) {
                println(t.message)
            }

            private fun updateDb(post: Post, query: String) {
                val gsonObj = Gson()
//                val

//                for (e in ent.indices) {
//                    db.addEntities(Entity(e["key"],e[""],sentId,t,s))
//                }
//                val sentence = db.getSentence(query)
//                sentence.sentiment = 1
//                sentence.entitiesId =
//                db.updateSentence(sentence, sentId)

            }
        })
    }
}

