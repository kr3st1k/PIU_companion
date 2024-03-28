package dev.kr3st1k.piucompanion.screens.home.scores.best

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.kr3st1k.piucompanion.components.MyAlertDialog
import dev.kr3st1k.piucompanion.components.Utils
import dev.kr3st1k.piucompanion.components.YouSpinMeRightRoundBabyRightRound
import dev.kr3st1k.piucompanion.components.home.scores.best.DropdownMenuBestScores
import dev.kr3st1k.piucompanion.components.home.scores.best.LazyBestScore
import dev.kr3st1k.piucompanion.helpers.PreferencesManager
import dev.kr3st1k.piucompanion.objects.checkAndSaveNewUpdatedFiles
import dev.kr3st1k.piucompanion.objects.readBgJson
import dev.kr3st1k.piucompanion.screens.Screen

@SuppressLint("MutableCollectionMutableState", "CoroutineCreationDuringComposition")
@Composable
fun BestUserPage(
    navControllerGlobal: NavController,
    lifecycleOwner: LifecycleOwner,
)
{
    val context = LocalContext.current

    checkAndSaveNewUpdatedFiles(context)

    val viewModel = viewModel<BestUserViewModel>(
        factory = BestUserViewModelFactory(
            PreferencesManager(context),
            bgsFunc = { readBgJson(context) }
        )
    )

    val checkingLogin = Utils.rememberLiveData(
        liveData = viewModel.checkingLogin,
        lifecycleOwner,
        initialValue = true
    )
    val checkLogin = Utils.rememberLiveData(
        liveData = viewModel.checkLogin,
        lifecycleOwner,
        initialValue = false
    )
    val isRecent =
        Utils.rememberLiveData(liveData = viewModel.isRecent, lifecycleOwner, initialValue = false)
    val scores =
        Utils.rememberLiveData(liveData = viewModel.scores, lifecycleOwner, initialValue = null)
    val selectedOption = Utils.rememberLiveData(
        liveData = viewModel.selectedOption,
        lifecycleOwner,
        initialValue = Pair("All", "")
    )

    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        DropdownMenuBestScores(
            viewModel.options,
            selectedOption.value,
            onUpdate = { viewModel.refreshScores(it) })
        if (checkingLogin.value) {
            YouSpinMeRightRoundBabyRightRound("Check if you logged in...")
        } else {
            if (checkLogin.value) {
                if (scores.value.isNotEmpty()) {
                    LazyBestScore(
                        scores.value,
                        onRefresh = { viewModel.loadScores() },
                        onLoadNext = { viewModel.addScores() },
                        isRecent = isRecent.value
                    )
                }
                else
                {
                    YouSpinMeRightRoundBabyRightRound("Getting best scores...")
                }
            } else {
                MyAlertDialog(
                    showDialog = true,
                    title = "Login failed!",
                    content = "You need to authorize",
                    onDismiss = {
                        navControllerGlobal.navigate(Screen.LoginWebViewScreen.route) {
                            popUpTo(Screen.HomeScreen.route) { inclusive = false }
                        }
                    }
                )
            }
        }
    }
}