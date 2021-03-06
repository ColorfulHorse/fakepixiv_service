package com.lyj.fakepixiv.model.response

import com.squareup.moshi.JsonClass

/**
 * @author green sun
 *
 * @date 2019/11/27
 *
 * @desc
 */
@JsonClass(generateAdapter = true)
open class BaseResponse <T> (val code: Int, val data: T, val message: String = map.getOrElse(code){ MESSAGE_OK }) {
	companion object {
		const val CODE_OK = 200
		const val CODE_SERVER_ERR = 501
		const val CODE_CLIENT_ERR = 502
		
		const val MESSAGE_OK = "请求成功"
		const val MESSAGE_SERVER_ERR = "服务器异常"
		const val MESSAGE_CLIENT_ERR = "参数错误"
		
		val map = mapOf(
			CODE_OK to MESSAGE_OK,
			CODE_SERVER_ERR to MESSAGE_SERVER_ERR,
			CODE_CLIENT_ERR to MESSAGE_CLIENT_ERR
		)
	}
}
