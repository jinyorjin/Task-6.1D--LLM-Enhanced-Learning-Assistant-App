package com.deakin.task61learningassistant

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class ProgressFragment : Fragment() {

    private val client = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_progress, container, false)

        val tvPlan1 = view.findViewById<TextView>(R.id.tvPlanPart1)
        val tvPlan2 = view.findViewById<TextView>(R.id.tvPlanPart2)
        val tvPlan3 = view.findViewById<TextView>(R.id.tvPlanPart3)

        val pbLoading = view.findViewById<ProgressBar>(R.id.pbLoadingPlan)
        val btnBack = view.findViewById<Button>(R.id.btnBackHome)

        val prefs = requireActivity().getSharedPreferences("user_profile", Context.MODE_PRIVATE)
        val interest = prefs.getString("interest", "General") ?: "General"

        // AI에게 데이터를 3개의 파트로 나눠달라고 요청 (카드 디자인을 위해)
        val planPrompt = "Suggest a 7-day study plan for $interest. Provide it in 3 clear parts (Days 1-2, Days 3-5, Days 6-7). Keep each part very short."

        pbLoading.visibility = View.VISIBLE

        callGemini(planPrompt, { result ->
            pbLoading.visibility = View.GONE
            // 텍스트를 줄바꿈 기준으로 나눠서 카드에 분배 (간단한 파싱)
            val parts = result.split("\n\n")
            tvPlan1.text = parts.getOrNull(0) ?: "Days 1-2: Start with basics"
            tvPlan2.text = parts.getOrNull(1) ?: "Days 3-5: Deep dive into $interest"
            tvPlan3.text = parts.getOrNull(2) ?: "Days 6-7: Practice and review"
        }, {
            pbLoading.visibility = View.GONE
            tvPlan1.text = "Days 1-2: Basics of $interest"
            tvPlan2.text = "Days 3-5: Practical Implementation"
            tvPlan3.text = "Days 6-7: Final Projects & Review"
        })

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    // callGemini 함수는 기존과 동일하게 유지...
    private fun callGemini(prompt: String, onResult: (String) -> Unit, onError: () -> Unit) {
        val json = JSONObject().apply {
            put("contents", org.json.JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", org.json.JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    })
                })
            })
        }
        val requestBody = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${BuildConfig.GEMINI_API_KEY}")
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { activity?.runOnUiThread { onError() } }
            override fun onResponse(call: Call, response: Response) {
                val bodyString = response.body?.string()
                if (!response.isSuccessful || bodyString == null) {
                    activity?.runOnUiThread { onError() }
                    return
                }
                try {
                    val outputText = JSONObject(bodyString).getJSONArray("candidates")
                        .getJSONObject(0).getJSONObject("content")
                        .getJSONArray("parts").getJSONObject(0).getString("text")
                    activity?.runOnUiThread { onResult(outputText) }
                } catch (e: Exception) { activity?.runOnUiThread { onError() } }
            }
        })
    }
}