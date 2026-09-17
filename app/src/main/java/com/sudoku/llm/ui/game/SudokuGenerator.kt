package com.sudoku.llm.ui.game

import kotlin.random.Random

enum class Difficulty(val label: String, val givenCount: Int) {
    EASY("하 (Easy)", 42),
    MEDIUM("중 (Medium)", 33),
    HARD("상 (Hard)", 25);

    companion object {
        fun fromOrdinal(o: Int) = entries[o]
    }
}

object SudokuGenerator {
    private const val SIZE = 9

    fun generate(difficulty: Difficulty): Array<IntArray> {
        val solution = Array(SIZE) { IntArray(SIZE) }
        fillGrid(solution)
        val puzzle = solution.map { it.copyOf() }.toTypedArray()
        removeCells(puzzle, difficulty.givenCount)
        return puzzle
    }

    fun getSolution(puzzle: Array<IntArray>): Array<IntArray> {
        val solution = puzzle.map { it.copyOf() }.toTypedArray()
        fillGrid(solution)
        return solution
    }

    private fun fillGrid(grid: Array<IntArray>): Boolean {
        for (r in 0 until SIZE) {
            for (c in 0 until SIZE) {
                if (grid[r][c] == 0) {
                    val nums = (1..9).shuffled()
                    for (n in nums) {
                        if (isValid(grid, r, c, n)) {
                            grid[r][c] = n
                            if (fillGrid(grid)) return true
                            grid[r][c] = 0
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    private fun isValid(grid: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
        for (c in 0 until SIZE) if (grid[row][c] == num) return false
        for (r in 0 until SIZE) if (grid[r][col] == num) return false
        val br = (row / 3) * 3
        val bc = (col / 3) * 3
        for (r in br until br + 3) for (c in bc until bc + 3) if (grid[r][c] == num) return false
        return true
    }

    private fun removeCells(grid: Array<IntArray>, keepCount: Int) {
        val cells = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until SIZE) for (c in 0 until SIZE) cells.add(r to c)
        cells.shuffle()
        val removeCount = SIZE * SIZE - keepCount
        cells.take(removeCount).forEach { (r, c) -> grid[r][c] = 0 }
    }
}