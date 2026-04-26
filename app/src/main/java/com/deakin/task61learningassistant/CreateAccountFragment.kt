package com.deakin.task61learningassistant

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class CreateAccountFragment : Fragment(R.layout.fragment_create_account) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etName = view.findViewById<EditText>(R.id.etName)
        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val btnCreateAccount = view.findViewById<Button>(R.id.btnCreateAccount)
        val btnBackToLogin = view.findViewById<Button>(R.id.btnBackToLogin)

        btnCreateAccount.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                val prefs = requireContext().getSharedPreferences("user_profile", Context.MODE_PRIVATE)
                prefs.edit().putString("username", name).apply()

                val bundle = Bundle().apply {
                    putString("userName", name)
                    putString("userEmail", email)
                }

                val setupFragment = SetupFragment()
                setupFragment.arguments = bundle

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, setupFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        btnBackToLogin.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}