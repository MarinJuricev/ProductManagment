package org.product.inventory.web.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.product.inventory.web.di.DiJs

@Composable
inline fun <reified T : Any> rememberViewModel(
    vararg parameters: Any,
) = remember { DiJs.get<T>(parameters.toList()) }
