package com.example.luckyvickyaos

import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.luckyvickyaos.data.Habit
import com.google.gson.Gson

class HabitDetailFragment : Fragment() {
    private var habitId: Long = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_habit_detail, container, false)

        // 버튼 텍스트를 "목록가기"로 설정
        val createHabitButton = activity?.findViewById<Button>(R.id.btn_create_habit)
        createHabitButton?.text = "목록가기"

        // 삭제 버튼 설정
        val deleteHabitButton = view.findViewById<Button>(R.id.btn_delete_habit)
        deleteHabitButton.setOnClickListener {
            deleteHabit()
            Toast.makeText(context, "삭제완료", Toast.LENGTH_SHORT).show()
        }

        // 전달된 습관의 ID
        habitId = arguments?.getLong("habitId", -1) ?: -1

        // 습관 상세 정보를 서버에서 받아와서 화면에 표시하는 함수 호출
        if (habitId != -1L) {
            val sharedPreferences = requireContext().getSharedPreferences("user_info", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getLong("user_id", -1)
            getHabitInfo(userId, habitId)
        }

        return view
    }

    private fun getHabitInfo(userId: Long, habitId: Long) {
        // Retrofit 등을 사용하여 서버로부터 습관 목록을 받아옵니다.
        // HabitController의 /current 엔드포인트에 GET 요청을 보내어 목록을 받아올 수 있습니다.
        val url = "http://10.0.2.2:8080/app/habit/$habitId?id=$userId"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val habit =
                    Gson().fromJson(response.toString(), Habit::class.java)
                // 서버로부터 받아온 습관 목록을 화면에 표시
                displayHabit(habit)
            },
            Response.ErrorListener { error ->
                // 서버 오류 처리
                Log.e(ContentValues.TAG, "서버 전송 실패", error)
            }
        )

        // Volley를 사용하여 요청 보내기
        Volley.newRequestQueue(context).add(request)
    }

    private fun displayHabit(habit: Habit) {
        val titleTextView = view?.findViewById<TextView>(R.id.textView_habitTitle)
        titleTextView?.text = habit.title

        val descriptionTextView = view?.findViewById<TextView>(R.id.textView_habitDescription)
        descriptionTextView?.text = habit.description

        val goalCountTextView = view?.findViewById<TextView>(R.id.textView_goalCount)
        goalCountTextView?.text = "목표 횟수: ${habit.goal_count}"

        val animalImageView = view?.findViewById<ImageView>(R.id.imageView_animal)
        val animalDrawableResId = when (habit.animal) {
            "북극곰" -> R.drawable.bear
            "아프리카 치타" -> R.drawable.cheetah
            "산호" -> R.drawable.coral
            "아프리카 코끼리" -> R.drawable.elephant
            "마운틴 고릴라" -> R.drawable.gorilla
            "수달" -> R.drawable.otter
            "판다" -> R.drawable.panda
            "벵갈 호랑이" -> R.drawable.tiger
            "바다거북" -> R.drawable.turtle
            else -> R.drawable.otter // 기본 아이콘 설정
        }
        animalImageView?.setImageResource(animalDrawableResId)

        // 색상 설정
        val habitLayout = view?.findViewById<LinearLayout>(R.id.habit_layout)

        val buttonWidth = resources.getDimensionPixelSize(R.dimen.button_width)
        val buttonHeight = resources.getDimensionPixelSize(R.dimen.button_height)
        val layoutParams = LinearLayout.LayoutParams(
            buttonWidth,
            buttonHeight
        ).apply {
            weight = 1f // 각 버튼의 가중치를 설정합니다.
            marginEnd =
                resources.getDimensionPixelSize(R.dimen.button_margin) // 각 버튼 사이의 간격을 설정합니다.
        }

        val goalCount = habit.goal_count ?: 0
        val count = habit.count ?: 0

        for (i in 0 until goalCount) {
            val button = Button(context)
            button.layoutParams = layoutParams

            button.text = (i + 1).toString()
            if (i < count) {
                val buttonColor = when (habit.color) {
                    "4" -> Color.parseColor("#FFA500")
                    "3" -> Color.BLUE
                    "1" -> Color.GREEN
                    else -> Color.YELLOW // 기본 색 설정
                }
                button.setBackgroundColor(buttonColor)
            } else {
                // 그 외 버튼은 회색으로 설정합니다.
                button.setBackgroundColor(Color.GRAY)

            }
            if (habitLayout != null) {
                habitLayout.addView(button)
            }
        }
    }

    private fun deleteHabit() {
        val sharedPreferences = requireContext().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("user_id", -1)
        val url = "http://10.0.2.2:8080/app/habit/$habitId?id=$userId"

        val request = JsonObjectRequest(
            Request.Method.DELETE, url, null,
            Response.Listener { response ->
                // 성공적으로 삭제된 경우 HabitListFragment로 이동
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragment_container, HabitListFragment())
                    ?.commit()
            },
            Response.ErrorListener { error ->
                Log.e(ContentValues.TAG, "서버 전송 실패", error)
            }
        )

        Volley.newRequestQueue(context).add(request)
    }
}