# 🎮 Smart Word Game (משחק מילים חכם)

A colorful, kid-friendly Android word game in Hebrew that helps children (age 7+) practice vocabulary using timed multiple-choice questions with adaptive learning.

## Features

- 📝 **Multiple-choice quiz** — word→meaning and meaning→word questions
- ⏱️ **3 difficulty levels** — Easy (60s), Medium (30s), Hard (15s) per question
- 🧠 **Adaptive learning** — weak words appear more frequently
- 📊 **30-day activity summary** — track daily progress with visual charts
- 📚 **Full dictionary browser** — explore all 244 vocabulary words
- 💪 **Weak words list** — focused practice on difficult words
- 🐻 **Mascot feedback** — encouraging bear mascot reactions
- 🔊 **Game-like sounds** — cheerful melodies for correct, gentle tones for incorrect
- 🎨 **Material 3 design** — bright, accessible, RTL Hebrew UI throughout

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Build:** Gradle 8.14, compileSdk 36, minSdk 28
- **Architecture:** MVVM with ViewModel + StateFlow
- **Storage:** SharedPreferences (local only, no backend)

## Build

```bash
# Debug APK
./gradlew :app:assembleDebug

# Release APK (signed with debug keystore)
./gradlew :app:assembleRelease
```

APK output: `app/build/outputs/apk/release/`

## Project Structure

```
app/src/main/
├── assets/words.json              # 244 Hebrew vocabulary words
├── java/com/smartwordgame/app/
│   ├── MainActivity.kt            # Navigation hub (8 routes)
│   ├── data/                      # Data layer
│   │   ├── WordRepository.kt      # JSON word loading
│   │   ├── QuestionGenerator.kt   # Quiz logic with adaptive weighting
│   │   ├── WeakWordsManager.kt    # Persistent difficulty tracking (0-3)
│   │   ├── ActivityTracker.kt     # Daily stats tracking
│   │   ├── SettingsManager.kt     # App preferences
│   │   └── HebrewUtils.kt         # Niqqud stripping for sorting
│   ├── game/                      # Game logic
│   │   ├── GameViewModel.kt       # Timer, answers, pause/resume
│   │   └── GameState.kt           # Sealed state: Loading/Playing/Finished
│   └── ui/                        # Compose screens
│       ├── HomeScreen.kt          # Game setup + navigation drawer
│       ├── GameScreen.kt          # Quiz gameplay with HUD
│       ├── SummaryScreen.kt       # Round results
│       ├── PracticeScreen.kt      # Weak words practice
│       ├── DictionaryScreen.kt    # Full vocabulary browser
│       ├── ActivitySummaryScreen.kt # 30-day stats
│       ├── SettingsScreen.kt      # Sound, smart practice, reset
│       ├── AboutScreen.kt         # App info
│       ├── SoundManager.kt        # ToneGenerator-based audio
│       └── theme/                 # Color, Theme, Typography
└── res/                           # Icons, strings, themes
```

## License

Private project.
