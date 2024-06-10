package com.example.luckyvickyaos

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.converter.gson.GsonConverterFactory

class HabitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit)

        // Habit 목록을 표시할 Fragment를 추가합니다.
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HabitListFragment())
            .commit()

        // 습관 생성 버튼 클릭 시 CreateHabitFragment를 보여줍니다.
        val createHabitButton = findViewById<Button>(R.id.btn_create_habit)
        createHabitButton.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CreateHabitFragment())
                .addToBackStack(null)
                .commit()
        }

    }

}


