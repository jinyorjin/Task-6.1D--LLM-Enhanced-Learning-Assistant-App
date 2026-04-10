package com.deakin.task61learningassistant

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class ResultsFragment : Fragment(R.layout.fragment_results) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvResultStatus = view.findViewById<TextView>(R.id.tvResultStatus)
        val tvUserAnswer = view.findViewById<TextView>(R.id.tvUserAnswer)
        val tvAiPrompt = view.findViewById<TextView>(R.id.tvAiPrompt)
        val tvAiResponse = view.findViewById<TextView>(R.id.tvAiResponse)
        val btnGoHome = view.findViewById<Button>(R.id.btnGoHome)

        // Retrieve data passed from QuizFragment
        val isCorrect = arguments?.getBoolean("isCorrect") ?: false
        val userAnswer = arguments?.getString("userAnswer") ?: ""
        val aiPrompt = arguments?.getString("aiPrompt") ?: "No prompt log available"
        val aiResponse = arguments?.getString("aiResponse") ?: "No AI response available"

        // Update UI with quiz result
        tvResultStatus.text = if (isCorrect) "Result: Correct 🎉" else "Result: Incorrect ❌"

        // Display the user's answer
        tvUserAnswer.text = "Your answer: $userAnswer"

        // Requirement: show both the LLM prompt and response
        tvAiPrompt.text = "LLM Prompt: $aiPrompt"
        tvAiResponse.text = aiResponse

        // Button to return to the Home screen
        btnGoHome.setOnClickListener {

            // Navigate back to HomeFragment with a smooth transition
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, HomeFragment())
                .commit() // Not adding to back stack to avoid returning to results screen
        }
    }
}