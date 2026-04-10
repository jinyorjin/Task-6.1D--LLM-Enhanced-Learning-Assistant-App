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

class LessonFragment : Fragment() {

    private val client = OkHttpClient()

    // Predefined lesson content used when AI is unavailable (fallback content)
    private val fallbackLessons = mapOf(
        "Algorithms" to "Algorithms are step-by-step procedures for solving problems. Big O notation measures their efficiency.",
        "Data Structures" to "Data structures organize data efficiently, such as stacks (LIFO) and queues (FIFO).",
        "Web Development" to "Web development uses HTML, CSS, and JavaScript to build interactive websites.",
        "Testing" to "Testing ensures code quality. Unit testing checks small parts, while integration testing checks full systems.",
        "Mobile Development" to "Mobile apps use components like Activities and Manifest files to manage structure and lifecycle.",
        "Database" to "Databases store data in tables. SQL is used to query and manage this data.",
        "Cybersecurity" to "Cybersecurity protects systems from attacks. Encryption helps secure sensitive data.",
        "Artificial Intelligence" to "AI enables machines to learn from data using techniques like machine learning."
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_lesson, container, false)

        // Get user's selected interest from saved profile
        val prefs = requireActivity().getSharedPreferences("user_profile", Context.MODE_PRIVATE)
        val interest = prefs.getString("interest", "Algorithms") ?: "Algorithms"

        val tvLesson = view.findViewById<TextView>(R.id.tvLessonContent)
        val tvAiResponse = view.findViewById<TextView>(R.id.tvAiResponse)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBarLesson)

        val btnSummary = view.findViewById<Button>(R.id.btnSummary)
        val btnHint = view.findViewById<Button>(R.id.btnHint)
        val btnExplain = view.findViewById<Button>(R.id.btnExplain)
        val btnBack = view.findViewById<Button>(R.id.btnBack)

        // 1. Personalization: show lesson content based on selected topic
        val lessonText = fallbackLessons[interest] ?: "Welcome to your personalized learning path."
        tvLesson.text = "Topic: $interest\n\n$lessonText"

        // 2. Summary button → Try AI first, fallback if API fails
        btnSummary.setOnClickListener {
            tvAiResponse.text = "AI is summarizing..."
            progressBar.visibility = View.VISIBLE

            val prompt = "Summarize this lesson about $interest in one short sentence:\n\n$lessonText"

            callGemini(prompt, { result ->
                progressBar.visibility = View.GONE
                tvAiResponse.text = "AI Summary: $result"
            }, {
                progressBar.visibility = View.GONE
                tvAiResponse.text = "[Offline Mode] Summary: A basic overview of $interest."
            })
        }

        // 3. Hint button → AI or fallback hint
        btnHint.setOnClickListener {
            tvAiResponse.text = "AI is generating a hint..."
            progressBar.visibility = View.VISIBLE

            val prompt = "Give one study hint for learning $interest."

            callGemini(prompt, { result ->
                progressBar.visibility = View.GONE
                tvAiResponse.text = "AI Hint: $result"
            }, {
                progressBar.visibility = View.GONE
                tvAiResponse.text = "[Offline Mode] Hint: Focus on the key concepts and try simple examples."
            })
        }

        // 4. Explain button → AI or fallback explanation
        btnExplain.setOnClickListener {
            tvAiResponse.text = "AI is explaining..."
            progressBar.visibility = View.VISIBLE

            val prompt = "Explain $interest in simple terms for a beginner."

            callGemini(prompt, { result ->
                progressBar.visibility = View.GONE
                tvAiResponse.text = "AI Explanation: $result"
            }, {
                progressBar.visibility = View.GONE
                tvAiResponse.text = "[Offline Mode] Explanation: $interest helps developers solve problems more efficiently."
            })
        }

        // Back button → return to previous screen
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    // Function to call Gemini API
    private fun callGemini(prompt: String, onResult: (String) -> Unit, onError: () -> Unit) {
        // 1. API 키 가져오기 및 디버깅 로그
        val apiKey = BuildConfig.GEMINI_API_KEY
        android.util.Log.d("GEMINI_DEBUG_LESSON", "API Call Start. Key Length: ${apiKey.length}")

        if (apiKey.isEmpty()) {
            android.util.Log.e("GEMINI_DEBUG_LESSON", "ERROR: API Key is missing!")
            activity?.runOnUiThread { onError() }
            return
        }

        // 2. JSON 데이터 생성 (Lesson 내용 요청용)
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

        // 3. ✨ [중요] Google AI Studio에서 확인한 최신 모델 주소로 수정
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent?key=$apiKey"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        // 4. API 실행
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                android.util.Log.e("GEMINI_DEBUG_LESSON", "NETWORK FAILURE: ${e.message}")
                activity?.runOnUiThread { onError() }
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyString = response.body?.string()

                if (!response.isSuccessful) {
                    android.util.Log.e("GEMINI_DEBUG_LESSON", "SERVER ERROR: ${response.code}")
                    android.util.Log.e("GEMINI_DEBUG_LESSON", "BODY: $bodyString")
                    activity?.runOnUiThread { onError() }
                    return
                }

                try {
                    val jsonResponse = JSONObject(bodyString ?: "")
                    val candidates = jsonResponse.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val outputText = candidates.getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text")

                        android.util.Log.d("GEMINI_DEBUG_LESSON", "SUCCESS: Lesson content received.")
                        activity?.runOnUiThread { onResult(outputText) }
                    } else {
                        activity?.runOnUiThread { onError() }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("GEMINI_DEBUG_LESSON", "JSON ERROR: ${e.message}")
                    activity?.runOnUiThread { onError() }
                }
            }
        })
    }}