package core.utils

import kotlinx.coroutines.flow.Flow

// helper function to combine multiple flows into one
// this is a workaround for the lack of a built-in function in the Kotlin standard library
// which accept maximum of 5 flows
fun <T1, T2, T3, T4, T5, T6, R> combine(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    transform: suspend (T1, T2, T3, T4, T5, T6) -> R,
): Flow<R> = kotlinx.coroutines.flow.combine(
    kotlinx.coroutines.flow.combine(flow1, flow2, flow3, ::Triple),
    kotlinx.coroutines.flow.combine(flow4, flow5, flow6, ::Triple),
) { (item1, item2, item3), (item4, item5, item6) ->
    transform(item1, item2, item3, item4, item5, item6)
}
