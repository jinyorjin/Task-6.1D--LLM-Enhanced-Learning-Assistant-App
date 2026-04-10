package com.deakin.task61learningassistant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class WelcomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_welcome, container, false)

        val btnStart = view.findViewById<Button>(R.id.btnStart)
        val btnCreateAccount = view.findViewById<TextView>(R.id.btnCreateAccount)

        // Login 버튼 클릭 시 (가이드라인에 따라 Setup 혹은 Home으로 이동)
        btnStart.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, SetupFragment()) // 최초 접속이면 Setup으로
                .addToBackStack(null)
                .commit()
        }

        // Need an account? 클릭 시 회원가입 화면으로
        btnCreateAccount.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.fragment_container, CreateAccountFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}