# 변경 이력

## v1.1.3 (2026-09-18)
- 타이머: `key(state.isPaused, state.isWon)` 수정 — 일시정지/재개 정상 동작
- grid border: `Canvas`로 grid lines 분리 — cell borders 중첩 문제 완전 해결
- 숫자 입력 피드백 개선 — given cell은 bold/dark, 입력 cell은 accent color, 충돌 cell은 white-on-red
- JUnit 테스트 6개 추가 (givenCount, solution 완성성, row/col/box 중복 검증, 무작위 생성)

## v1.1.2 (2026-09-18)
- `systemBarsPadding()` 추가 — 화면 최상단 짤림 방지
- 선택 셀 highlight 개선
- `MenuScreen` modifier 파라미터 추가

## v1.1.0 / v1.0.0
- 최초 릴리즈