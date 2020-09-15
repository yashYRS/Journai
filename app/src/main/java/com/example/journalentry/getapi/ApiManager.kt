package com.example.journalentry.getapi

import androidx.appcompat.app.AppCompatActivity
import com.example.journalentry.database.DatabaseManager
import com.example.journalentry.database.Entity
import com.example.journalentry.database.Sentence
import okhttp3.OkHttpClient
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URLEncoder

class ApiManager{
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

            return Retrofit.Builder()
                .baseUrl("https://api.wit.ai/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()
        }
    }

    // GET Request to the API
    fun getRequest(query: String, context: AppCompatActivity) {
        val url = "https://api.wit.ai/message?v=20200908&q=$query"
        val retrofit = createRetrofit()
        val jsonPlaceHolderApi = retrofit.create(
            JsonPlaceHolderApi::class.java
        )
        val call = jsonPlaceHolderApi.getPosts(url)
        call!!.enqueue(object : Callback<Post?> {
            override fun onResponse(call: Call<Post?>, response: Response<Post?>) {
                if (!response.isSuccessful) {
                    println("Code: " + response.code())
                    return
                }
                val post = response.body()
                if (post != null) {
                    updateDb(post, query, context)
                }
            }
            override fun onFailure(call: Call<Post?>, t: Throwable) {
                println(t.message)
            }
        })
    }

    private fun updateDb(post: Post, query: String, context: AppCompatActivity) {
        val db = DatabaseManager(context)

        val entities = post.witEntities
        val intents = post.witIntents
        val traits = post.witTraits
        val sentId = db.getSentenceId(query)

        // Add Entities in DB
        if (entities != null) {
            for ((_, value) in entities) {
                val entityVal = value[0]["value"].toString()
                val entityTyp = value[0]["type"].toString()
                val entityScore = value[0]["confidence"].toString().toFloat()
                val entityKey = value[0]["body"].toString()
                val newEnt = Entity(entityKey, entityVal, sentId, entityTyp, entityScore)
                db.addEntities(newEnt)
            }
        }

        // Get Sentiment
        var sentiment = 0f
        if (traits != null) {
            for ((key, value) in traits) {
                if (key == "wit\$sentiment"){
                    sentiment = value[0]["confidence"].toString().toFloat()
                }
            }
        }

        // get Intent
        var intent = ""
        var confidence = 0f
        if (intents != null) {
            if (intents.isNotEmpty()) {
                intent = intents[0]["name"].toString()
                confidence = intents[0]["confidence"].toString().toFloat()
            }
        }

        // Update Sentence in DB
        val sentence = db.getSentence(sentId)
        val updatedSentence = Sentence(sentence.noteId, sentence.content, sentiment, 1, intent, confidence)
        db.updateSentence(updatedSentence, sentId)
        db.close()
    }

    // Search Query request
    fun getQuery(query: String): Call<Post?>? {
        val query = URLEncoder.encode(query, "UTF-8").replace("+", "%20")
        val url = "https://api.wit.ai/message?v=20200908&q=$query"

        val retrofit = createRetrofit()

        val jsonPlaceHolderApi = retrofit.create(
            JsonPlaceHolderApi::class.java
        )
        return jsonPlaceHolderApi.getPosts(url)
    }
}

