package ssfa.sudoku.llm.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sudoku.llm.ui.game.Difficulty

@Composable
fun MenuScreen(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onDifficultySelected: (Difficulty) -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "SUDOKU",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("라이트", color = MaterialTheme.colorScheme.onBackground)
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = { onThemeToggle() }
                )
                Text("다크", color = MaterialTheme.colorScheme.onBackground)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "난이도 선택",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Difficulty.entries.forEach { diff ->
                DifficultyButton(
                    label = diff.label,
                    isDarkTheme = isDarkTheme,
                    onClick = { onDifficultySelected(diff) }
                )
            }
        }
    }
}

@Composable
private fun DifficultyButton(
    label: String,
    isDarkTheme: Boolean,
    onClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(surfaceColor)
            .clickable(onClick = onClick)
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = primaryColor
        )
    }
}