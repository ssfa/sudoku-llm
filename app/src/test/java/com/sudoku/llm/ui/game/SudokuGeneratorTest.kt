package com.sudoku.llm.ui.game

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

fun main() {
    println("Running SudokuGenerator tests...")

    // Test: generated puzzle has correct given count per difficulty
    for (diff in Difficulty.entries) {
        val puzzle = SudokuGenerator.generate(diff)
        val givenCount = puzzle.sumOf { row -> row.count { it != 0 } }
        println("  ${diff.label}: ${givenCount} given cells (expected ${diff.givenCount})")
        assertEquals(diff.givenCount, givenCount, "Given count mismatch for ${diff.label}")
    }

    // Test: solution is complete (no zeros)
    for (diff in Difficulty.entries) {
        val puzzle = SudokuGenerator.generate(diff)
        val solution = SudokuGenerator.getSolution(puzzle)
        for (r in 0..8) {
            for (c in 0..8) {
                assertTrue(solution[r][c] in 1..9, "Solution cell [$r][$c] is ${solution[r][c]}, expected 1-9")
            }
        }
    }

    // Test: puzzle + solution match on given cells
    for (diff in Difficulty.entries) {
        val puzzle = SudokuGenerator.generate(diff)
        val solution = SudokuGenerator.getSolution(puzzle)
        for (r in 0..8) {
            for (c in 0..8) {
                if (puzzle[r][c] != 0) {
                    assertEquals(puzzle[r][c], solution[r][c], "Given cell mismatch at [$r][$c]")
                }
            }
        }
    }

    // Test: solution has valid Sudoku rules (no duplicates in rows/cols/boxes)
    for (diff in Difficulty.entries) {
        val puzzle = SudokuGenerator.generate(diff)
        val solution = SudokuGenerator.getSolution(puzzle)
        for (r in 0..8) {
            val rowNums = solution[r].toList()
            assertEquals(9, rowNums.toSet().size, "Duplicate in row $r")
        }
        for (c in 0..8) {
            val colNums = (0..8).map { solution[it][c] }
            assertEquals(9, colNums.toSet().size, "Duplicate in col $c")
        }
        for (boxR in 0..2) {
            for (boxC in 0..2) {
                val boxNums = mutableListOf<Int>()
                for (dr in 0..2) for (dc in 0..2) {
                    boxNums.add(solution[boxR * 3 + dr][boxC * 3 + dc])
                }
                assertEquals(9, boxNums.toSet().size, "Duplicate in box ($boxR,$boxC)")
            }
        }
    }

    // Test: different calls produce different puzzles
    val p1 = SudokuGenerator.generate(Difficulty.EASY)
    val p2 = SudokuGenerator.generate(Difficulty.EASY)
    assertNotEquals(p1.contentDeepToString(), p2.contentDeepToString(), "Two generated puzzles should differ")

    println("  All SudokuGenerator tests passed!")
}

private fun Array<IntArray>.contentDeepToString(): String = joinToString { it.joinToString() }