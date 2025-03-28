package android.notesmanager.ui.theme.screen

import android.notesmanager.R
import android.notesmanager.ui.theme.NotesManagerTheme
import android.notesmanager.ui.theme.dataclass.GetAllNotesItem
import android.notesmanager.ui.theme.ui.theme.Pink80
import android.notesmanager.ui.theme.utils.NetworkUtils
import android.notesmanager.ui.theme.utils.NotesManagerConstants
import android.notesmanager.ui.theme.viewmodels.MainViewModel
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.pixplicity.easyprefs.library.Prefs

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onFabClick: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allNotes = viewModel.allNotes.collectAsState(initial = emptyList()).value
    val deleteNotesResult by viewModel.deleteNotes.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val context = LocalContext.current
    val isInterNetAvailable: Boolean = NetworkUtils.isInternetAvailable(context)
    val errorMessage by viewModel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    if (!isInterNetAvailable) {
        Toast.makeText(
            context, context.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT
        ).show()
        return
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                if (message.contains("404")) context.getString(
                    R.string.server_unavailable
                ) else context.getString(R.string.some_error_occurred)
            )
            viewModel.clearErrorMessage()
        }
    }

    LaunchedEffect(deleteNotesResult) {
        deleteNotesResult?.let {
            Toast.makeText(
                context, context.getString(R.string.notes_deleted_successfully), Toast.LENGTH_SHORT
            ).show()
            viewModel.clearDeleteNotesState()
        }
    }
    var searchQuery by remember { mutableStateOf("") }

    val filterList = if (searchQuery.isEmpty()) {
        allNotes
    } else {
        allNotes.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Surface(
            modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ConstraintLayout(Modifier.fillMaxSize()) {
                val (text, searchBar, loader, lazyColumn, addNotes) = createRefs()
                Text(
                    text = stringResource(R.string.app_name),
                    fontSize = 24.sp,
                    modifier = Modifier.constrainAs(text) {
                        top.linkTo(parent.top, 40.dp)
                        start.linkTo(parent.start, 15.dp)
                    },
                )
                SearchBar(query = searchQuery,
                    onQueryChanged = { searchQuery = it },
                    onClear = { searchQuery = "" },
                    Modifier.constrainAs(searchBar) {
                        top.linkTo(text.bottom, 10.dp)
                        start.linkTo(parent.start, 10.dp)
                        end.linkTo(parent.end, 10.dp)
                    })
                if (!isLoading) LazyColumn(
                    Modifier
                        .padding(horizontal = 10.dp)
                        .constrainAs(lazyColumn) {
                            top.linkTo(searchBar.bottom, 10.dp)
                            start.linkTo(searchBar.start)
                            end.linkTo(searchBar.end)
                            bottom.linkTo(parent.bottom, 20.dp)
                            height = Dimension.fillToConstraints
                            width = Dimension.fillToConstraints
                        },
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(if (filterList.isNotEmpty()) filterList.size else allNotes.size) { note ->
                        LazyColumnItems(
                            if (filterList.isNotEmpty()) filterList[note] else allNotes[note],
                            viewModel,
                            onEditClick
                        )
                    }
                } else {
                    CircularProgressIndicator(Modifier.constrainAs(loader) {
                        top.linkTo(searchBar.bottom)
                        start.linkTo(searchBar.start)
                        end.linkTo(searchBar.end)
                        bottom.linkTo(parent.bottom)
                    }, color = Color.Red, strokeWidth = 3.dp)
                }
                FloatingActionButton(onClick = {
                    onFabClick()
                }, Modifier.constrainAs(addNotes) {
                    bottom.linkTo(parent.bottom, 25.dp)
                    end.linkTo(parent.end, 25.dp)
                }, containerColor = Pink80) {
                    Image(
                        painter = painterResource(R.drawable.baseline_edit_24),
                        contentDescription = ""
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyColumnItems(
    getAllNotesItem: GetAllNotesItem, viewModel: MainViewModel, onEditClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    Box(
        Modifier
            .fillMaxWidth()
            .height(90.dp)
            .background(
                Color.Gray.copy(0.1f), RoundedCornerShape(10.dp)
            )
            .combinedClickable(onClick = {}, onLongClick = { showDialog = true })
    ) {
        ConstraintLayout(Modifier.fillMaxSize()) {
            val (heading, desc, createdAt, arrow) = createRefs()
            Text(text = getAllNotesItem.name, Modifier.constrainAs(heading) {
                top.linkTo(parent.top, 10.dp)
                start.linkTo(parent.start, 10.dp)
            }, fontSize = 14.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Text(
                text = getAllNotesItem.description, Modifier.constrainAs(desc) {
                    top.linkTo(heading.bottom)
                    start.linkTo(heading.start)
                    end.linkTo(arrow.start, 10.dp)
                    width = Dimension.fillToConstraints
                }, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Updated at: " + (getAllNotesItem.createdTimeORDate ?: "N/A"),
                Modifier.constrainAs(createdAt) {
                    top.linkTo(desc.bottom)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(heading.start)
                },
                fontSize = 10.sp
            )
            Image(painter = painterResource(R.drawable.baseline_arrow_circle_right_24),
                contentDescription = "",
                Modifier
                    .constrainAs(arrow) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end, 20.dp)
                    }
                    .size(20.dp))
        }
    }
    if (showDialog) {
        AlertDialog(onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.note_options)) },
            text = { Text(stringResource(R.string.what_would_you_like_to_do_with_this_note)) },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onEditClick()
                    Prefs.putInt(NotesManagerConstants.ID, getAllNotesItem.id)
                }) {
                    Text(stringResource(R.string.edit))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    viewModel.deleteNotes(getAllNotesItem.id)
                }) {
                    Text(stringResource(R.string.delete))
                }
            })
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(10.dp),
        placeholder = {
            Text(
                "Search...", modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            )
        },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Clear Search")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(10.dp)
    )
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NotesManagerTheme {
        //Greeting()
    }
}