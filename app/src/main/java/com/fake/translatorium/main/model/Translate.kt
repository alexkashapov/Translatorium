package com.fake.translatorium.main.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Translate(@JsonProperty("code") val code:Int,
                     @JsonProperty("text") val text:ArrayList<String>,
                     @JsonProperty("lang") val lang: String)