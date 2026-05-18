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

### Style

- Bright palette with high contrast
- Friendly rounded buttons
- Large typography
- Simple icons

### Components

- Big tappable answers (minimum 48px height)
- Clear selected state
- Avoid clutter and long paragraphs

### Animations

- Quick, positive reinforcement animations
- Keep them short and non-blocking
- Do not cause motion sickness

### Sounds

- Positive sound for correct
- Gentle negative sound for incorrect/timeout
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
7. Settings screen (minimal)

### Navigation Menu (from Home)

- מילים לתרגול (Words to Practice)
- מילון מילים (Full Dictionary)
- סיכום 30 ימים (30-Day Summary)
- הגדרות (Settings)

### Settings (MVP)

- Sound: On/Off
- Smart practice: On/Off
- Reset progress (weak word scores)

---

## 17. Activity Summary (Last 30 Days)

### Purpose

Provide a simple way to track consistency and improvement over time.

### Requirements

- Display a daily breakdown for the last 30 days
- For each day show: number of questions answered, number of correct answers
- If no activity on a given day, display zero values clearly

### Visualization

- Simple and colorful bar chart or list view
- Each day represented visually so trends are easy to understand
- Child-friendly, not analytical or complex

### UI

- Title: "סיכום 30 ימים אחרונים"
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

- Words with score > 0 visually distinguished
- Different color, icon (star/warning), or background highlight

### Sorting Options

- Default: alphabetical by Hebrew word
- Optional toggle: sort by difficulty (highest score first)

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
- Display: word (with ניקוד), meaning, difficulty indicator (1-3 stars)

### Sorting

- Primary: highest difficulty score first
- Secondary: alphabetical order

### Actions

- "תרגל מילים קשות" button: starts a round biased toward weak words
- Optional reset capability

### UI

- Title: "מילים קשות" / "מילים לתרגול"
- Consistent with rest of app

---

## 20. Edge Cases and Rules

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

## 21. Consistency Requirements

All features must follow:

- Full Hebrew interface, all text localized
- RTL layout throughout
- Colorful, engaging, and child-friendly design
- Large and readable text
- Clear visual feedback
- Smooth performance on mobile devices
- Responsive design for phone and tablet

---

## 22. Acceptance Criteria (Definition of Done)

### Functional

- App loads JSON successfully and validates content
- Home screen allows selecting: questions per round (10, 20, 30) and difficulty (easy, medium, hard)
- Game runs with: 4 options per question, correct/incorrect feedback, timer behavior with timeout handling
- Persistent HUD showing progress
- Summary screen shows: correct count, total questions, total time, list of incorrectly answered words with meanings
- Words to Practice screen: shows global weak words with meanings, sorting by score, scores update with clamp 0-3
- Persistence across app restarts
- Activity tracking records daily stats
- 30-day summary displays daily breakdown
- Full dictionary browser shows all words with weak word highlighting

### UX

- Everything is in Hebrew
- RTL layout everywhere
- Large text and kid-friendly UI
- Sound toggle works
- Text wraps properly for long explanations
