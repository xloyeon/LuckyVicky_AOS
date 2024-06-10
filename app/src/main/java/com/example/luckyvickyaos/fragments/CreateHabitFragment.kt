package com.example.luckyvickyaos.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.luckyvickyaos.HabitActivity
import com.example.luckyvickyaos.R
import org.json.JSONObject

class CreateHabitFragment : Fragment() {

    private var selectedAnimal: String? = null
    private var selectedColor: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_habit, container, false)

        val createHabitButton = activity?.findViewById<Button>(R.id.btn_create_habit)
        createHabitButton?.text = "목록가기"

        // View 요소 초기화
        val titleEditText = view.findViewById<EditText>(R.id.editText_habitTitle)
        val descriptionEditText = view.findViewById<EditText>(R.id.editText_habitDescription)
        val goalCountEditText = view.findViewById<EditText>(R.id.editText_goalCount)
        val selectAnimalButton = view.findViewById<Button>(R.id.button_selectAnimal)
        val selectColorButton = view.findViewById<Button>(R.id.button_selectColor)
        val createButton = view.findViewById<Button>(R.id.button_createHabit)

        selectAnimalButton.setOnClickListener {
            val animals = arrayOf("북극곰", "판다", "수달", "바다거북", "마운틴 고릴라", "산호", "벵갈 호랑이", "아프리카 치타", "아프리카 코끼리")
            showSelectionDialog("Select Animal", animals) { animal ->
                selectedAnimal = animal
            }
        }

        // 색상 선택 버튼 클릭 시
        selectColorButton.setOnClickListener {
            val colors = arrayOf("Green", "Yellow", "Blue", "Orange")
            showSelectionDialog("Select Color", colors) { color ->
                selectedColor = when (color) {
                    "Green" -> 1
                    "Yellow" -> 2
                    "Blue" -> 3
                    "Orange" -> 4
                    else -> null
                }
            }
        }

        // 생성 버튼 클릭 시 이벤트 처리
        createButton.setOnClickListener {

            // 사용자가 선택한 값 확인
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val goalCount = goalCountEditText.text.toString().toInt() // 예외 처리 필요

            // 사용자가 선택한 동물과 색상 값 확인
            val selectedAnimal = selectedAnimal
            val selectedColor = selectedColor

            // 습관 생성 요청 생성 및 서버에 전송
            createHabit(title, description, goalCount, selectedAnimal, selectedColor)
        }

        return view
    }

    private fun handleCreateSuccess() {
        // 습관 생성 성공 시 HabitActivity로 전환
        val intent = Intent(requireContext(), HabitActivity::class.java).apply {
            // 새로운 습관 생성 여부를 전달
            putExtra("newHabitCreated", true)
        }
        startActivity(intent)
    }

    private fun showSelectionDialog(title: String, items: Array<String>, callback: (String) -> Unit) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
            .setItems(items) { _, which ->
                val selectedItem = items[which]
                callback(selectedItem)
            }
        builder.create().show()
    }

    private fun createHabit(title: String, description: String, goalCount: Int, animal: String?, color: Int?) {
        // 서버에 POST 요청 보내기
        val requestBody = JSONObject().apply {
            put("title", title)
            put("description", description)
            put("goal_count", goalCount)
            put("animal", animal)
            put("color", color)
        }

        val sharedPreferences = requireContext().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("user_id", -1)

        val requestUrl = "http://10.0.2.2:8080/app/habit?id=$userId"
        val request = JsonObjectRequest(
            Request.Method.POST, requestUrl, requestBody,
            Response.Listener { response ->
                // 요청 성공 시 처리
                requireActivity().supportFragmentManager.popBackStack()
            },
            Response.ErrorListener { error ->
                // 요청 실패 시 처리
                // 오류 메시지 표시 등
            }
        )

        // Volley 라이브러리를 사용하여 요청 큐에 요청 추가
        Volley.newRequestQueue(requireContext()).add(request)
    }
}