package com.lyj.fakepixiv

import com.lyj.fakepixiv.app.utils.JsonUtil
import com.lyj.fakepixiv.model.request.History
import com.lyj.fakepixiv.model.request.HistoryReq
import com.lyj.fakepixiv.model.request.Illust
import com.lyj.fakepixiv.model.response.BaseResponse
import com.lyj.fakepixiv.util.DBConfig
import com.lyj.fakepixiv.util.endJson
import com.lyj.fakepixiv.util.parseJsonBody
import com.squareup.moshi.Types
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.array
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.ext.mongo.findAwait
import io.vertx.kotlin.ext.mongo.saveAwait
import kotlinx.coroutines.*

class MainVerticle : AbstractVerticle(), CoroutineScope by CoroutineScope(Dispatchers.IO + SupervisorJob()) {
	private lateinit var client: MongoClient
	override fun start(startPromise: Promise<Void>) {
		println("thread: ${Thread.currentThread().name}")
		
		val router = Router.router(vertx)
		router.get("/").handler { req ->
			req.response()
				.putHeader("content-type", "text/plain")
				.end("index")
		}
		router.post("/history/illust/create")
			.consumes("application/json")
			.produces("application/json")
			.handler { it.parseJsonBody { json -> createIllustHistory(it, json) } }
		router.get("/illust/browser_history").handler(this::getIllustHistory)
		
		client = MongoClient.createShared(vertx, DBConfig.CONNECTION)
		vertx
			.createHttpServer()
			.requestHandler(router)
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
	 * 创建历史记录
	 */
	fun createIllustHistory(context: RoutingContext, json: String) {
		launch(CoroutineExceptionHandler { _, _ ->
			val res = BaseResponse(BaseResponse.CODE_SERVER_ERR, data = Any())
			context.response().end(JsonUtil.bean2Json(res))
		}) {
			val req = JsonUtil.json2Bean<HistoryReq>(json)
			req?.let {
				val illust = it.illust
				illust._id = illust.id
				client.saveAwait(DBConfig.ILLUST, JsonObject(JsonUtil.bean2Json(illust)))
				val historyId = "${illust.id}_${it.userId}"
				val history = History(historyId, it.userId, illust.id, System.currentTimeMillis())
				client.saveAwait(DBConfig.HISTORY, JsonObject(JsonUtil.bean2Json(history)))
				context.endJson(json { obj() }.toString())
				return@launch
			}
			val res = BaseResponse(BaseResponse.CODE_CLIENT_ERR, data = Any())
			context.endJson(JsonUtil.bean2Json(res))
		}
	}
	
	/**
	 * 获取历史记录
	 */
	fun getIllustHistory(context: RoutingContext) {
		launch(CoroutineExceptionHandler { _, _ ->
			val res = BaseResponse(BaseResponse.CODE_SERVER_ERR, data = Any())
			context.response().end(JsonUtil.bean2Json(res))
		}) {
			val userId = context.request().getParam("userId")
			val category = context.request().getParam("category")
			val query = json {
//				array(
//					obj(
//						"\$lookup" to obj(
//							"from" to "illust",
//							"localField" to "userId",
//							"foreignField" to "_id"
//							//"as" to
//						)
//					)
//				)
				obj()
			}
			val data: List<JsonObject> = client.findAwait(DBConfig.HISTORY, query)
			val res: BaseResponse<List<JsonObject>> = BaseResponse(BaseResponse.CODE_OK, data = data)
			val json = JsonUtil.bean2Json(res, Types.newParameterizedType(List::class.java, JsonObject::class.java))
			context.endJson(json)
		}
	}
}
