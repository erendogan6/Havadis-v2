package com.erendogan.havadis_v2.model

data class PaylasimModel(
    val id:String,
    val email: String,
    val aciklama:String,
    val url: String,
    var begeniSayisi:String,
    val paylasimTuru:String,
    val tarih:String,
    val yorumSayisi:String
)