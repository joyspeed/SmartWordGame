# 🎮 Smart Word Game — משחק מילים חכם

A colorful, kid-friendly Android word game in Hebrew that helps children (age 7+) practice vocabulary using timed multiple-choice questions with adaptive learning.

Built with Kotlin + Jetpack Compose. Fully offline, no backend required.

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 📝 **Multiple-choice quiz** | Both word→meaning and meaning→word questions (50/50 split) |
| ⏱️ **3 difficulty levels** | Easy (60s), Medium (30s), Hard (15s) per question |
| 🧠 **Adaptive learning** | Weak words appear more frequently using weighted selection |
| 📊 **30-day activity summary** | Daily progress with color-coded accuracy bars |
| 📚 **Full dictionary** | Browse all 244 Hebrew vocabulary words with difficulty indicators |
| 💪 **Weak words practice** | Focused list of words the child struggles with |
| 🐻 **Mascot** | Friendly bear mascot reacts to correct/incorrect answers |
| 🔊 **Game sounds** | Cheerful melodies for correct, gentle tones for incorrect (3 variants each) |
| 🎨 **Material 3 UI** | Bright, accessible, fully RTL Hebrew interface |
| 🔒 **Parent-protected reset** | Progress reset requires a secret code |

## 📱 Screens

| Screen | Purpose |
|--------|---------|
| **Home** | Round setup — choose question count (10/20/30) and difficulty |
| **Game** | Timed quiz with HUD showing progress, score, and countdown |
| **Summary** | Round results — score, time, accuracy, and mistakes list |
| **Practice** | Weak words with difficulty stars and "practice hard words" button |
| **Dictionary** | Full vocabulary browser, sortable by alphabet or difficulty |
| **Activity** | 30-day history with accuracy-colored progress bars |
| **Settings** | Sound toggle, smart practice toggle, code-protected reset |
| **About** | App info with mascot |

Navigation uses a **Material side drawer** accessible from the home screen.

## 🛠️ Tech Stack

- **Language:** Kotlin 2.1
- **UI:** Jetpack Compose + Material 3
- **Architecture:** MVVM (ViewModel + StateFlow)
- **Build:** Gradle 8.14, compileSdk 36, minSdk 28, targetSdk 35
- **Storage:** SharedPreferences (local only, fully offline)
- **Testing:** JUnit 4 unit tests
- **CI:** GitHub Actions (build + test on push/PR)

## 🏗️ Build

**Prerequisites:** JDK 17, Android SDK

```bash
# Debug APK (~55 MB, unminified)
./gradlew :app:assembleDebug

# Release APK (~2.3 MB, R8 minified + signed)
./gradlew :app:assembleRelease

# Run unit tests
./gradlew :app:testDebugUnitTest
```

APK output: `app/build/outputs/apk/release/app-release.apk`

> **Note:** The release build is signed with the debug keystore for side-loading. For Play Store distribution, replace with a proper release keystore in `app/build.gradle.kts`.

## 🧪 Tests

18 unit tests covering core game logic:

- **HebrewUtilsTest** — niqqud stripping, alphabetical sorting without diacritics
- **QuestionGeneratorTest** — round generation, question types, weighted selection, edge cases, no duplicates

```bash
./gradlew :app:testDebugUnitTest
```

## 📁 Project Structure

```
app/src/main/
├── assets/
│   └── words.json                  # 244 Hebrew vocabulary words with niqqud
├── java/com/smartwordgame/app/
│   ├── MainActivity.kt             # NavHost with 8 routes
│   ├── data/
│   │   ├── WordRepository.kt       # JSON word loading
│   │   ├── QuestionGenerator.kt    # Quiz logic + adaptive weighting
│   │   ├── WeakWordsManager.kt     # Score tracking (0-3), SharedPreferences
│   │   ├── ActivityTracker.kt      # Daily stats (questions/correct per day)
│   │   ├── SettingsManager.kt      # Sound & smart practice toggles
│   │   ├── HebrewUtils.kt          # stripNiqqud() for proper sorting
│   │   ├── WordItem.kt             # Data class
│   │   └── RoundConfig.kt          # Round setup (count, difficulty, smart mode)
│   ├── game/
│   │   ├── GameViewModel.kt        # Timer, answer logic, pause/resume
│   │   └── GameState.kt            # Sealed: Loading / Playing / Finished
│   └── ui/
│       ├── HomeScreen.kt           # Drawer navigation + game setup
│       ├── GameScreen.kt           # Quiz with HUD, timer colors, BackHandler
│       ├── SummaryScreen.kt        # Round results + mistakes
│       ├── PracticeScreen.kt       # Weak words list + practice button
│       ├── DictionaryScreen.kt     # Full vocabulary browser
│       ├── ActivitySummaryScreen.kt # 30-day bar chart
│       ├── SettingsScreen.kt       # Toggles + code-protected reset
│       ├── AboutScreen.kt          # App info + mascot
│       ├── SoundManager.kt         # ToneGenerator audio (3 variants each)
│       └── theme/                  # Color palette, Material theme, typography
├── res/
│   ├── mipmap-*/                   # App icon (Hebrew Aleph א on red)
│   └── values/                     # Strings, themes, icon background
app/src/test/
└── java/com/smartwordgame/app/data/
    ├── HebrewUtilsTest.kt
    └── QuestionGeneratorTest.kt
.github/workflows/
└── android-ci.yml                  # CI: test + build on push/PR
```

## 🎮 How the Game Works

1. **Setup** — Choose number of questions (10/20/30) and difficulty (easy/medium/hard)
2. **Play** — Each question shows a word or meaning with 4 multiple-choice options
3. **Timer** — Countdown with color changes (blue → orange → red) based on difficulty
4. **Feedback** — Correct: confetti + cheerful sound. Wrong: gentle sound + correct answer highlighted
5. **Summary** — See score, time, accuracy percentage, and all mistakes
6. **Adaptive** — Words you get wrong score higher (0→3) and appear more frequently in future rounds

## 📋 Specification

See [SPEC.md](SPEC.md) for the full product specification (26 sections covering all features, UX requirements, and acceptance criteria).

## 📄 License

Private project.
