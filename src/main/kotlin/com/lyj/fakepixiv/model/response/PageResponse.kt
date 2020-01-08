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
class PageResponse <T> (code: Int, data: T, val pageNo: Int = 1, val pageSize: Int = 20, val total: Long = 0):
	BaseResponse<T>(code, data) {
	companion object {
		const val CODE_OK = 200
		const val CODE_SERVER_ERR = 500
		const val CODE_CLIENT_ERR = 400
		
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
