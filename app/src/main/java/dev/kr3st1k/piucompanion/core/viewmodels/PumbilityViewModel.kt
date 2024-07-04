package dev.kr3st1k.piucompanion.core.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.kr3st1k.piucompanion.core.db.dao.ScoresDao
import dev.kr3st1k.piucompanion.core.db.data.PumbilityScore
import dev.kr3st1k.piucompanion.core.helpers.Utils
import dev.kr3st1k.piucompanion.core.modules.DbManager
import dev.kr3st1k.piucompanion.core.network.NetworkRepositoryImpl
import dev.kr3st1k.piucompanion.core.network.data.User
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PumbilityViewModel : ViewModel() {
    val scores = MutableStateFlow<List<PumbilityScore>>(mutableListOf())
    val user: MutableState<User?> = mutableStateOf(null)
    private val db = DbManager()
    private val scoresDao: ScoresDao = db.getScoreDao()
    val isRefreshing = MutableStateFlow(false)

    init {
        loadScores()
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun fetchAndAddToDb() {
        viewModelScope.launch {
            isRefreshing.value = true
            val tmp = NetworkRepositoryImpl.getPumbilityInfo()
            user.value = tmp?.user
            tmp?.scores?.forEach {
                GlobalScope.async {
                    scoresDao.insertPumbility(
                        PumbilityScore(
                            songName = it.songName,
                            songBackgroundUri = it.songBackgroundUri,
                            difficulty = it.difficulty,
                            score = it.score,
                            rank = it.rank,
                            datetime = it.datetime,
                            hash = Utils.generateHashForScore(
                                it.score,
                                it.difficulty,
                                it.songName,
                                it.datetime
                            )
                        )
                    )
                }.await()
            }
            scores.value = GlobalScope.async { scoresDao.getAllPumbilityScores() }.await()
            isRefreshing.value = false
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun loadScores() {
        viewModelScope.launch {
            scores.value = GlobalScope.async { scoresDao.getAllPumbilityScores() }.await()
            fetchAndAddToDb()
//            scores.value = mutableListOf()
//            val data = NetworkRepositoryImpl.getPumbilityInfo()
//            if (data == null) {
//                scores.value = null
//            } else {
//                scores.value = data.scores
//                user.value = data.user
//            }
        }
    }
}