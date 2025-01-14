package org.product.inventory.web.core

// This workaround is used to fill space in case of empty string
fun String.fillWithSpaceIfEmpty() = ifEmpty { " " }
