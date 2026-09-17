package com.sudoku.llm.ui.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.sudoku.llm.ui.game.Difficulty
import com.sudoku.llm.ui.game.SudokuGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class GameState(
    val puzzle: Array<IntArray> = Array(9) { IntArray(9) },
    val solution: Array<IntArray> = Array(9) { IntArray(9) },
    val userGrid: Array<IntArray> = Array(9) { IntArray(9) },
    val given: Array<BooleanArray> = Array(9) { BooleanArray(9) },
    val selectedRow: Int = -1,
    val selectedCol: Int = -1,
    val elapsedSeconds: Int = 0,
    val isPaused: Boolean = false,
    val isWon: Boolean = false,
    val difficulty: Difficulty = Difficulty.EASY
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as GameState
        return puzzle.contentDeepEquals(other.puzzle) &&
                solution.contentDeepEquals(other.solution) &&
                userGrid.contentDeepEquals(other.userGrid)
    }

    override fun hashCode(): Int {
        var result = puzzle.contentDeepHashCode()
        result = 31 * result + solution.contentDeepHashCode()
        result = 31 * result + userGrid.contentDeepHashCode()
        return result
    }
}

class GameViewModel : ViewModel() {
    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    fun startGame(difficulty: Difficulty) {
        val puzzle = SudokuGenerator.generate(difficulty)
        val solution = SudokuGenerator.getSolution(puzzle)
        val given = Array(9) { BooleanArray(9) }
        for (r in 0..8) for (c in 0..8) given[r][c] = puzzle[r][c] != 0

        _state.value = GameState(
            puzzle = puzzle,
            solution = solution,
            userGrid = puzzle.map { it.copyOf() }.toTypedArray(),
            given = given,
            selectedRow = -1,
            selectedCol = -1,
            elapsedSeconds = 0,
            isPaused = false,
            isWon = false,
            difficulty = difficulty
        )
    }

    fun selectCell(row: Int, col: Int) {
        if (_state.value.isWon || _state.value.isPaused) return
        _state.value = _state.value.copy(selectedRow = row, selectedCol = col)
    }

    fun inputNumber(num: Int) {
        val s = _state.value
        if (s.isWon || s.isPaused || s.selectedRow < 0) return
        val { selectedRow, selectedCol } = s
        if (s.given[selectedRow][selectedCol]) return

        val newGrid = s.userGrid.map { it.copyOf() }.toTypedArray()
        newGrid[selectedRow][selectedCol] = num

        val won = checkWin(newGrid, s.solution)
        _state.value = s.copy(userGrid = newGrid, isWon = won)
    }

    fun clearCell() = inputNumber(0)

    fun tick() {
        val s = _state.value
        if (!s.isPaused && !s.isWon) {
            _state.value = s.copy(elapsedSeconds = s.elapsedSeconds + 1)
        }
    }

    fun togglePause() {
        _state.value = _state.value.copy(isPaused = !_state.value.isPaused)
    }

    fun resetGame() = startGame(_state.value.difficulty)

    private fun checkWin(user: Array<IntArray>, solution: Array<IntArray>): Boolean {
        for (r in 0..8) for (c in 0..8) if (user[r][c] != solution[r][c]) return false
        return true
    }

    fun getConflict(row: Int, col: Int, num: Int): Boolean {
        val s = _state.value
        if (num == 0) return false
        for (c in 0..8) if (c != col && s.userGrid[row][c] == num) return true
        for (r in 0..8) if (r != row && s.userGrid[r][col] == num) return true
        val br = (row / 3) * 3
        val bc = (col / 3) * 3
        for (r in br until br + 3) for (c in bc until bc + 3) {
            if ((r != row || c != col) && s.userGrid[r][c] == num) return true
        }
        return false
    }
}