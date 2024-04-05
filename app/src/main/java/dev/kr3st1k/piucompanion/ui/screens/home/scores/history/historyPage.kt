package dev.kr3st1k.piucompanion.ui.screens.home.scores.history

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.kr3st1k.piucompanion.core.helpers.Utils
import dev.kr3st1k.piucompanion.ui.components.MyAlertDialog
import dev.kr3st1k.piucompanion.ui.components.YouSpinMeRightRoundBabyRightRound
import dev.kr3st1k.piucompanion.ui.components.home.scores.latest.LazyLatestScore

@SuppressLint("MutableCollectionMutableState")
@Composable
fun HistoryPage(
    navController: NavController,
    lifecycleOwner: LifecycleOwner,
) {
    val viewModel = viewModel<HistoryViewModel>()

    val checkLogin =
        Utils.rememberLiveData(viewModel.checkLogin, lifecycleOwner, initialValue = false)
    val checkingLogin =
        Utils.rememberLiveData(viewModel.checkingLogin, lifecycleOwner, initialValue = true)
    val scores = Utils.rememberLiveData(viewModel.scores, lifecycleOwner, initialValue = null)

    Column(modifier = Modifier.fillMaxSize()) {
        if (checkingLogin.value) {
            YouSpinMeRightRoundBabyRightRound("Check if you logged in...")
        } else {
            if (checkLogin.value) {
                if (scores.value.isNotEmpty()) {
                    LazyLatestScore(scores.value, onRefresh = { viewModel.loadScores() })
                } else {
                    YouSpinMeRightRoundBabyRightRound("Getting latest scores...")
                }
            } else {
                MyAlertDialog(
                    showDialog = true,
                    title = "Login failed!",
                    content = "You need to authorize",
                    onDismiss = {}
                )
            }
        }
    }
}