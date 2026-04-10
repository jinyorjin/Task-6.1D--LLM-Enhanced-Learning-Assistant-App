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

        val btnStartTask = view.findViewById<Button>(R.id.btnStartTask)
        val btnLessons = view.findViewById<Button>(R.id.btnLessons)
        val btnProgress = view.findViewById<Button>(R.id.btnProgress)

        val prefs = requireActivity().getSharedPreferences("user_profile", Context.MODE_PRIVATE)

        val name = prefs.getString("name", "Student") ?: "Student"
        val interest = prefs.getString("interest", "General Learning") ?: "General Learning"
        val hours = prefs.getString("hours", "0") ?: "0"

        // UI text
        tvWelcome.text = "Hello, $name"
        tvInterest.text = "Focus topic: $interest"
        tvHours.text = "Goal: $hours hours per week"

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

        return view
    }
}