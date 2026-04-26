<img width="383" height="600" alt="image" src="https://github.com/user-attachments/assets/99cc6bf7-5876-416c-afa8-ad196bc8e9bb" />
README
LLM-Enhanced Learning Assistant App
This project is an upgraded version of Task 6.1D, extended for Task 10.1D with additional features including history tracking, sharing functionality, and premium simulation.

This is an Android application designed to support students’ learning using Large Language Models (LLMs). The app provides personalised learning experiences based on the user’s interests, goals, and study habits.

Features
User profile setup (name, interest, goal, study hours)
Interest selection using buttons
AI-powered learning tools:
Lesson summary generation
Study hints
Explanation of answers (correct/incorrect)
Quiz system with instant feedback
AI-generated responses using Gemini API
Loading and fallback handling when AI is unavailable
App Flow

Welcome → Setup → Home → Lesson / Quiz → Results

Technologies Used
Kotlin (Android)
Fragments (Single Activity Architecture)
SharedPreferences (local data storage)
OkHttp (API calls)
Google Gemini API (LLM integration)
Notes
Dummy data is used for lessons and quizzes.
AI responses may fall back to local data if the API fails.
