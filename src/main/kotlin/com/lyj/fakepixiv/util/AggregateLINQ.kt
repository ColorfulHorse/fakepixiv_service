package com.lyj.fakepixiv.util

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.jsonArrayOf
import io.vertx.kotlin.core.json.jsonObjectOf

/**
 * @author green sun
 *
 * @date 2019/12/4
 *
 * @desc
 */
class AggregateLINQ : JsonObject {
	var pipeline: JsonArray = jsonArrayOf()
	
	
	private constructor(collectionName: String, action: (JsonArray.() -> Unit)? = null) {
		put("aggregate", collectionName)
		//put("explain", true)
		put("cursor", jsonObjectOf())
		put("pipeline", pipeline.apply {
			action?.invoke(this)
		})
	}
	
	private constructor(map: Map<String, Any>) : super(map) {
		pipeline = getJsonArray("pipeline")
	}
	
	fun operate(action: JsonArray.() -> Unit): AggregateLINQ {
		action(pipeline)
		return this
	}
	
	companion object {
		fun create(collectionName: String, action: (JsonArray.() -> Unit)? = null) =
			AggregateLINQ(collectionName, action)
	}
	
	override fun copy(): AggregateLINQ {
		val map = super.copy().map
		return AggregateLINQ(map)
	}
	
	fun lookup(
		from: String,
		localField: String,
		foreignField: String,
		`as`: String
	): AggregateLINQ {
		pipeline.add(
			jsonObjectOf(
				"\$lookup" to jsonObjectOf(
					"from" to "illust",
					"localField" to "illustId",
					"foreignField" to "_id",
					"as" to "illust"
				)
			)
		)
		return this
	}
	
	fun match(vararg fields: Pair<String, Any?>): AggregateLINQ {
		pipeline.add(
			jsonObjectOf(
				"\$match" to jsonObjectOf(*fields)
			)
		)
		return this
	}
	
	fun unwind(
		path: String,
		preserveNullAndEmptyArrays: Boolean = false,
		includeArrayIndex: String? = null
	): AggregateLINQ {
		pipeline.add(
			jsonObjectOf(
				"\$unwind" to jsonObjectOf(
					"path" to "\$$path",
					"preserveNullAndEmptyArrays" to preserveNullAndEmptyArrays
				).apply {
					includeArrayIndex?.let { put("includeArrayIndex", includeArrayIndex) }
				}
			)
		)
		return this
	}
	
	fun project(vararg fields: Pair<String, Any?>): AggregateLINQ {
		pipeline.add(
			jsonObjectOf(
				"\$project" to jsonObjectOf(*fields)
			)
		)
		return this
	}
	
	fun count(fieldName: String): AggregateLINQ {
		pipeline.add(jsonObjectOf("\$count" to fieldName))
		return this
	}
	
	fun skip(skip: Int): AggregateLINQ {
		pipeline.add(jsonObjectOf("\$skip" to skip))
		return this
	}
	
	fun limit(limit: Int): AggregateLINQ {
		pipeline.add(jsonObjectOf("\$limit" to limit))
		return this
	}
	
	fun sort(field: String, order: Int): AggregateLINQ {
		pipeline.add(jsonObjectOf("\$sort" to jsonObjectOf(
			field to order
		)))
		return this
	}
}