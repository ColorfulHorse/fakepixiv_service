package com.lyj.fakepixiv.util

import io.reactivex.internal.util.QueueDrainHelper.request
import io.vertx.ext.web.RoutingContext

/**
 * @author green sun
 *
 * @date 2019/11/29
 *
 * @desc
 */
fun RoutingContext.parseJsonBody(next: (String) -> Unit) {
	request().bodyHandler {
		val json = it.toJsonObject().toString()
		next(json)
	}
}

fun RoutingContext.endJson(json: String) {
	response()
		.putHeader("content-type", "application/json")
		.end(json)
}