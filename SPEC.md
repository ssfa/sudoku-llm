# Sudoku Puzzle App — SPEC

## 1. Project Overview
- **Type**: Native Android app (Kotlin, Jetpack Compose)
- **Core**: 9×9 Sudoku puzzle — three difficulty levels (상/중/하)
- **Target**: Android (phone, minSdk 26, targetSdk 34)

## 2. UI/UX

### Screens
1. **Main Menu** — difficulty selection (상/중/하) + theme toggle (라이트/다크)
2. **Game Board** — 9×9 grid, number input pad, timer, pause/reset
3. **Result Dialog** — clear notification with elapsed time

### Visual Style
- Clean, minimal. Jetpack Compose Material 3.
- Two themes: Light (white/gray/blue accent) / Dark (dark gray/black/cyan accent)
- Grid: bold lines for 3×3 boxes, thin for cells. Selected cell highlighted.
- Number pad: 1–9 + clear button.

### Navigation
- Single-activity, Compose Navigation.
- Menu → Game → (back to Menu on clear/back)

## 3. Functionality

### Difficulty Levels
| Level | Known Cells | Description |
|-------|-------------|-------------|
| 하 (Easy) | 40–45 | many givens |
| 중 (Medium) | 30–35 | moderate |
| 상 (Hard) | 22–28 | few givens |

### Core Logic
- Generate a valid complete 9×9 Sudoku (backtracking)
- Remove cells per difficulty → puzzle
- Player taps cell → selects it → taps number pad to fill/clear
- Input validation: prevent duplicate in row/col/box (highlight conflict in red)
- Win check: all 81 cells filled correctly
- Timer: starts on puzzle start, pauses on background/pause

### State
- In-memory only (no persistence needed for v1)

## 4. Technical

- **Language**: Kotlin 1.9
- **UI**: Jetpack Compose with Material 3
- **Architecture**: Single ViewModel (MVVM), no DI framework — manual injection
- **Build**: Gradle 8.4, AGP 8.2, Kotlin DSL
- **Runtime**: mise-managed JDK 17, Android SDK via `sdkmanager`
- **Min SDK**: 26 | **Target SDK**: 34

## 5. File Structure (minimum)

```
app/
  src/main/
    java/com/sudoku/llm/
      MainActivity.kt        # Single activity
      ui/
        theme/
          Color.kt
          Theme.kt
        menu/
          MenuScreen.kt
        game/
          GameScreen.kt
          GameViewModel.kt
          SudokuGenerator.kt
          SudokuGenerator.kt
      SudokuApp.kt           # Application class
    res/
      values/
        strings.xml
        colors.xml
build.gradle.kts
settings.gradle.kts
gradle.properties
```