package dev.kr3st1k.piucompanion.core.network

import com.fleeksoft.ksoup.nodes.Document
import dev.kr3st1k.piucompanion.core.network.data.BgInfo
import dev.kr3st1k.piucompanion.core.network.data.LoadableList
import dev.kr3st1k.piucompanion.core.network.data.ReleaseResponse
import dev.kr3st1k.piucompanion.core.network.data.User
import dev.kr3st1k.piucompanion.core.network.data.avatar.AvatarShop
import dev.kr3st1k.piucompanion.core.network.data.news.News
import dev.kr3st1k.piucompanion.core.network.data.news.NewsBanner
import dev.kr3st1k.piucompanion.core.network.data.score.BestUserScore
import dev.kr3st1k.piucompanion.core.network.data.score.LatestScore
import dev.kr3st1k.piucompanion.core.network.data.score.Pumbility
import dev.kr3st1k.piucompanion.core.network.data.title.TitleShop
import io.ktor.http.Parameters

interface NetworkRepository {
    suspend fun getDocument(
        host: String,
        path: String = "/",
        params: Parameters = Parameters.Empty,
        checkLogin: Boolean = false,
    ): Document?
    suspend fun formPost(
        host: String,
        path: String = "/",
        params: Parameters = Parameters.Empty,
        checkLogin: Boolean = false,
    ): Boolean?
    suspend fun getUpdateInfo(): String
    suspend fun getBgJson(): MutableList<BgInfo>
    suspend fun checkIfLoginSuccessRequest(): Boolean
    suspend fun loginToAmPass(login: String, password: String, rememberMe: Boolean = true): Boolean
    suspend fun getNewsBanners(): MutableList<NewsBanner>
    suspend fun getNewsList(): MutableList<News>
    suspend fun getUserInfo(): User?
    suspend fun getPumbilityInfo(): Pumbility?
    suspend fun setAvatar(value: String): Boolean?
    suspend fun buyAvatar(value: String): Boolean?
    suspend fun getAvatarShopInfo(): AvatarShop?
    suspend fun getGithubUpdateInfo(): ReleaseResponse
    suspend fun getTitleShopInfo(): TitleShop?
    suspend fun setTitle(value: String): Boolean?
    suspend fun getBestUserScores(
        page: Int? = null,
        lvl: String = "",
        res: MutableList<BestUserScore> = mutableListOf(),
    ): LoadableList<BestUserScore>?

    suspend fun getLatestScores(length: Int): MutableList<LatestScore>?
}
