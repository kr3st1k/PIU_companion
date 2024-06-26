package dev.kr3st1k.piucompanion.ui.pages.home

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import dev.kr3st1k.piucompanion.core.network.NetworkRepositoryImpl
import dev.kr3st1k.piucompanion.core.network.data.News
import dev.kr3st1k.piucompanion.core.network.data.NewsBanner
import dev.kr3st1k.piucompanion.ui.components.YouSpinMeRightRoundBabyRightRound
import dev.kr3st1k.piucompanion.ui.components.home.news.LazyNews
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@Composable
fun NewsScreen(
    viewModel: NewsViewModel,
    listState: LazyListState,
) {
    val newsBanners by viewModel.newsBanners.collectAsStateWithLifecycle()
    val news = viewModel.news.collectAsStateWithLifecycle()

    LazyNews(
        news = news.value,
        newsBanners = newsBanners,
        listState = listState,
        onRefresh = { viewModel.refreshNews() }
    )
    if (news.value.isEmpty()) {
        YouSpinMeRightRoundBabyRightRound("Getting news...")
    }

}

class NewsViewModel : ViewModel() {
    val newsBanners = MutableStateFlow<MutableList<NewsBanner>>(mutableListOf())
    val news = MutableStateFlow<MutableList<News>>(mutableListOf())

    init {
        loadNews()
    }

    private fun loadNews() {
        viewModelScope.launch {
            newsBanners.value = NetworkRepositoryImpl.getNewsBanners()
            news.value = NetworkRepositoryImpl.getNewsList()
        }
    }

    fun refreshNews() {
        viewModelScope.launch {
            news.value = mutableListOf()
            news.value = NetworkRepositoryImpl.getNewsList()
        }
    }
}