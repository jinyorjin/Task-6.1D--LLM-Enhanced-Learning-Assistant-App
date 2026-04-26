<img width="383" height="600" alt="image" src="https://github.com/user-attachments/assets/99cc6bf7-5876-416c-afa8-ad196bc8e9bb" />
README
# LLM-Enhanced Learning Assistant App

This project is an upgraded version of my Task 6.1D application, further developed for Task 10.1D.  
In this version, I extended the original app by adding new features such as history tracking, sharing, and a simulated premium system to improve usability and overall learning experience.

This Android application is designed to support student learning using Large Language Models (LLMs). It provides a simple and interactive way for users to study based on their own interests, goals, and study habits.

## Features

- User profile setup (name, interest, goal, study hours)
- Interest selection using buttons for easier interaction
- AI-supported learning tools:
  - Lesson summary generation
  - Study hints
  - Explanation of answers (correct/incorrect)
- Quiz system with instant feedback
- AI-generated responses using the Gemini API
- Loading indicator and fallback handling when AI is unavailable
- History feature to review previous AI responses
- Share feature to share learning results
- Simulated premium feature for extended functionality

## App Flow

Welcome → Setup → Home → Lesson / Quiz → Results

## Technologies Used

- Kotlin (Android)
- Fragments (Single Activity Architecture)
- SharedPreferences (local data storage)
- OkHttp (API communication)
- Google Gemini API (LLM integration)

## Notes

- Dummy data is used for lessons and quizzes.
- If the AI service is unavailable or fails, the app provides fallback responses to ensure continuity.
