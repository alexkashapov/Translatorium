package com.fake.translatorium.main.jsondata

import com.fasterxml.jackson.annotation.JsonProperty

data class Langs(@JsonProperty("dirs")val dirs:ArrayList<String>,
                 @JsonProperty("langs") val langs: HashMap<String,String>)