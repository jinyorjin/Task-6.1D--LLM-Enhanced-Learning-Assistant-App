package com.deakin.task61learningassistant

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class QuizFragment : Fragment() {

    private val client = OkHttpClient()

    //
    private val fallbackHints = mapOf(
        "Algorithms" to "Think about how we measure efficiency (Big O) and step-by-step logic.",
        "Data Structures" to "Consider how data is organized, like a stack of plates or a connected list.",
        "Web Development" to "Think about the structure of a webpage and how browsers style it.",
        "Testing" to "Focus on how we verify individual units of code or the whole system flow.",
        "Mobile Development" to "Consider the lifecycle of an app screen and how data moves between them.",
        "Database" to "Think about how data is stored in tables and how we uniquely identify rows.",
        "Cybersecurity" to "Consider methods of protecting data and common types of digital attacks.",
        "Artificial Intelligence" to "Think about how machines learn from data to make predictions."
    )

    // Fake AI data
    private val fallbackExplanations = mapOf(
        "correct" to "Excellent! Your answer perfectly aligns with the core principles of this field. You're showing great progress!",
        "incorrect" to "Not quite. In this topic, accuracy is key. Try reviewing the fundamental concepts and try again!"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_quiz, container, false)

        val prefs = requireActivity().getSharedPreferences("user_profile", Context.MODE_PRIVATE)
        val username = prefs.getString("username", "")?.trim().orEmpty()
        val interest = prefs.getString("interest_$username", "Algorithms") ?: "Algorithms"

        val tvQuestion = view.findViewById<TextView>(R.id.tvQuestion)
        val etAnswer = view.findViewById<EditText>(R.id.etAnswer)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)
        val btnHint = view.findViewById<Button>(R.id.btnHint)
        val tvResult = view.findViewById<TextView>(R.id.tvResult)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val historyStorage = HistoryStorage(requireContext())

        // 1. Personalization)
        val questions = when (interest) {
            "Algorithms" -> listOf(
                Pair("What is the time complexity of Binary Search?", "o(log n)"),
                Pair("Which algorithm is used for finding the shortest path?", "dijkstra")
            )
            "Data Structures" -> listOf(
                Pair("Which data structure uses LIFO (Last-In-First-Out)?", "stack"),
                Pair("What is a linked list made of?", "nodes")
            )
            "Web Development" -> listOf(
                Pair("What does HTML stand for?", "hypertext markup language"),
                Pair("Which CSS property is used to change text color?", "color")
            )
            "Testing" -> listOf(
                Pair("What is the main goal of Unit Testing?", "to test individual components"),
                Pair("What does TDD stand for?", "test driven development")
            )
            "Mobile Development" -> listOf(
                Pair("What is the main UI component of an Android app?", "activity"),
                Pair("Which file defines the app's structure and permissions?", "manifest")
            )
            "Database" -> listOf(
                Pair("What is used to uniquely identify each record in a table?", "primary key"),
                Pair("What does SQL stand for?", "structured query language")
            )
            "Cybersecurity" -> listOf(
                Pair("What is the practice of tricking users into giving private info?", "phishing"),
                Pair("What is the process of converting data into a secret code?", "encryption")
            )
            "Artificial Intelligence" -> listOf(
                Pair("What is the field where machines learn from data?", "machine learning"),
                Pair("Which AI structure is inspired by the human brain?", "neural network")
            )
            else -> listOf(
                Pair("What is the capital city of Australia?", "canberra"),
                Pair("Which city has the Opera House?", "sydney")
            )
        }

        val (questionText, correctAnswer) = questions.random()
        tvQuestion.text = questionText

        // Submit button: Attempt AI first -> Activate Fake AI on failure
        btnSubmit.setOnClickListener {
            val userAnswer = etAnswer.text.toString().trim().lowercase()
            val isCorrect = userAnswer == correctAnswer

            val promptText = "The student is studying $interest. For the question '$questionText', the student answered '$userAnswer'. " +
                    "Briefly explain why this is ${if (isCorrect) "correct" else "incorrect"} like a helpful tutor."
            fun handleFallbackResult(fallbackText: String) {
                historyStorage.saveHistory(HistoryItem(promptText, fallbackText, "Quiz Feedback"))
                navigateToResults(isCorrect, userAnswer, promptText, fallbackText)
            }

            progressBar.visibility = View.VISIBLE
            tvResult.text = "AI is analyzing your answer..."

            callGemini(promptText, { result ->
                //  AI sucess
                historyStorage.saveHistory(HistoryItem(promptText, result, "Quiz Feedback"))
                navigateToResults(isCorrect, userAnswer, promptText, result)
            }, {
                //  AI failed Fake AI
                val fakeAiResponse = if (isCorrect) fallbackExplanations["correct"] else fallbackExplanations["incorrect"]
                val fallback = "[Offline Mode] ${fakeAiResponse ?: "Please review the core concept and try again."}"
                handleFallbackResult(fallback)
            })
        }

        // Hint Button: AI First Attempt -> Display Fake AI upon failure
        btnHint.setOnClickListener {
            val prompt = "Give a very short, helpful hint for the question: $questionText. Do not reveal the answer."
            progressBar.visibility = View.VISIBLE

            callGemini(prompt, { result ->
                //  AI suceess
                progressBar.visibility = View.GONE
                tvResult.text = "✨ AI Hint: $result"
                historyStorage.saveHistory(HistoryItem(prompt, result, "Quiz Hint"))
            }, {
                // AI failed Fake Hint
                progressBar.visibility = View.GONE
                val fakeHint = fallbackHints[interest] ?: "Think about the core concepts of $interest!"
                val fallback = "💡 Fake Hint: $fakeHint"
                tvResult.text = fallback
                historyStorage.saveHistory(HistoryItem(prompt, fallback, "Quiz Hint"))
            })
        }

        return view
    }

    private fun navigateToResults(isCorrect: Boolean, answer: String, prompt: String, response: String) {
        activity?.runOnUiThread {
            view?.findViewById<ProgressBar>(R.id.progressBar)?.visibility = View.GONE
            val bundle = Bundle().apply {
                putBoolean("isCorrect", isCorrect)
                putString("userAnswer", answer)
                putString("aiPrompt", prompt)
                putString("aiResponse", response)
            }
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, ResultsFragment().apply { arguments = bundle })
                .addToBackStack(null)
                .commit()
        }
    }

    private fun callGemini(prompt: String, onResult: (String) -> Unit, onError: () -> Unit) {
        // 1. API 키 로드 및 디버깅 로그 (키가 비었는지 확인하는 것이 최우선!)
        val apiKey = BuildConfig.GEMINI_API_KEY
        android.util.Log.d("GEMINI_DEBUG", "API Call Start. Key Length: ${apiKey.length}")

        if (apiKey.isEmpty()) {
            android.util.Log.e("GEMINI_DEBUG", "CRITICAL ERROR: API Key is EMPTY! Please check local.properties and REBUILD.")
            activity?.runOnUiThread { onError() }
            return
        }

        // 2. JSON 데이터 생성
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


        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent?key=$apiKey"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        // 4. API 실행
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                android.util.Log.e("GEMINI_DEBUG", "NETWORK FAILURE (Check Internet): ${e.message}")
                activity?.runOnUiThread { onError() }
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyString = response.body?.string()

                if (!response.isSuccessful) {
                    // 여기서 또 404가 뜬다면 URL의 마침표나 공백 문제입니다.
                    android.util.Log.e("GEMINI_DEBUG", "SERVER ERROR CODE: ${response.code}")
                    android.util.Log.e("GEMINI_DEBUG", "SERVER ERROR BODY: $bodyString")
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

                        android.util.Log.d("GEMINI_DEBUG", "SUCCESS: Response received.")
                        activity?.runOnUiThread { onResult(outputText) }
                    } else {
                        android.util.Log.e("GEMINI_DEBUG", "AI BLOCKED CONTENT OR EMPTY RESPONSE")
                        activity?.runOnUiThread { onError() }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("GEMINI_DEBUG", "JSON PARSING ERROR: ${e.message}")
                    activity?.runOnUiThread { onError() }
                }
            }
        })
    }    }