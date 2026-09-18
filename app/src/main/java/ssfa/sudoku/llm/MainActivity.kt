package ssfa.sudoku.llm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sudoku.llm.ui.game.GameScreen
import com.sudoku.llm.ui.game.GameViewModel
import com.sudoku.llm.ui.menu.MenuScreen
import com.sudoku.llm.ui.theme.SudokuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            var showGame by remember { mutableStateOf(false) }
            val viewModel: GameViewModel = viewModel()

            SudokuTheme(darkTheme = isDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (showGame) {
                        val state by viewModel.state.collectAsState()
                        GameScreen(
                            state = state,
                            isDarkTheme = isDarkTheme,
                            onCellSelect = { r, c -> viewModel.selectCell(r, c) },
                            onNumberInput = { n -> viewModel.inputNumber(n) },
                            onClear = { viewModel.clearCell() },
                            onTogglePause = { viewModel.togglePause() },
                            onReset = { viewModel.resetGame() },
                            onTick = { viewModel.tick() },
                            onBack = { showGame = false },
                            getConflict = { r, c, n -> viewModel.getConflict(r, c, n) },
                            modifier = Modifier.systemBarsPadding()
                        )
                    } else {
                        MenuScreen(
                            isDarkTheme = isDarkTheme,
                            onThemeToggle = { isDarkTheme = !isDarkTheme },
                            onDifficultySelected = { diff ->
                                viewModel.startGame(diff)
                                showGame = true
                            },
                            modifier = Modifier.systemBarsPadding()
                        )
                    }
                }
            }
        }
    }
}