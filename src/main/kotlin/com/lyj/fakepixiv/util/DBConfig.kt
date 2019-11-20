package com.lyj.fakepixiv.util

import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

/**
 * Created by green sun on 2019/11/16.
 */
object DBConfig {
	val CONNECTION = json {
		obj (
			"host" to "127.0.0.1",
			"port" to 27017,
			"username" to "lyj",
			"password" to "liaolove1314",
			"db_name" to "fakepixiv"
			//"authSource" to "fakepixiv.db"
		)
	}
}