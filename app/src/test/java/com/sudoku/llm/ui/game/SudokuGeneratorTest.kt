package com.sudoku.llm.ui.game

import org.junit.Test
import org.junit.Assert.*

class SudokuGeneratorTest {

    @Test
    fun givenCount_matchesDifficulty() {
        for (diff in Difficulty.entries) {
            val puzzle = SudokuGenerator.generate(diff)
            val givenCount = puzzle.sumOf { row -> row.count { it != 0 } }
            assertEquals("Given count mismatch for ${diff.label}", diff.givenCount, givenCount)
        }
    }

    @Test
    fun solution_isComplete() {
        for (diff in Difficulty.entries) {
            val puzzle = SudokuGenerator.generate(diff)
            val solution = SudokuGenerator.getSolution(puzzle)
            for (r in 0..8) {
                for (c in 0..8) {
                    assertTrue("Solution cell [$r][$c] out of range", solution[r][c] in 1..9)
                }
            }
        }
    }

    @Test
    fun givenCells_matchSolution() {
        for (diff in Difficulty.entries) {
            val puzzle = SudokuGenerator.generate(diff)
            val solution = SudokuGenerator.getSolution(puzzle)
            for (r in 0..8) {
                for (c in 0..8) {
                    if (puzzle[r][c] != 0) {
                        assertEquals("Given cell mismatch at [$r][$c]", puzzle[r][c], solution[r][c])
                    }
                }
            }
        }
    }

    @Test
    fun solution_hasNoDuplicateInRows() {
        for (diff in Difficulty.entries) {
            val solution = SudokuGenerator.getSolution(SudokuGenerator.generate(diff))
            for (r in 0..8) {
                assertEquals("Duplicate in row $r", 9, solution[r].toSet().size)
            }
        }
    }

    @Test
    fun solution_hasNoDuplicateInCols() {
        for (diff in Difficulty.entries) {
            val solution = SudokuGenerator.getSolution(SudokuGenerator.generate(diff))
            for (c in 0..8) {
                val colNums = (0..8).map { solution[it][c] }
                assertEquals("Duplicate in col $c", 9, colNums.toSet().size)
            }
        }
    }

    @Test
    fun solution_hasNoDuplicateInBoxes() {
        for (diff in Difficulty.entries) {
            val solution = SudokuGenerator.getSolution(SudokuGenerator.generate(diff))
            for (boxR in 0..2) {
                for (boxC in 0..2) {
                    val boxNums = mutableListOf<Int>()
                    for (dr in 0..2) for (dc in 0..2) {
                        boxNums.add(solution[boxR * 3 + dr][boxC * 3 + dc])
                    }
                    assertEquals("Duplicate in box ($boxR,$boxC)", 9, boxNums.toSet().size)
                }
            }
        }
    }

    @Test
    fun generate_producesDifferentPuzzles() {
        val p1 = SudokuGenerator.generate(Difficulty.EASY)
        val p2 = SudokuGenerator.generate(Difficulty.EASY)
        assertNotEquals(p1.contentDeepToString(), p2.contentDeepToString())
    }
}

private fun Array<IntArray>.contentDeepToString(): String = joinToString { it.joinToString() }