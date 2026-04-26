package com.deakin.task61learningassistant

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {
    private var tvPlanStatus: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val tvWelcome = view.findViewById<TextView>(R.id.tvWelcome)
        val tvInterest = view.findViewById<TextView>(R.id.tvInterest)
        val tvHours = view.findViewById<TextView>(R.id.tvHours)
        val tvTaskDesc = view.findViewById<TextView>(R.id.tvTaskDesc)
        tvPlanStatus = view.findViewById(R.id.tvPlanStatus)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)
        val tvTotalQuestions = view.findViewById<TextView>(R.id.tvTotalQuestions)
        val tvCorrectCount = view.findViewById<TextView>(R.id.tvCorrectCount)
        val tvIncorrectCount = view.findViewById<TextView>(R.id.tvIncorrectCount)

        val btnStartTask = view.findViewById<Button>(R.id.btnStartTask)
        val btnLessons = view.findViewById<Button>(R.id.btnLessons)
        val btnProgress = view.findViewById<Button>(R.id.btnProgress)
        val btnHistory = view.findViewById<Button>(R.id.btnHistory)
        val btnShare = view.findViewById<Button>(R.id.btnShare)
        val btnShareProfileLarge = view.findViewById<Button>(R.id.btnShareProfileLarge)
        val btnPremium = view.findViewById<Button>(R.id.btnPremium)

        val prefs = requireActivity().getSharedPreferences("user_profile", Context.MODE_PRIVATE)
        val username = getCurrentUsername()

        val name = prefs.getString("name_$username", username.ifEmpty { "Student" }) ?: "Student"
        val email = prefs.getString("email", "student@deakin.edu.au") ?: "student@deakin.edu.au"
        val interest = prefs.getString("interest_$username", "General Learning") ?: "General Learning"
        val hours = prefs.getString("hours_$username", "0") ?: "0"

        // UI text
        tvWelcome.text = "Hello, $name"
        tvEmail.text = email
        tvInterest.text = "Focus topic: $interest"
        tvHours.text = "Goal: $hours hours per week"
        refreshPlanStatus()
        val historyItems = HistoryStorage(requireContext()).getAllHistory()
        val quizItems = historyItems.filter { it.featureType == "Quiz Feedback" }
        val totalQuestions = quizItems.size
        val correctCount = quizItems.count { it.response.contains("correct", ignoreCase = true) && !it.response.contains("incorrect", ignoreCase = true) }
        val incorrectCount = (totalQuestions - correctCount).coerceAtLeast(0)
        tvTotalQuestions.text = totalQuestions.toString()
        tvCorrectCount.text = correctCount.toString()
        tvIncorrectCount.text = incorrectCount.toString()

        // AI recomo
        tvTaskDesc.text = "Based on your interest in $interest, we've prepared a custom quiz task."


        btnStartTask.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.fragment_container, QuizFragment())
                .addToBackStack(null)
                .commit()
        }

        // 나머지 버튼 기능 유지
        btnLessons.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.fragment_container, LessonFragment()).addToBackStack(null).commit()
        }

        btnProgress.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.fragment_container, ProgressFragment()).addToBackStack(null).commit()
        }

        btnHistory.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HistoryFragment())
                .addToBackStack(null)
                .commit()
        }

        btnShare.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ShareFragment())
                .addToBackStack(null)
                .commit()
        }
        btnShareProfileLarge.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ShareFragment())
                .addToBackStack(null)
                .commit()
        }

        btnPremium.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PremiumFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        refreshPlanStatus()
    }

    private fun refreshPlanStatus() {
        // Always read current premium state when Home is visible.
        val premiumKey = getPremiumKey()
        val isPremium = requireContext()
            .getSharedPreferences("premium_prefs", Context.MODE_PRIVATE)
            .getBoolean(premiumKey, false)
        tvPlanStatus?.text = if (isPremium) "Plan: Premium" else "Plan: Free"
    }

    private fun getPremiumKey(): String {
        val username = getCurrentUsername()
        return "is_premium_$username"
    }

    private fun getCurrentUsername(): String {
        return requireContext()
            .getSharedPreferences("user_profile", Context.MODE_PRIVATE)
            .getString("username", "")
            ?.trim()
            .orEmpty()
    }
}