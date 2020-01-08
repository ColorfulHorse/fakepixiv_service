package com.lyj.fakepixiv.model.bean

import com.lyj.fakepixiv.model.request.Illust
import com.squareup.moshi.JsonClass

/**
 * @author green sun
 *
 * @date 2019/12/5
 *
 * @desc
 */
@JsonClass(generateAdapter = true)
data class HistoryReq(val userId: Long, val illust: Illust)

@JsonClass(generateAdapter = true)
data class History(val _id: String, val userId: Long, val illustId: Long, val view_time: Long)

@JsonClass(generateAdapter = true)
data class HistoryResp(val view_time: Long, val illust: Illust)