package android.notesmanager.ui.theme.screen

import android.content.Context
import android.notesmanager.R
import android.notesmanager.ui.theme.dataclass.GetAllNotesItem
import android.notesmanager.ui.theme.dataclass.PostNewNotes
import android.notesmanager.ui.theme.ui.theme.Pink80
import android.notesmanager.ui.theme.utils.NotesManagerConstants
import android.notesmanager.ui.theme.viewmodels.MainViewModel
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.pixplicity.easyprefs.library.Prefs
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CreateNotesScreen(
    context: Context, viewModel: MainViewModel, navController: NavHostController
) {
    val updateResult = viewModel.noteUpdateState.collectAsState().value
    val saveNotes = viewModel.noteCreationState.collectAsState().value
    val id = Prefs.getInt(NotesManagerConstants.ID)
    val isLoading by viewModel.loading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackBarHostState.showSnackbar(
                if (message.contains("404")) context.getString(
                    R.string.server_unavailable
                ) else context.getString(R.string.some_error_occurred)
            )
            viewModel.clearErrorMessage()
        }
    }

    LaunchedEffect(id) {
        if (id != -1) {
            viewModel.getSingleNotes(id)
        }
    }

    val getSingleNotes by viewModel.getSingleNotes.collectAsState()
    val note = getSingleNotes ?: GetAllNotesItem(
        id = 0, name = "", description = "", createdTimeORDate = ""
    )

    var heading by remember { mutableStateOf(note.name) }
    var description by remember { mutableStateOf(note.description) }
    var isModified by remember { mutableStateOf(false) }

    LaunchedEffect(note) {
        if (note.name.isNotEmpty()) heading = note.name
        if (note.description.isNotEmpty()) description = note.description
    }

    LaunchedEffect(heading, description) {
        isModified = heading != note.name || description != note.description
    }

    LaunchedEffect(updateResult) {
        updateResult?.let {
            Toast.makeText(
                context, context.getString(R.string.note_updated_successfully), Toast.LENGTH_SHORT
            ).show()
            viewModel.resetSaveNotesState()
            navController.navigate(NotesManagerConstants.MAIN_SCREEN)
        }
    }

    LaunchedEffect(saveNotes) {
        saveNotes?.let {
            Toast.makeText(
                context, context.getString(R.string.notes_saved_successfully), Toast.LENGTH_SHORT
            ).show()
            viewModel.resetSaveNotesState()
            navController.navigate(NotesManagerConstants.MAIN_SCREEN)
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackBarHostState) }) { paddingValues ->
        Surface(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ConstraintLayout(Modifier.fillMaxSize()) {
                val (arrowBack, headingText, outlinedTextFieldDescription, update, outlinedTextFieldHeading, loader) = createRefs()

                Image(painter = painterResource(R.drawable.baseline_arrow_back_24),
                    contentDescription = "",
                    Modifier
                        .size(25.dp)
                        .constrainAs(arrowBack) {
                            top.linkTo(parent.top, 35.dp)
                            start.linkTo(parent.start, 20.dp)
                        }
                        .clickable {
                            navController.popBackStack()
                            Prefs.remove(NotesManagerConstants.ID)
                        })
                Text(text = if (Prefs.contains(NotesManagerConstants.ID)) "Update Note" else "Create New Note",
                    fontSize = 16.sp,
                    modifier = Modifier.constrainAs(headingText) {
                        start.linkTo(arrowBack.end)
                        end.linkTo(parent.end)
                        top.linkTo(arrowBack.top)
                        bottom.linkTo(arrowBack.bottom)
                    })

                EditTextField(80,
                    if (note.name.isEmpty()) context.getString(R.string.write_heading_here) else heading,
                    heading,
                    onQueryChanged = {
                        heading = it
                        isModified = true
                    },
                    Modifier.constrainAs(outlinedTextFieldHeading) {
                        top.linkTo(arrowBack.bottom, 20.dp)
                        start.linkTo(parent.start, 10.dp)
                        end.linkTo(parent.end, 10.dp)
                        width = Dimension.fillToConstraints
                    })

                EditTextField(250,
                    if (note.description.isEmpty()) context.getString(R.string.write_description_here) else description,
                    description,
                    onQueryChanged = {
                        description = it
                        isModified = true
                    },
                    Modifier.constrainAs(outlinedTextFieldDescription) {
                        top.linkTo(outlinedTextFieldHeading.bottom, 5.dp)
                        start.linkTo(parent.start, 10.dp)
                        end.linkTo(parent.end, 10.dp)
                        width = Dimension.fillToConstraints
                    })
                if (isLoading) {
                    CircularProgressIndicator(Modifier.constrainAs(loader) {
                        start.linkTo(outlinedTextFieldDescription.start, 10.dp)
                        end.linkTo(outlinedTextFieldDescription.end, 10.dp)
                        bottom.linkTo(parent.bottom, 30.dp)
                    }, color = Color.Red, strokeWidth = 3.dp)
                } else {
                    Button(
                        onClick = {
                            if (Prefs.contains(NotesManagerConstants.ID)) {
                                val calendar = Calendar.getInstance()
                                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val formattedDateTime = formatter.format(calendar.time)
                                viewModel.updateNotes(
                                    id, PostNewNotes(
                                        name = heading,
                                        description = description,
                                        date = formattedDateTime
                                    )
                                )
                            } else {
                                createNewNotes(context, heading, description, viewModel)
                            }
                        }, enabled = isModified, modifier = Modifier
                            .height(50.dp)
                            .constrainAs(update) {
                                start.linkTo(outlinedTextFieldDescription.start, 10.dp)
                                end.linkTo(outlinedTextFieldDescription.end, 10.dp)
                                bottom.linkTo(parent.bottom, 30.dp)
                                width = Dimension.fillToConstraints
                            }, colors = ButtonDefaults.buttonColors(Pink80)
                    ) {
                        Text(
                            text = if (Prefs.contains(NotesManagerConstants.ID)) "Update" else "Save",
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

fun createNewNotes(
    context: Context, heading: String, description: String, viewModel: MainViewModel
) {
    if (heading.isEmpty() || heading.isBlank()) {
        Toast.makeText(
            context, context.getString(R.string.please_enter_heading), Toast.LENGTH_SHORT
        ).show()
        return
    } else if (description.isEmpty() || description.isBlank()) {
        Toast.makeText(
            context, context.getString(R.string.please_enter_description), Toast.LENGTH_SHORT
        ).show()
        return
    } else {
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDateTime = formatter.format(calendar.time)
        viewModel.createNewNotes(
            PostNewNotes(
                heading.trim(), description.trim(), formattedDateTime
            )
        )
    }
}

@Composable
fun EditTextField(
    height: Int,
    text: String,
    query: String,
    onQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isHeading = text.equals("heading", ignoreCase = true)
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .padding(10.dp),
        placeholder = {
            Text(
                text, modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            )
        },
        shape = RoundedCornerShape(10.dp),
        maxLines = if (text.equals("heading", ignoreCase = true)) 1 else Int.MAX_VALUE,
        keyboardOptions = if (isHeading) KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        else KeyboardOptions.Default
    )
}

@Preview(showBackground = true)
@Composable
fun CreateNotesScreenPreview() {
    //CreateNotesScreen(/*context, viewModel*/)
}