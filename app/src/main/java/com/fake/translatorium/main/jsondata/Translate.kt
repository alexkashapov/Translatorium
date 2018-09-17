package com.fake.translatorium.main.jsondata

import com.fasterxml.jackson.annotation.JsonProperty

data class Translate(@JsonProperty("code") val code:Int,
                     @JsonProperty("text") val text:ArrayList<String>,
                     @JsonProperty("lang") val lang: String)