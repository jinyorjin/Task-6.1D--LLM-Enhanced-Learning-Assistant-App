package com.deakin.task61learningassistant

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class SetupFragment : Fragment() {

    // Variable to store the selected interest topic
    private var selectedInterest: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_setup, container, false)

        val etName = view.findViewById<EditText>(R.id.etName)
        val etHours = view.findViewById<EditText>(R.id.etHours)
        val btnSaveProfile = view.findViewById<Button>(R.id.btnSaveProfile)

        // 1. Connect all interest buttons (must match IDs in XML)
        val interestButtons = listOf(
            view.findViewById<Button>(R.id.btnAlgo),
            view.findViewById<Button>(R.id.btnDataStruct),
            view.findViewById<Button>(R.id.btnWebDev),
            view.findViewById<Button>(R.id.btnTesting),
            view.findViewById<Button>(R.id.btnMobileDev),
            view.findViewById<Button>(R.id.btnDatabase),
            view.findViewById<Button>(R.id.btnSecurity),
            view.findViewById<Button>(R.id.btnAI)
        )

        // 2. Set click listeners so only one topic can be selected at a time
        interestButtons.forEach { button ->
            button.setOnClickListener {

                // Save selected topic
                selectedInterest = button.text.toString()

                // Update UI: dim all buttons and highlight the selected one
                interestButtons.forEach { it.alpha = 0.4f }
                button.alpha = 1.0f

                Toast.makeText(context, "Topic Selected: $selectedInterest", Toast.LENGTH_SHORT).show()
            }
        }

        // If a name was passed from previous screen, fill it automatically
        val passedName = arguments?.getString("userName")
        val username = requireContext()
            .getSharedPreferences("user_profile", Context.MODE_PRIVATE)
            .getString("username", "")
            ?.trim()
            .orEmpty()
        val savedName = requireContext()
            .getSharedPreferences("user_profile", Context.MODE_PRIVATE)
            .getString("name_$username", "")

        when {
            !savedName.isNullOrEmpty() -> {
                etName.setText(savedName)
                etName.isEnabled = false
            }
            username.isNotEmpty() -> {
                etName.setText(username)
                etName.isEnabled = false
            }
            !passedName.isNullOrEmpty() -> etName.setText(passedName)
        }

        // 3. Handle Save & Next button
        btnSaveProfile.setOnClickListener {

            val name = etName.text.toString().trim()
            val hours = etHours.text.toString().trim()

            // Basic validation: name and interest must be selected
            if (name.isEmpty() || selectedInterest.isEmpty()) {
                Toast.makeText(requireContext(), "Please complete your profile first!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save user profile using SharedPreferences
            val prefs = requireActivity().getSharedPreferences("user_profile", Context.MODE_PRIVATE)
            prefs.edit().apply {
                putString("name_$username", name)
                putString("interest_$username", selectedInterest)
                putString("hours_$username", hours)
            }.apply()

            // Navigate to HomeFragment (dashboard screen)
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, HomeFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}