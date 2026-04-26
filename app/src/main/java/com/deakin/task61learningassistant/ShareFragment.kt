package com.deakin.task61learningassistant

import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class ShareFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_share, container, false)
        val tvPreview = view.findViewById<TextView>(R.id.tvSharePreview)
        val tvShareName = view.findViewById<TextView>(R.id.tvShareName)
        val tvShareStats = view.findViewById<TextView>(R.id.tvShareStats)
        val btnShareNow = view.findViewById<Button>(R.id.btnShareNow)
        val btnBack = view.findViewById<Button>(R.id.btnBackFromShare)

        val profilePrefs = requireContext().getSharedPreferences("user_profile", Context.MODE_PRIVATE)
        val username = profilePrefs.getString("username", "")?.trim().orEmpty()
        val name = profilePrefs.getString("name_$username", username.ifEmpty { "Student" }) ?: "Student"
        val historyItems = HistoryStorage(requireContext()).getAllHistory()
        val quizItems = historyItems.filter { it.featureType == "Quiz Feedback" }
        val total = quizItems.size
        val correct = quizItems.count { it.response.contains("correct", ignoreCase = true) && !it.response.contains("incorrect", ignoreCase = true) }
        tvShareName.text = name
        tvShareStats.text = "Total Questions: $total\nCorrect: $correct\nIncorrect: ${total - correct}"

        val shareText = buildShareText(total, correct)

        tvPreview.text = shareText
        btnShareNow.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, "Share profile"))
        }

        btnBack.setOnClickListener { parentFragmentManager.popBackStack() }

        return view
    }

    private fun buildShareText(total: Int, correct: Int): String {
        return "My Learning Assistant Progress:\n" +
            "Total Questions: $total\n" +
            "Correct: $correct\n" +
            "Try this app!"
    }
}
