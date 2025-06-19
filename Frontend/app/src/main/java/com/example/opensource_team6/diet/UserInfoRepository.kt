package com.example.opensource_team6.diet

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.opensource_team6.network.ApiConfig
import com.example.opensource_team6.util.TokenManager

class UserInfoRepository(private val context: Context) {
    fun fetchUserInfo(callback: (UserInfo?) -> Unit) {
        val token = TokenManager.getToken(context) ?: run {
            callback(null)
            return
        }
        val url = ApiConfig.BASE_URL + "/api/user/mypage"
        val req = object : JsonObjectRequest(Method.GET, url, null, { res ->
            val info = UserInfo(
                res.optString("name"),
                res.optString("gender"),
                res.optDouble("weight"),
                res.optDouble("targetWeight"),
                res.optInt("height"),
                res.optInt("age")
            )
            callback(info)
        }, { _ -> callback(null) }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }
        Volley.newRequestQueue(context).add(req)
    }
}
