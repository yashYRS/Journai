package com.example.journalentry.getapi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Url


interface JsonPlaceHolderApi {
    @Headers("Authorization: Bearer KGBCR3WTVZZI2FQFEKMHOI7S4OYROYDE")
    @GET
    fun getPosts(@Url url: String?): Call<Post?>?
}