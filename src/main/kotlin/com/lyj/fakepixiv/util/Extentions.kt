package com.lyj.fakepixiv.util

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
		val json = it.toString("UTF-8")
		next(json)
	}
}

fun RoutingContext.endJson(json: String) {
	response()
		.putHeader("content-type", "application/json")
		.end(json)
}


