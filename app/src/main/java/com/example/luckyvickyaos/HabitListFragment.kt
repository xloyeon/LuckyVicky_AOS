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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.luckyvickyaos.data.Habit
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Retrofit
import java.io.Serializable

class HabitListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_habit_list, container, false)

        // 버튼 텍스트를 "습관생성"으로 설정
        val createHabitButton = activity?.findViewById<Button>(R.id.btn_create_habit)
        createHabitButton?.text = "습관 생성"


        // 습관 목록을 서버에서 받아옵니다.
        val sharedPreferences =
            requireContext().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("user_id", -1)

        // 현재 진행 중인 습관 목록을 표시합니다.
        getCurrentHabitList(userId)

        return view
    }

    private fun getCurrentHabitList(id: Long) {
        // Retrofit 등을 사용하여 서버로부터 습관 목록을 받아옵니다.
        // HabitController의 /current 엔드포인트에 GET 요청을 보내어 목록을 받아올 수 있습니다.
        val url = "http://10.0.2.2:8080/app/habit/current?id=$id"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val habitListResponse =
                    Gson().fromJson(response.toString(), HabitListResponse::class.java)
                // 서버로부터 받아온 습관 목록을 화면에 표시
                displayHabitList(habitListResponse.habitResponseList)
            },
            Response.ErrorListener { error ->
                // 서버 오류 처리
                Log.e(ContentValues.TAG, "서버 전송 실패", error)
            }
        )

        // Volley를 사용하여 요청 보내기
        Volley.newRequestQueue(context).add(request)
    }

    private fun displayHabitList(habitList: List<Habit>) {
        val linearLayout = view?.findViewById<LinearLayout>(R.id.linearLayout_habitList)

        // HabitResponse 목록을 순회하면서 각각의 습관을 화면에 추가합니다.
        habitList.forEach { habit ->
            val habitLayout = LayoutInflater.from(context).inflate(R.layout.item_habit, null)

            // 습관 제목 텍스트뷰 설정
            val titleTextView = habitLayout.findViewById<TextView>(R.id.textView_habitTitle)
            titleTextView.text = habit.title

            // 습관 동물 이미지뷰 설정
            val animalImageView = habitLayout.findViewById<ImageView>(R.id.imageView_animal)
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
            animalImageView.setImageResource(animalDrawableResId)


            // 목표 버튼 레이아웃 설정
            val goalButtonLayout =
                habitLayout.findViewById<LinearLayout>(R.id.linearLayout_goalButtons)

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
            val weight = 1f // 각 버튼의 가중치

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
                goalButtonLayout.addView(button)
            }


            // 습관 항목 클릭 이벤트 추가
            habitLayout.setOnClickListener {
                // 클릭된 습관 항목의 ID를 사용하여 상세 페이지로 이동
                habit.id?.let { it1 -> navigateToHabitDetail(it1) }
            }

            // 습관 레이아웃을 화면에 추가합니다.
            linearLayout?.addView(habitLayout)
        }
    }

    // 습관 상세 페이지로 이동하는 함수
    private fun navigateToHabitDetail(habitId: Long) {
        // 습관 상세 페이지로 이동하는 코드
        val bundle = Bundle()
        bundle.putLong("habitId", habitId)

        val habitDetailFragment = HabitDetailFragment()
        habitDetailFragment.arguments = bundle

        fragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, habitDetailFragment)
            ?.addToBackStack(null)
            ?.commit()
    }
}

data class HabitListResponse(
    val habitResponseList: List<Habit>
)
