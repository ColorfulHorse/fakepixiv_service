package com.lyj.fakepixiv.app.utils

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.Type

/**
 * @author greensun
 *
 * @date 2019/8/15
 *
 * @desc
 */
object JsonUtil {

    val moshi: Moshi by lazy { Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }

    inline fun <reified T> bean2Json(source: T?, vararg types: Type): String {
        if (source == null) {
            return ""
        }
        val adapter = if (types.isNotEmpty()) {
            val type = Types.newParameterizedType(T::class.java, *types)
            moshi.adapter(type)
        }else {
            moshi.adapter(T::class.java)
        }
        return adapter.toJson(source)
    }

    inline fun <reified T> json2Bean(json: String): T? {
        val adapter = moshi.adapter(T::class.java)
        return adapter.fromJson(json)
    }
}