package com.kr3st1k.pumptracker.core.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kr3st1k.pumptracker.core.helpers.Crypto
import com.kr3st1k.pumptracker.core.network.NetworkRepositoryImpl
import com.kr3st1k.pumptracker.di.LoginManager
import kotlinx.coroutines.launch

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
                    .let {
                        Crypto.encryptData(password.value.text)
                            .let { it1 -> LoginManager().saveLoginData(it, it1) }
                    }
                enterHomeScreen.value = true
            } else
                showFailedDialog.value = true
            isLoading.value = false
        }
    }
}