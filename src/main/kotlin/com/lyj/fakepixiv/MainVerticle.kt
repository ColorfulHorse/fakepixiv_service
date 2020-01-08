package com.lyj.fakepixiv

import com.lyj.fakepixiv.app.utils.JsonUtil
import com.lyj.fakepixiv.model.bean.History
import com.lyj.fakepixiv.model.bean.HistoryReq
import com.lyj.fakepixiv.model.bean.HistoryResp
import com.lyj.fakepixiv.model.request.Illust
import com.lyj.fakepixiv.model.response.*
import com.lyj.fakepixiv.util.*
import com.squareup.moshi.Types
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.*
import io.vertx.kotlin.ext.mongo.saveAwait
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

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
		router.post("/history/illust/create").handler { it.parseJsonBody { json -> createIllustHistory(it, json) } }
		router.get("/illust/browser_history").handler(this::getIllustHistory)
		router.get("/app/checkPatch").handler(this::checkPatch)
		
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
	
	private fun checkPatch(context: RoutingContext) {
		launch(CoroutineExceptionHandler { _, _ ->
			val resp = BaseResponse(BaseResponse.CODE_SERVER_ERR, Any())
			context.endJson(JsonUtil.bean2Json(resp))
		}) {
			val version = context.request().getParam("version").toInt()
			val patchVersion = context.request().getParam("patch_version").toInt()
			val query = AggregateLINQ.create(DBConfig.VERSIONS)
				.match(
					"version" to version,
					"patch_version" to patchVersion
				)
			val json = client.aggregateOneAwait(DBConfig.VERSIONS, query.pipeline)
			val resp: BaseResponse<PatchResp> =
			if (json != null) {
				BaseResponse(BaseResponse.CODE_OK, JsonUtil.json2Bean<PatchResp>(json.toString()).apply {
					hasPatch = true
				})
			}else {
				BaseResponse(BaseResponse.CODE_OK, PatchResp())
			}
			val jsonStr = JsonUtil.bean2Json(resp, PatchResp::class.java)
			context.endJson(jsonStr)
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
			val illust = req.illust
			illust._id = illust.id
			client.saveAwait(DBConfig.ILLUST, JsonObject(JsonUtil.bean2Json(illust)))
			val historyId = "${illust.id}_${req.userId}"
			val history =
				History(historyId, req.userId, illust.id, System.currentTimeMillis())
			val date = json {
				obj(
					"\$date" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault()).format(
						Date(history.view_time)
					)
				)
			}
			client.saveAwait(DBConfig.HISTORY, JsonObject(JsonUtil.bean2Json(history)).apply {
				put("view_date", date)
			})
			context.endJson(json { obj() }.toString())
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
			val threadName = Thread.currentThread().name
			var pageNo = 1
			var pageSize = 20
			pageNo = context.request().getParam("pageNo").toInt()
			pageSize = context.request().getParam("pageSize").toInt()
			val userId = context.request().getParam("userId").toInt()
			val category = context.request().getParam("category")
			val original = AggregateLINQ.create(DBConfig.HISTORY)
				.lookup("illust", "illustId", "_id", "illust")
				.match(
					"userId" to userId,
					"illust.type" to if (category == Illust.NOVEL) Illust.NOVEL else jsonObjectOf(
						"\$in" to jsonArrayOf(
							Illust.ILLUST,
							Illust.COMIC
						)
					)
				)
				.unwind("illust")
				.project(
					"view_time" to "\$view_time",
					"illust" to "\$illust"
				)
			val counter = original.copy()
			val total = client.aggregateCountAwait(DBConfig.HISTORY, counter.pipeline)
			val query = original.copy()
				.skip((pageNo - 1) * pageSize)
				.limit(pageSize)
				.sort("view_time", -1)
			val data: List<HistoryResp> = client
				.aggregateAwait(DBConfig.HISTORY, query.pipeline)
				.map { JsonUtil.json2Bean<HistoryResp>(it.toString()) }
			val res: PageResponse<List<HistoryResp>> = PageResponse(BaseResponse.CODE_OK, data, pageNo, pageSize, total)
			val json = JsonUtil.bean2Json(res, Types.newParameterizedType(List::class.java, HistoryResp::class.java))
			context.endJson(json)
		}
	}
}
