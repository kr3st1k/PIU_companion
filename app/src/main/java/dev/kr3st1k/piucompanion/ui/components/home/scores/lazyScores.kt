package dev.kr3st1k.piucompanion.ui.components.home.scores

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.kr3st1k.piucompanion.core.db.data.score.BestScore
import dev.kr3st1k.piucompanion.core.db.data.score.Score
import dev.kr3st1k.piucompanion.di.BgManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LazyScores(
    scores: List<Score>,
    onRefresh: () -> Unit,
    dropDownMenu: @Composable (() -> Unit)? = null,
    listState: LazyGridState,
    item: @Composable (() -> Unit)? = null,
    isRefreshing: Boolean,
) {
    val state = rememberPullToRefreshState()
    val bgs = BgManager().readBgJson()

    PullToRefreshBox(
        contentAlignment = Alignment.TopCenter,
        state = state,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(370.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (dropDownMenu != null)
                item(span = {
                    GridItemSpan(maxLineSpan)
                }) {
                    dropDownMenu()
                }
            if (item != null)
                item(span = {
                    GridItemSpan(maxLineSpan)
                }) {
                    item()
                }
            if (scores.isNotEmpty()) {
                items(scores.take(50)) { data ->
                    if (data.songBackgroundUri == null)
                        data.songBackgroundUri =
                            bgs.find { tt -> tt.song_name == data.songName }?.jacket
                                ?: "https://www.piugame.com/l_img/bg1.png"
                    ScoreCard(data)
                }
                if (scores.count() > 50) {
                    if (scores.first() is BestScore && item != null)
                        item(span = {
                            GridItemSpan(maxLineSpan)
                        }) {
                            HorizontalDivider(
                                modifier = Modifier.padding(bottom = 4.dp),
                                thickness = 2.dp,
                                color = Color(0xFF222933)
                            )
                        }
                    items(scores.subList(51, scores.count())) { data ->
                        if (data.songBackgroundUri == null)
                            data.songBackgroundUri =
                                bgs.find { tt -> tt.song_name == data.songName }?.jacket
                                    ?: "https://www.piugame.com/l_img/bg1.png"
                        ScoreCard(data)

                    }
                }
            }
        }
    }
}