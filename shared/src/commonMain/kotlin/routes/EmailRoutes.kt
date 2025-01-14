@file:Suppress("ktlint:standard:filename")

package routes

import io.ktor.resources.Resource

@Resource("/v1")
class V1 {

    @Resource("/send-email")
    class SendEmailRoute(val v1: V1 = V1())
}
