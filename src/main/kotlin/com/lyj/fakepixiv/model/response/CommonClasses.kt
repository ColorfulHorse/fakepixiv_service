package com.lyj.fakepixiv.model.response

import com.squareup.moshi.JsonClass

/**
 * @author green sun
 *
 * @date 2019/12/5
 *
 * @desc
 */

@JsonClass(generateAdapter = true)
data class PatchResp(val version: Int = -1, val patch_version: Int = -1, val url: String = "", var hasPatch: Boolean = !url.isBlank())