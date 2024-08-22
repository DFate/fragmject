package com.example.fragment.project.data

import android.util.Log
import com.example.fragment.project.R
import com.example.miaow.base.http.HttpResponse
import com.google.gson.Gson
import kotlinx.parcelize.IgnoredOnParcel

data class Login(
    val data: User? = null
) : HttpResponse()

data class Register(
    val data: User? = null
) : HttpResponse()

data class User @JvmOverloads constructor(
    val admin: String = "",
    val chapterTops: List<Any>? = null,
    var coinCount: String = "",
    val collectIds: List<Int>? = null,
    val email: String = "",
    val icon: String = "",
    val id: String = "",
    val nickname: String = "",
    val password: String = "",
    val publicName: String = "",
    val token: String = "",
    val type: String = "",
    var avatar: String = "",
    var sex: String = "",
    var birthday: String = "",
    var city: String = "",
    val username: String = ""
) {

    @IgnoredOnParcel
    val avatarList: List<Int> = listOf(
        R.mipmap.avatar_1_raster,
        R.mipmap.avatar_2_raster,
        R.mipmap.avatar_3_raster,
        R.mipmap.avatar_4_raster,
        R.mipmap.avatar_5_raster,
        R.mipmap.avatar_6_raster,
    )

    fun getAvatarId(): Int {
        var index = 0
        try {
            val id = id.toInt()
            if (id >= 0) {
                index = id % 6
            }
        } catch (e: Exception) {
            Log.e(this.javaClass.name, e.message.toString())
        }
        return avatarList[index]
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }

    fun isLogin(): Boolean {
        return id.isBlank()
    }

}