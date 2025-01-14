@file:JsModule("firebase/app")
@file:JsNonModule
@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS",
    "EXTERNAL_DELEGATION",
)

package org.product.inventory.web.service

external interface FirebaseApp {
    var name: String
    var options: Any
}

external fun initializeApp(
    options: FirebaseOptions,
    name: String? = definedExternally,
): FirebaseApp
