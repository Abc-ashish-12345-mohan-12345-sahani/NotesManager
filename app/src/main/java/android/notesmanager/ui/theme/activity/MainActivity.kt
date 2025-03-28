package android.notesmanager.ui.theme.activity

import android.notesmanager.ui.theme.NotesManagerTheme
import android.notesmanager.ui.theme.screen.CreateNotesScreen
import android.notesmanager.ui.theme.screen.MainScreen
import android.notesmanager.ui.theme.utils.NotesManagerConstants
import android.notesmanager.ui.theme.viewmodels.MainViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesManagerTheme {
                MainActivityScreen(mainViewModel)
            }
        }
    }
}

@Composable
fun MainActivityScreen(viewModel: MainViewModel) {
    var isDarkTheme by remember { mutableStateOf(false) }
    NotesManagerTheme(darkTheme = isDarkTheme) {
        val context = LocalContext.current
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = NotesManagerConstants.MAIN_SCREEN
        ) {
            composable(NotesManagerConstants.MAIN_SCREEN) {
                MainScreen(
                    viewModel,
                    isDarkTheme = isDarkTheme,
                    onToggleDarkMode = { isDarkTheme = !isDarkTheme },
                    onFabClick = {
                        Prefs.remove(NotesManagerConstants.ID)
                        navController.navigate(NotesManagerConstants.CREATE_NOTES_SCREEN)
                    },
                    onEditClick = {
                        navController.navigate(NotesManagerConstants.CREATE_NOTES_SCREEN)
                    })
            }
            composable(NotesManagerConstants.CREATE_NOTES_SCREEN) {
                CreateNotesScreen(
                    context, viewModel, navController
                )
            }
        }
    }
}
