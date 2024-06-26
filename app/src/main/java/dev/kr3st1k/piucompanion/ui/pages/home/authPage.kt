package dev.kr3st1k.piucompanion.ui.pages.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dev.kr3st1k.piucompanion.core.helpers.Crypto
import dev.kr3st1k.piucompanion.core.modules.LoginManager
import dev.kr3st1k.piucompanion.core.network.NetworkRepositoryImpl
import dev.kr3st1k.piucompanion.ui.components.AlertDialogWithButton
import dev.kr3st1k.piucompanion.ui.pages.Screen
import kotlinx.coroutines.launch

@Composable
fun LoginPage(viewModel: LoginViewModel, navController: NavController) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val controller = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AlertDialogWithButton(
            showDialog = viewModel.showFailedDialog.value, title = "Failed",
            content = "Try Again", onDismiss = {
                viewModel.showFailedDialog.value = false
            })
        if (viewModel.enterHomeScreen.value)
            navController.navigate(Screen.HistoryPage.route) {
                popUpTo(Screen.LoginPage.route) {
                    inclusive = true
                }
                popUpTo(Screen.NewsPage.route) {
                    inclusive = true
                }
                popUpTo(Screen.AuthLoadingPage.route) {
                    inclusive = true
                }
            }
        OutlinedTextField(
            value = viewModel.username.value,
            onValueChange = { viewModel.username.value = it },
            singleLine = true,
            modifier = Modifier.padding(4.dp),
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
        OutlinedTextField(
            value = viewModel.password.value,
            onValueChange = { viewModel.password.value = it },
            label = { Text("Password") },
            modifier = Modifier.padding(4.dp),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { this.defaultKeyboardAction(ImeAction.Done); viewModel.onLoginClicked() }
            ),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, description)
                }
            }

        )
        Button(
            onClick = {
                controller?.hide()
                viewModel.onLoginClicked()
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Login")
        }
    }
    if (viewModel.isLoading.value)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
}

class LoginViewModel : ViewModel() {
    val username = mutableStateOf(TextFieldValue())
    val password = mutableStateOf(TextFieldValue())
    val showFailedDialog = mutableStateOf(false)
    val enterHomeScreen = mutableStateOf(false)
    val isLoading = mutableStateOf(false)

    fun onLoginClicked() {
        if (username.value.text == "" || password.value.text == "") {
            showFailedDialog.value = true
            return
        }
        viewModelScope.launch {
            isLoading.value = true
            val r = NetworkRepositoryImpl.loginToAmPass(username.value.text, password.value.text)
            if (r) {
                Crypto.encryptData(username.value.text)
                    ?.let {
                        Crypto.encryptData(password.value.text)
                            ?.let { it1 -> LoginManager().saveLoginData(it, it1) }
                    }
                enterHomeScreen.value = true
            } else
                showFailedDialog.value = true
            isLoading.value = false
        }
    }
}
