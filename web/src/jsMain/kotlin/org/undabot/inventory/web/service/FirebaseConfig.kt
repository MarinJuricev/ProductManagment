package org.product.inventory.web.service

fun initFirebase() {
    val firebaseOptions = FirebaseOptions(
        apiKey = "",
        measurementId = "",
        messagingSenderId = "",
        appId = "",
        projectId = "",
        storageBucket = "",
        authDomain = "",
    )
    initializeApp(firebaseOptions)
}
