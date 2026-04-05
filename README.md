# Vault: Personal Finance Companion 🏦✨

Vault is a lightweight, intuitive mobile application designed to help users track their daily spending habits, visualize financial patterns, and build better saving routines. It is built entirely in Kotlin using modern Android development practices, including Jetpack Compose, MVVM architecture, and Room Database.

> **Note to Evaluators:** This project was built to demonstrate product thinking, clean UI/UX, and robust state management. It intentionally avoids feeling like a heavy, traditional banking app, focusing instead on everyday usability and habit-building.

## 📱 Core Features

* **Home Dashboard:** A glanceable overview of the user's current balance, total income, and total expenses, featuring a quick view of recent transactions.
* **Seamless Transaction Tracking:** A highly optimized data-entry screen allowing users to quickly log income or expenses with specific categories, dates, and optional notes.
* **Interactive Insights:** A visual breakdown of spending patterns utilizing dynamic charts to help users understand exactly where their money is going.
* **🔥 Creative Feature: "No-Spend Streak"** * *Product Thinking:* To make the app engaging and habit-forming, Vault includes an automated "No-Spend Streak" tracker. It calculates consecutive days without an expense logged, rewarding the user visually on the dashboard and gamifying the act of saving.

## 🛠️ Architecture & Tech Stack

This project follows the **Modern Android Development (MAD)** guidelines to ensure scalability, testability, and a clear separation of concerns.

* **UI Layer:** 100% **Jetpack Compose**. The UI is completely reactive, observing data streams from the ViewModel.
* **Architecture Pattern:** **MVVM (Model-View-ViewModel)**. Business logic is strictly separated from UI components.
* **State Management:** **Kotlin Coroutines & StateFlow**. The UI reacts instantly to database changes without requiring manual refreshes.
* **Data Layer (Local Storage):** **Room Database**. Used for persistent, offline-first data handling. DAOs return `Flow<List<T>>` to ensure a single, reactive source of truth.
* **Navigation:** **Compose Navigation** (`navigation-compose`) utilizing a structured bottom navigation bar and seamless screen transitions.

## 🎨 UX & Design Decisions

* **Explicit UI States:** The app handles "Empty States" gracefully. If a user has no transactions, the dashboard presents a friendly onboarding illustration rather than a blank screen.
* **Custom Inputs:** The "Add Transaction" screen features a specialized, massive numeric input field to make quick, on-the-go data entry as frictionless as possible.
* **Typography & Hierarchy:** Utilizes Material Design 3 principles with a clean, deep-blue and teal color palette to ensure numbers and critical data stand out clearly.

## 🚀 Setup & Installation

To run this project locally:

1. Clone the repository to your local machine.
2. Open the project in **Android Studio** (Koala or newer recommended).
3. Let Gradle sync the dependencies. 
   *(Note: This project uses KSP for Room. Ensure your `gradle.properties` allows Kotlin source sets if prompted).*
4. Build and run the app on an Android Emulator or a physical device running API Level 24 or higher.

## 🤔 Assumptions Made

* **Single Currency:** For simplicity and a lightweight UX, the app assumes a single, localized currency. Multi-currency support was deemed out of scope for a "lightweight companion."
* **Offline-First:** The app assumes the user wants instant, offline access to their data. Therefore, everything is handled securely on-device via Room SQLite, rather than simulating network latency with a mock API.

---
*Developed as a practical assessment of mobile engineering, UI/UX design, and product thinking.*
