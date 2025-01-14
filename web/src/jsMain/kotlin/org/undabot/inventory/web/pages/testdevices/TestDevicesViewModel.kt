package org.product.inventory.web.pages.testdevices

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TestDevicesViewModel(
    val scope: CoroutineScope,
) {

    private val _state = MutableStateFlow(TestDevicesState())
    val state = _state.asStateFlow()
}
