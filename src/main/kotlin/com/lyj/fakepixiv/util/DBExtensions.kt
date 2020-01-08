package com.lyj.fakepixiv.util

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.coroutines.awaitEvent

/**
 * @author green sun
 *
 * @date 2019/12/3
 *
 * @desc
 */
suspend fun MongoClient.aggregateAwait(collection: String, pipeline: JsonArray): List<JsonObject> {
	return awaitEvent { handler ->
		val data: MutableList<JsonObject> = mutableListOf()
		aggregate(collection, pipeline)
			.handler { res ->
				data.add(res)
			}.exceptionHandler {
				throw it
			}.endHandler {
				handler.handle(data)
			}
	}
}

suspend fun MongoClient.aggregateOneAwait(collection: String, pipeline: JsonArray): JsonObject? {
	return awaitEvent { handler ->
		var json: JsonObject? = null
		aggregate(collection, pipeline)
			.handler { res ->
				json = res
			}.exceptionHandler {
				throw it
			}.endHandler {
				handler.handle(json)
			}
	}
}

suspend fun MongoClient.aggregateCountAwait(collection: String, pipeline: JsonArray): Long {
	if (!pipeline.isEmpty) {
		val last: JsonObject = pipeline.getJsonObject(pipeline.size() - 1)
		var fieldName = ""
		if (last.containsKey("\$count")) {
			fieldName = last.getString("\$count")
		} else {
			fieldName = "count"
			pipeline.add(jsonObjectOf("\$count" to fieldName))
		}
		return awaitEvent { handler ->
			aggregate(collection, pipeline)
				.handler { res ->
					handler.handle(res.getLong(fieldName))
				}.exceptionHandler {
					throw it
				}
		}
	}
	return 0
}

