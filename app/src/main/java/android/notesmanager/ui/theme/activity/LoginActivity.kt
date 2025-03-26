package android.notesmanager.ui.theme.activity

import android.notesmanager.ui.theme.ui.theme.NotesManagerTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesManagerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting2(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Greeting2(modifier: Modifier = Modifier) {
    Surface(modifier) {
        ConstraintLayout(Modifier.fillMaxSize()) {
            val userName = remember { mutableStateOf("") }
            val userPassword = remember { mutableStateOf("") }
            val (heading, username, password, login) = createRefs()
            Text(
                text = "Hello,\nWelcome to the login page", Modifier.constrainAs(heading) {
                    top.linkTo(parent.top, 180.dp)
                    start.linkTo(parent.start, 20.dp)
                }, fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                fontFamily = FontFamily.SansSerif
            )
            OutlinedTextField(
                value = userName.value, onValueChange = {
                    userName.value = it
                },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = "person")
                },
                label = {
                    Text(text = "username")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(username) {
                        top.linkTo(heading.bottom, 20.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(20.dp, 20.dp, 20.dp, 0.dp)
            )
            OutlinedTextField(
                value = userPassword.value, onValueChange = {
                    userPassword.value = it
                },
                leadingIcon = {
                    Icon(Icons.Default.Info, contentDescription = "password")
                },
                label = {
                    Text(text = "password")
                },
                modifier = Modifier
                    .constrainAs(password) {
                        top.linkTo(username.bottom, 20.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .fillMaxWidth()
                    .padding(20.dp, 10.dp, 20.dp, 0.dp),
                visualTransformation = PasswordVisualTransformation()
            )
            OutlinedButton(
                onClick = {

                },
                modifier = Modifier
                    .constrainAs(login) {
                        top.linkTo(password.bottom, 20.dp)
                        start.linkTo(password.start)
                        end.linkTo(password.end)
                        width = Dimension.fillToConstraints
                    }
                    .padding(20.dp, 25.dp, 20.dp, 0.dp)
            ) {
                Text(
                    text = "Login",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    NotesManagerTheme {
        Greeting2()
    }
}