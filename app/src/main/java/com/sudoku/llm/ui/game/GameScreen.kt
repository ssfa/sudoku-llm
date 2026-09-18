package com.sudoku.llm.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sudoku.llm.ui.theme.*

@Composable
fun GameScreen(
    state: GameState,
    isDarkTheme: Boolean,
    onCellSelect: (Int, Int) -> Unit,
    onNumberInput: (Int) -> Unit,
    onClear: () -> Unit,
    onTogglePause: () -> Unit,
    onReset: () -> Unit,
    onBack: () -> Unit,
    onTick: () -> Unit,
    getConflict: (Int, Int, Int) -> Boolean,
    modifier: Modifier = Modifier
) {
    val gridLineColor = if (isDarkTheme) DarkGridLine else LightGridLine
    val gridBoxColor = if (isDarkTheme) DarkGridBox else LightGridBox
    val selectedColor = if (isDarkTheme) DarkSelected else LightSelected
    val conflictColor = if (isDarkTheme) DarkConflict else LightConflict
    val givenColor = if (isDarkTheme) DarkGiven else LightGiven
    val filledColor = if (isDarkTheme) DarkFilled else LightFilled
    val primaryColor = if (isDarkTheme) DarkPrimary else LightPrimary
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground
    val surfaceColor = if (isDarkTheme) DarkSurface else LightSurface

    // Timer — restart loop when game starts / unpauses / resets (key must change each start)
    key(state.elapsedSeconds, state.isPaused, state.isWon) {
        LaunchedEffect(Unit) {
            if (!state.isPaused && !state.isWon) {
                while (true) {
                    kotlinx.coroutines.delay(1000)
                    onTick()
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.difficulty.label,
                    fontSize = 14.sp,
                    color = primaryColor
                )
                Text(
                    text = formatTime(state.elapsedSeconds),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconBtn("⏸", onTogglePause)
                    IconBtn("🔄", onReset)
                    IconBtn("◀", onBack)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sudoku grid — Canvas for clean borders, no overlap
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(surfaceColor)
            ) {
                // Cell numbers layer (behind grid lines)
                Column(modifier = Modifier.fillMaxSize()) {
                    for (row in 0..8) {
                        Row(modifier = Modifier.weight(1f)) {
                            for (col in 0..8) {
                                val isSelected = state.selectedRow == row && state.selectedCol == col
                                val num = state.userGrid[row][col]
                                val isGiven = state.given[row][col]
                                val isConflict = num != 0 && getConflict(row, col, num)

                                val cellBg = when {
                                    isConflict -> conflictColor
                                    isSelected -> selectedColor
                                    else -> Color.Transparent
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(cellBg)
                                        .clickable { onCellSelect(row, col) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (num != 0) {
                                        Text(
                                            text = num.toString(),
                                            fontSize = 20.sp,
                                            fontWeight = if (isGiven) FontWeight.Bold else FontWeight.Normal,
                                            color = when {
                                                isConflict -> Color.White
                                                isGiven -> givenColor
                                                else -> filledColor
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Grid lines layer (on top via Canvas)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cellW = size.width / 9
                    val cellH = size.height / 9

                    // Thin inner grid lines
                    for (i in 1..8) {
                        val isThick = i % 3 == 0
                        val color = if (isThick) gridBoxColor else gridLineColor
                        val strokeWidth = if (isThick) 2.dp.toPx() else 0.5.dp.toPx()

                        // Vertical line at column i
                        drawLine(
                            color = color,
                            start = Offset(i * cellW, 0f),
                            end = Offset(i * cellW, size.height),
                            strokeWidth = strokeWidth
                        )
                        // Horizontal line at row i
                        drawLine(
                            color = color,
                            start = Offset(0f, i * cellH),
                            end = Offset(size.width, i * cellH),
                            strokeWidth = strokeWidth
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Number pad
            NumberPad(
                onNumber = onNumberInput,
                onClear = onClear,
                primaryColor = primaryColor,
                surfaceColor = surfaceColor
            )
        }

        // Won overlay
        if (state.isWon) {
            WonDialog(
                time = formatTime(state.elapsedSeconds),
                onRestart = onReset,
                onMenu = onBack,
                isDarkTheme = isDarkTheme
            )
        }

        // Paused overlay
        if (state.isPaused) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("일시정지", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Button(onClick = onTogglePause, colors = ButtonDefaults.buttonColors(containerColor = primaryColor)) {
                        Text("계속하기")
                    }
                }
            }
        }
    }
}

@Composable
private fun NumberPad(
    onNumber: (Int) -> Unit,
    onClear: () -> Unit,
    primaryColor: Color,
    surfaceColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        for (row in 0..2) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (col in 1..3) {
                    val num = row * 3 + col
                    NumButton(num, primaryColor, surfaceColor) { onNumber(num) }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NumButton(0, primaryColor, surfaceColor, label = "✕") { onClear() }
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun NumButton(
    num: Int,
    primaryColor: Color,
    surfaceColor: Color,
    label: String = num.toString(),
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(surfaceColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = primaryColor
        )
    }
}

@Composable
private fun IconBtn(icon: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = icon, fontSize = 18.sp)
    }
}

@Composable
private fun WonDialog(
    time: String,
    onRestart: () -> Unit,
    onMenu: () -> Unit,
    isDarkTheme: Boolean
) {
    val primaryColor = if (isDarkTheme) DarkPrimary else LightPrimary
    val surfaceColor = if (isDarkTheme) DarkSurface else LightSurface

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(surfaceColor)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("🎉", fontSize = 48.sp)
            Text(
                "축하합니다!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )
            Text(
                "시간: $time",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = onRestart,
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Text("다시 하기")
                }
                OutlinedButton(onClick = onMenu) {
                    Text("메뉴")
                }
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%02d:%02d".format(m, s)
}