package com.example.opensw_metronome.model

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


// class는 이미 완성, data 추가 == class 호출은 metronomefragment, practcielogfragment.kt 에서 실행
data class Data_PracticeLog(
    val when_now: LocalDateTime,    // 로그가 기록된 날짜시간
    val what_BPM: Int,              // 몇 BPM으로 연습했는가?
    val time_goal: Int,             // 타이머: 목표
    val time_how: Int,              // 타이머: 실제 실행시간
    val what_scale: String,         // 어떤 스케일에서
    val rating: Int                 // user 직접 점수 매겨서 평가
)


object PracticeLogRepository {
    private val log_list = mutableListOf<Data_PracticeLog>()
    val logs: List<Data_PracticeLog> get() = log_list

    fun add_log(entry: Data_PracticeLog) {
        log_list.add(0, entry)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createGson() = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java,
            JsonSerializer<LocalDateTime> { src, _, _ ->
                JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            }
        )
        .registerTypeAdapter(LocalDateTime::class.java,
            JsonDeserializer<LocalDateTime> { json, _, _ ->
                LocalDateTime.parse(json.asString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            }
        )
        .create()


    @RequiresApi(Build.VERSION_CODES.O)
    fun saveToPrefs(context: Context) {
        val prefs = context.getSharedPreferences("log_store", Context.MODE_PRIVATE)
        val gson  = createGson()
        val json  = gson.toJson(log_list)
        prefs.edit().putString("logs_json", json).apply()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun loadFromPrefs(context: Context) {
        val prefs     = context.getSharedPreferences("log_store", Context.MODE_PRIVATE)
        val savedJson = prefs.getString("logs_json", "[]") ?: "[]"
        val gson      = createGson()
        val type      = object : TypeToken<List<Data_PracticeLog>>() {}.type
        val list: List<Data_PracticeLog> = gson.fromJson(savedJson, type)
        log_list.clear()
        log_list.addAll(list)
    }
}