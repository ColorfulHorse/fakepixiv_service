package com.lyj.fakepixiv

import com.lyj.fakepixiv.util.DBConfig
import com.mongodb.MongoClientSettings
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.Json
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.ext.mongo.findAwait
import io.vertx.kotlin.ext.mongo.insertAwait
import io.vertx.kotlin.ext.mongo.saveAwait
import kotlinx.coroutines.*

class MainVerticle : AbstractVerticle(), CoroutineScope by CoroutineScope(Dispatchers.IO + SupervisorJob()) {
	
	override fun start(startPromise: Promise<Void>) {
		println("thread: ${Thread.currentThread().name}")
		
		val router = Router.router(vertx)
		router.post("/history/illust/create").handler {
			createIllustHistory(it)
		}
		
		val client = MongoClient.createShared(vertx, DBConfig.CONNECTION)
		
		vertx
			.createHttpServer()
			.requestHandler { req ->
				req.response()
					.putHeader("content-type", "text/plain")
					.end("Hello from Vert.x!")
			}
			.listen(8888) { http ->
				if (http.succeeded()) {
					startPromise.complete()
					println("HTTP server started on port 8888")
				} else {
					startPromise.fail(http.cause());
				}
			}
	}
	
	/**
	 *
	 */
	fun createIllustHistory(context: RoutingContext) {
		//JsonUtil.bean2Json()
	}
	
}
