# Smart Word Game — Product Specification

## 1. Product Overview

- **Product name:** Smart Word Game
- **Subtitle:** Hebrew Word Quiz for Kids (Age 7)
- **Goal:** A colorful, kid-friendly mobile-first word game in Hebrew that helps children practice vocabulary using timed multiple-choice questions. The app tracks words the child struggles with, and uses that data to adapt future rounds.
- **Target audience:**
  - Primary: Children age 7
  - Secondary: Parents who might help set up the game
- **Platform:** Android (Kotlin + Jetpack Compose)
- **Storage:** Local only (no backend required), supports offline usage with bundled JSON

---

## 2. Core User Experience

### Main Loop

1. Child selects round length (number of questions)
2. Child selects difficulty (timer per question)
3. Child plays a round of multiple choice questions
4. Child sees round summary, including mistakes
5. Child can start another round
6. Child can open "Words to Practice" screen (weak words list)

### Key Experience Principles

- Bright, fun, accessible UI
- Large text
- Simple visuals
- Fast flow, minimal reading for controls
- All UI elements in Hebrew, RTL layout everywhere

---

## 3. Localization and RTL Requirements

### Language

- Application UI must be Hebrew only
- All labels, captions, buttons, menus, hints, and messages in Hebrew
- Example buttons: "התחל", "הבא", "חזור", "סיכום", "מילים לתרגול", "הגדרות"

### RTL Layout

- Entire app uses RTL direction
- Text alignment defaults to right
- Navigation and progress indicators should feel natural for Hebrew reading direction
- On selection, highlight and feedback should not assume LTR flows

### Hebrew with ניקוד

- The game must render Hebrew with vowel marks reliably
- Use a font stack that supports Hebrew diacritics well
- Ensure line-height, letter spacing, and font rendering do not cause diacritics to collide or clip

---

## 4. Content Model

- Vocabulary dataset: `app/src/main/assets/words.json`
- Format: `[{"id": 1, "word": "אֲחִיזָה", "explanation": "הַחֲזָקָה, תְפִיסָה"}, ...]`
- 244 Hebrew words with niqqud (vowel marks)

---

## 5. Game Modes and Question Types

The game supports two question directions:

### Question Type A

- **Prompt:** Hebrew word (with ניקוד)
- **Answers:** 4 meanings (1 correct, 3 distractors)

### Question Type B

- **Prompt:** Meaning
- **Answers:** 4 Hebrew words (with ניקוד)

### Selection Strategy

- Default: 50% type A, 50% type B
- Ensure no repeated word within the same round unless dataset is too small

---

## 6. Round Setup (Home Screen)

### Controls

- **Round length slider:** Values: 10, 20, 30 (as presets, snaps to values)
- **Difficulty selector:**
  - Easy: 60 seconds per question
  - Medium: 30 seconds per question
  - Hard: 15 seconds per question
- **Start button**

### Hebrew UI Copy

- Title: "משחק מילים"
- Slider label: "כמה שאלות בסיבוב?"
- Difficulty label: "רמת קושי"
- Difficulty options: "קל (דקה לשאלה)", "בינוני (30 שניות לשאלה)", "קשה (15 שניות לשאלה)"
- Start: "התחל"

---

## 7. Question Generation Logic

### Requirements

- For each question, select one vocabulary item as the correct item
- Generate 3 distractors randomly from other items
- Shuffle answer options each time

### Constraints

- Distractors must be unique and not the correct item
- Avoid "too similar" distractors (optional enhancement)
- Use a good pseudo-random shuffle
- Avoid repeating the same correct word within a round
- For small datasets, allow reuse only if necessary (log warning, not shown to user)

---

## 8. Timer and Difficulty

### Timer Behavior

- Each question displays a countdown
- When timer reaches 0:
  - Mark question as incorrect
  - Play failure sound
  - Show failure animation
  - Highlight the correct answer
  - Enable "Next" button

### Difficulty Mapping

- Easy: 60s
- Medium: 30s
- Hard: 15s

### Pause Behavior

- If the app goes to background, pause timer
- On return, show a "Continue" overlay
- Reduces frustration and feels fair for kids

---

## 9. Answer Selection, Feedback, and Flow

### Answer Interaction

- Child taps one option
- Options become disabled immediately to prevent double taps

### Correct Answer

- Play positive sound
- Show positive animation (confetti, star burst, smiley)
- Update counters
- Auto advance after ~2-3 seconds

### Incorrect Answer

- Play negative sound (gentle, not scary)
- Show failure animation (sad face, wobble)
- Highlight: selected wrong option in red, correct option in green
- Show "Next" button (no auto advance)

### Accessibility

- Option in settings to turn sound off
- Color feedback is not the only feedback: use icons, outline, and text like "נכון" or "לא נכון"

---

## 10. Persistent Progress Display (HUD)

Always-visible HUD during the round:

- Correct answers: "נכון: 3 מתוך 7"
- Question index: "שאלה 5 מתוך 20"
- Timer: large, high contrast

HUD is sticky at the top, RTL aligned.

---

## 11. End of Round Summary Screen

### Must Display

- Total correct: "ענית נכון על 12 מתוך 20"
- Total time: "זמן כולל: 03:42"
- Button: "סיבוב נוסף"
- Button: "מילים לתרגול"
- Mistakes list: all words answered incorrectly, with meaning (Hebrew word with ניקוד + meaning beneath)

### Additional Details

- Accuracy percent: "דיוק: 60%"
- Optional breakdown by question type

---

## 12. Weak Words Tracking (Global, Persistent)

### Data Model

- For each word id, store a score (0 to 3)
- Wrong answer: score +1
- Correct answer: score -1
- Clamp score between 0 and 3

### When to Update

- On incorrect selection: increment
- On timeout: increment
- On correct selection: decrement

### Persistence

- Stored locally on device (SharedPreferences)
- Score cannot go below 0 or exceed 3
- Words with score 0 are not shown in "Words to Practice"

---

## 13. Words to Practice Screen

### Purpose

A dedicated screen for parent or child to see which words need more practice.

### Must Display

- Title: "מילים לתרגול"
- List of weak words (score 1 to 3)
- Each list item shows: word (with ניקוד), meaning, difficulty indicator (1 to 3 stars)

### Sorting

- Default: highest score first (3, then 2, then 1)
- Secondary: alphabetical by Hebrew word

### Actions

- "תרגל עכשיו" button: starts a round biased toward weak words
- "איפוס" button: reset all scores to 0 (with confirm dialog: "בטוח שברצונך לאפס?")

---

## 14. Adaptive Learning

### Weighted Selection Rule

- Score 3: weight 6
- Score 2: weight 3
- Score 1: weight 2
- Score 0: weight 1

Questions chosen according to weights, preventing duplicates within the same round.

### Result

- Child sees weak words more often
- The game naturally adapts without explicit "hard mode content"

### Toggle

- Setting: "מצב תרגול חכם"
- Default: ON

---

## 15. UI and Visual Design Guidelines

### Color Palette

- **Primary:** Sky Blue `#4FC3F7` — buttons, highlights, main actions
- **Primary Dark:** Strong Blue `#0288D1` — app bar, selected states
- **Secondary:** Orange `#FF9800` — CTAs, progress, emphasis
- **Background:** Soft Cream `#FFF8E1` — main background
- **Surface:** White `#FFFFFF` — cards, panels
- **Text Primary:** Dark Gray `#263238` — main readable text
- **Text Secondary:** Medium Gray `#546E7A` — less important text

### Feedback Colors

- **Success:** Green `#66BB6A` — friendly, not harsh
- **Error:** Soft Red `#EF5350` — not scary
- **Warning:** Yellow `#FFCA28` — timer low
- **Info:** Light Blue `#29B6F6` — neutral hints

### Fun Layer Accents (used sparingly in animations, icons, cards)

- Purple: `#BA68C8`
- Teal: `#4DB6AC`
- Pink: `#F06292`

### Style

- Bright palette with high contrast
- Friendly rounded buttons (16-24px radius)
- Large typography
- Simple icons and emoji-based indicators
- Avoid very pale colors (hard to see), too many saturated colors at once, and red/green only signaling

### Components

- Big tappable answers (minimum 48px height)
- Clear selected state
- Avoid clutter and long paragraphs
- Material 3 components: Cards, FilterChips, SegmentedButtons, NavigationDrawer

### Animations

- Quick, positive reinforcement animations
- Keep them short and non-blocking
- Do not cause motion sickness, avoid aggressive shaking
- Timer pulse animation (scale 1.0→1.15) when ≤3 seconds remain

### Sounds

- **Correct:** Short reward melody — 3 randomized variants (ascending tone sequences), ~0.8–1.5 seconds
- **Incorrect:** Gentle "oops" — 2 descending tones, soft and not alarming
- **Timer warning:** Optional soft tick in last 3 seconds
- Volume toggle, default ON

---

## 16. Navigation Structure

### Screens

1. Home / Round setup
2. Game screen (question, timer, answers, HUD)
3. Summary screen (results, mistakes, next actions)
4. Words to Practice screen
5. Full Dictionary screen
6. 30-Day Activity Summary screen
7. Settings screen
8. About screen

### Navigation (Material Drawer)

Side drawer accessible via hamburger menu (☰) in the TopAppBar:

- 🎮 משחק (Home / Game)
- 💪 מילים קשות (Words to Practice)
- 📚 מילון מילים (Full Dictionary)
- 📊 סיכום 30 ימים (30-Day Summary)
- ⚙️ הגדרות (Settings)
- ℹ️ אודות (About)

### Navigation Behavior

- Game opens by default
- Menu is easy to access but not intrusive
- Clear Hebrew labels and recognizable emoji icons
- Avoid deep navigation hierarchies

### Settings

- 🔊 Sound: On/Off
- 🧠 Smart practice: On/Off
- Reset progress — code-protected (requires entering "0000" to prevent accidental child resets)

---

## 17. Activity Summary (Last 30 Days)

### Purpose

Provide a simple way to track consistency and improvement over time.

### Requirements

- Display a daily breakdown for the last 30 days
- For each day show: number of questions answered, number of correct answers
- **Only display days with activity** — skip empty days entirely

### Visualization

- Colorful bar chart with accuracy-based coloring:
  - Accuracy ≥ 80%: Green bar `#66BB6A` ✅
  - Accuracy 50-79%: Orange bar `#FF9800` ⚠️
  - Accuracy < 50%: Red bar `#EF5350` ❌
- Child-friendly, not analytical or complex
- Mascot 🐻 shown when no activity exists

### UI

- Title: "סיכום 30 הימים האחרונים"
- Header card with totals: questions, correct answers, overall accuracy %
- RTL layout, large clear numbers, minimal text

---

## 18. Full Dictionary Browser

### Purpose

Expose all words in a simple, exploration-focused view, without quiz pressure.

### Requirements

- Display all words from the JSON dataset
- Each item shows: the word (with ניקוד), the meaning
- Scrollable and performant (LazyColumn)

### Highlighting Weak Words

Words with score > 0 visually distinguished using tiered indicators:
- **Score 3:** 🔥 fire icon + red-tinted card background `#FFEBEE`
- **Score 2:** ⭐⭐ stars + orange-tinted card `#FFF3E0`
- **Score 1:** ⭐ star + light yellow card `#FFFDE7`
- **Score 0:** Normal white surface

### Sorting Options

- Default: alphabetical by Hebrew word (**ignoring niqqud** for proper ordering)
- Toggle: sort by difficulty (highest score first)
- Uses segmented control: `[ א-ב ]` `[ לפי קושי ]`

### UI

- Title: "מילון מילים"
- Clean, readable list, large typography
- Colorful and friendly

---

## 19. Focused Weak Words List

### Purpose

Enable focused practice and quick review of difficult vocabulary.

### Requirements

- Include only words with score > 0
- Display: word (with ניקוד), meaning, difficulty indicator
- Tiered difficulty cards:
  - **Score 3:** 🔥 prefix + red-tinted card `#FFEBEE`
  - **Score 2:** Orange-tinted card `#FFF3E0` + ⭐⭐
  - **Score 1:** Light yellow card `#FFFDE7` + ⭐

### Sorting

- Primary: highest difficulty score first
- Secondary: alphabetical order (**ignoring niqqud**)

### Actions

- "תרגל מילים קשות" button: starts a round biased toward weak words
- Reset: code-protected (requires entering "0000"), shows "קוד שגוי" in red on wrong code
- Empty state: mascot message "🐻 כל הכבוד! אין מילים לתרגול"

### UI

- Title: "מילים לתרגול"
- Consistent with rest of app

---

## 20. Mascot Integration

### Purpose

Add emotional connection and motivation through a friendly animal mascot.

### Mascot: 🐻 Bear

### Use Cases

- **Correct answer:** Mascot celebrates — randomly shows "🐻 כל הכבוד!", "🐻 מעולה!", or "🐻 נהדר!"
- **Wrong answer:** Mascot reacts gently — "🐻 בפעם הבאה!"
- **Timeout:** "🐻 אופס, נגמר הזמן"
- **Summary screen:** Mascot gives feedback based on accuracy
- **Empty states:** Mascot shown with encouraging messages

### Design Rules

- Keep it simple and consistent
- Do not over-animate
- Avoid distraction during questions (mascot appears only in feedback)

---

## 21. Game Screen UX Improvements

### Back Confirmation Dialog

When user taps back during an active game (unanswered question):
- Show dialog: "לצאת מהמשחק?"
- Text: "ההתקדמות בסיבוב הנוכחי תאבד"
- Confirm: "כן, לצאת" → exits game
- Dismiss: "להישאר" → stays in game

### Timer Warning

- When ≤3 seconds remain: timer text turns red with pulsing scale animation (1.0→1.15)
- Optional tick sound in last 3 seconds (can be disabled)

### Answer Feedback Colors

- Correct: SuccessGreen `#66BB6A` with 0.2 alpha container
- Incorrect: ErrorRed `#EF5350` with 0.2 alpha container

---

## 22. About Screen

- Title: "אודות"
- Mascot: 🐻
- App name: "משחק מילים חכם"
- Version: "גרסה 1.0"
- Description: "משחק לימוד מילים בעברית לילדים"
- Footer: "נבנה באהבה 💙"

---

## 23. Hebrew Sorting (Niqqud Handling)

All alphabetical sorting throughout the app strips niqqud (vowel marks) before comparing, using the `stripNiqqud()` utility function. This ensures proper Hebrew alphabetical order regardless of diacritical marks.

Unicode range stripped: `U+0591` to `U+05C7`

---

## 24. Edge Cases and Rules

### Dataset Too Small

- If fewer than 4 words exist: show error "צריך לפחות 4 מילים כדי לשחק"
- If fewer unique distractors available: allow reuse as last resort, keep options unique within a question

### Repeat Avoidance

- Do not repeat the same correct word within a round
- If round length exceeds available words: cap automatically and display "יש X מילים זמינות, נעדכן את הסיבוב ל-X שאלות"

### Timer Accuracy

- Keep timer stable across mobile devices
- Pause on background

### Performance

- Must run smoothly on typical phones
- Precompute a round's questions at start to avoid jank mid-game

---

## 25. Consistency Requirements

All features must follow:

- Full Hebrew interface, all text localized
- RTL layout throughout
- Colorful, engaging, and child-friendly design
- Large and readable text
- Clear visual feedback
- Smooth performance on mobile devices
- Responsive design for phone and tablet

---

## 26. Acceptance Criteria (Definition of Done)

### Functional

- App loads JSON successfully and validates content
- Home screen allows selecting: questions per round (10, 20, 30) and difficulty (easy, medium, hard)
- Navigation drawer with all menu items accessible via hamburger menu
- Game runs with: 4 options per question, correct/incorrect feedback, timer behavior with timeout handling
- Mascot feedback on correct/incorrect/timeout answers
- Back confirmation dialog during active game
- Timer pulse animation at ≤3 seconds
- Persistent HUD showing progress
- Summary screen shows: correct count, total questions, total time, list of incorrectly answered words with meanings
- Words to Practice screen: shows global weak words with tiered indicators (🔥/⭐), code-protected reset
- Persistence across app restarts
- Activity tracking records daily stats
- 30-day summary displays active days only with accuracy-colored bars
- Full dictionary browser shows all words with tiered weak word highlighting
- Alphabetical sorting ignores niqqud throughout the app
- About screen shows app info with mascot
- Sound system with 3 correct variants and gentle incorrect sound

### UX

- Everything is in Hebrew
- RTL layout everywhere
- Material 3 Navigation Drawer with emoji labels
- New color palette: Sky Blue primary, Cream background, proper feedback colors
- Large text and kid-friendly UI
- Sound toggle works
- Text wraps properly for long explanations
- Code-protected reset (requires "0000") in Settings and Practice screens
- The experience feels like a game first, not a learning tool
