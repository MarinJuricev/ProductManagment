package org.product.inventory.web.service

data class FirebaseOptions(
    @JsName("apiKey") val apiKey: String,
    @JsName("authDomain") val authDomain: String,
    @JsName("projectId") val projectId: String?,
    @JsName("storageBucket") val storageBucket: String?,
    @JsName("messagingSenderId") val messagingSenderId: String?,
    @JsName("appId") val appId: String?,
    @JsName("measurementId") val measurementId: String?,
)
