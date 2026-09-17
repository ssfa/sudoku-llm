# Sudoku

안드로이드 스도쿠 퍼즐 앱 — Jetpack Compose + Material 3

## 기능

- **난이도 3단계**: 하 (Easy 42개 Given), 중 (Medium 33개 Given), 상 (Hard 25개 Given)
- **테마**: 라이트 / 다크 토글
- **타이머**: 게임 진행 시간 표시, 일시정지 가능
- **입력 검증**: 중복 시 충돌 셀 빨간색 표시
- **클리어判定**: 81칸 모두 정답 시 축하 다이얼로그

## 빌드

```bash
./gradlew assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

## 환경

- **JDK**: 17 (mise managed — `mise.toml` 참조)
- **Android SDK**: compileSdk 34, minSdk 26, targetSdk 34
- **Gradle**: 8.4

## 구조

```
app/src/main/java/com/sudoku/llm/
  MainActivity.kt          # Navigation, theme state
  ui/
    theme/                 # Color.kt, Theme.kt
    menu/MenuScreen.kt     # 난이도 선택 화면
    game/
      GameScreen.kt        # 게임 보드 UI
      GameViewModel.kt     # 상태 관리
      SudokuGenerator.kt   # 퍼즐 생성 (backtracking)
```

## 버전

- **v1.0.0** — 최초リリース