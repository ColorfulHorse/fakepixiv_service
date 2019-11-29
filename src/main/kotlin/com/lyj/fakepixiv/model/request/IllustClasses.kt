package com.lyj.fakepixiv.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import javax.swing.text.html.HTML.Tag.BR

/**
 * Created by green sun on 2019/11/16.
 */
@JsonClass(generateAdapter = true)
data class HistoryReq(val userId: Long, val illust: Illust)

@JsonClass(generateAdapter = true)
data class History(val _id: String, val userId: Long, val illustId: Long, val view_time: Long)

@JsonClass(generateAdapter = true)
data class Illust(
	var _id: Long = -1,
	var caption: String = "",
	var create_date: String = "",
	val height: Int,
	var id: Long,
	var image_urls: ImageUrls = ImageUrls(),
	var is_bookmarked: Boolean = false,
	val is_muted: Boolean = false,
	val type: String,
	val meta_pages: List<MetaPage> = listOf(),
	val meta_single_page: MetaSinglePage = MetaSinglePage(),
	val page_count: Int = 0,
	val text_length: Int = 0,
	val restrict: Int = 0,
	val sanity_level: Int = 0,
	var series: Series? = null,
	var tags: List<Tag> = listOf(),
	var title: String = "",
	val tools: List<Any> = listOf(),
	var total_bookmarks: Int = 0,
	var total_view: Int = 0,
	val user: User = User(),
	val visible: Boolean = true,
	val width: Int = 0,
	val is_mypixiv_only: Boolean = false,
	val is_x_restricted: Boolean = false,
	val x_restrict: Int = 0 // 1 r-18
) {
	companion object {
		const val ILLUST = "illust"
		const val COMIC = "manga"
		const val NOVEL = "novel"
	}
}

@JsonClass(generateAdapter = true)
data class MetaSinglePage(
	val original_image_url: String = ""
)

@JsonClass(generateAdapter = true)
data class MetaPage(
	val image_urls: ImageUrls = ImageUrls()
)

@JsonClass(generateAdapter = true)
data class Tag(
	val name: String = "",
	val translated_name: String? = "",
	@Json(name = "added_by_uploaded_user")
	val byUser: Boolean = false,
	var isTranslated: Boolean = false
)

@JsonClass(generateAdapter = true)
data class Series(
	val id: Int = 0,
	val title: String = ""
)

@JsonClass(generateAdapter = true)
data class ImageUrls(
	val large: String = "",
	val medium: String = "",
	val square_medium: String = "",
	var original: String = ""
)

@JsonClass(generateAdapter = true)
data class User(
	val account: String = "",
	val id: Long = -1,
	val comment: String = "",
	val is_mail_authorized: Boolean = false,
	val is_premium: Boolean = false,
	val mail_address: String = "",
	val name: String = "",
	var is_followed: Boolean = false,
	val profile_image_urls: ProfileImageUrls = ProfileImageUrls(),
	val require_policy_agreement: Boolean = false,
	val x_restrict: Int = 0
)

@JsonClass(generateAdapter = true)
data class ProfileImageUrls(
	val px_16x16: String = "",
	val px_170x170: String = "",
	val px_50x50: String = "",
	val medium: String = ""
)
