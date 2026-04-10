package com.deakin.task61learningassistant

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the main layout for the activity
        setContentView(R.layout.activity_main)

        // Load the initial screen only when the activity is first created
        // This prevents fragment duplication when the device rotates
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, WelcomeFragment())
                .commit()
        }
    }
}