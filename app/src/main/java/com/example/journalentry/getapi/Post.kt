package com.example.journalentry.getapi

import com.google.gson.annotations.SerializedName
import com.google.gson.internal.LinkedTreeMap

class Post {
    @SerializedName("text")
    var witText: String? = null

    @SerializedName("intents")
    var witIntents: ArrayList<LinkedTreeMap<String,Any>>? = null

    @SerializedName("entities")
    var witEntities: LinkedTreeMap<String,ArrayList<LinkedTreeMap<String,Any>>>? = null

    @SerializedName("traits")
    var witTraits: LinkedTreeMap<String,ArrayList<LinkedTreeMap<String,Any>>>? = null
}