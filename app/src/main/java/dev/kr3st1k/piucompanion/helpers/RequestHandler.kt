package dev.kr3st1k.piucompanion.helpers

import android.webkit.CookieManager
import dev.kr3st1k.piucompanion.MainActivity
import dev.kr3st1k.piucompanion.components.Utils
import dev.kr3st1k.piucompanion.objects.BestUserScore
import dev.kr3st1k.piucompanion.objects.BgInfo
import dev.kr3st1k.piucompanion.objects.LatestScore
import dev.kr3st1k.piucompanion.objects.NewsBanner
import dev.kr3st1k.piucompanion.objects.NewsThumbnailObject
import dev.kr3st1k.piucompanion.objects.User
import dev.kr3st1k.piucompanion.objects.WebViewCookieStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.cookies.cookies
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Cookie
import io.ktor.http.parameters
import io.ktor.util.date.GMTDate
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.Calendar
import java.util.Locale


fun MutableList<okhttp3.Cookie?>.toKtorCookie(time: Long): MutableList<Cookie> =
    this.map {
        Cookie(
            name = it?.name ?: "",
            value = it?.value?.replace("%25", "%") ?: "",
            domain = it?.domain ?: "",
            path = it?.path ?: "",
            secure = it?.secure ?: true,
            httpOnly = it?.httpOnly ?: false,
            expires = GMTDate(time),
            maxAge = 86400
        )
    }.toMutableList()

//fun String.fromBase64(): String = String(Base64.decode(this, Base64.DEFAULT))
//
//fun List<Cookie>.toJson(): String = dev.kr3st1k.piucompanion.screens.login.json.encodeToString(map {
//    CookieData(
//        name = it.name,
//        value = it.value,
//        domain = it.domain,
//        path = it.path!!,
//        secure = it.secure,
//        httpOnly = it.httpOnly,
//        expires = it.expires!!.timestamp
//    )
//})
//
//fun String.toBase64(): String = Base64.encodeToString(toByteArray(), Base64.DEFAULT)

//import android.webkit.CookieManager
//
//suspend fun getCookiesFromWebView(url: String): List<Cookie> {
//    val cookieManager = CookieManager.getInstance()
//    val cookies = cookieManager.getCookie(url)
//
//}

object RequestHandler {
    private val client: HttpClient = HttpClient(OkHttp) {
        engine {
            addNetworkInterceptor() { chain ->
                val request = chain.request()

                val ff = request.newBuilder()
                    .removeHeader("Accept")
                    .removeHeader("Accept-Charset")
                    .removeHeader("Accept-Encoding")
                    .removeHeader("Connection")
                    .addHeader("Connection", "keep-alive")
                    .addHeader("sec-ch-ua", MainActivity.secChUa)
                    .addHeader("sec-ch-ua-mobile", "?1")
                    .addHeader("sec-ch-ua-platform", "\"Android\"")
                    .addHeader("Sec-Fetch-Dest", "document")
                    .addHeader("Sec-Fetch-Mode", "navigate")
                    .addHeader("Sec-Fetch-Site", "none")
                    .addHeader("Sec-Fetch-User", "?1")
                    .addHeader(
                        "Accept",
                        "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7"
                    )
                    .addHeader("Accept-Encoding", "gzip, deflate, br, zstd")
                    .addHeader(
                        "Accept-Language",
                        "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7,ru-BY;q=0.6"
                    )
                    .addHeader("Upgrade-Insecure-Requests", "1").build()

                // Log the request headers
                println("Request headers: ${ff.headers}")

                // Proceed with the request
                chain.proceed(ff)
            }
        }

        install(UserAgent) {
            agent = MainActivity.userAgent
        }
        install(HttpCookies) {
            storage = WebViewCookieStorage(
                if (getCookiesFromWebView().isEmpty()) {
                    mutableListOf()
                } else {
                    getCookiesFromWebView()
                }
            )
        }
        followRedirects = true
    };


    private fun getCookiesFromWebView(): MutableList<Cookie> {
        val cookieManager = CookieManager.getInstance()
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val timestamp = calendar.timeInMillis
        var cookies = cookieManager.getCookie("https://am-pass.net")
        if (cookies != null) {
            if (cookies.contains("nullsid") || cookies.split(";").size >= 5) {
                val uri = HttpUrl.Builder()
                    .scheme("https")
                    .host("am-pass.net")
                    .build();
                val uri3 = HttpUrl.Builder()
                    .scheme("https")
                    .host("api.am-pass.net")
                    .build();

                val parsedCookies =
                    cookies.split(";").map { okhttp3.Cookie.parse(uri, it) }
                        .toMutableList()
                cookies = cookieManager.getCookie("https://api.am-pass.net")
                parsedCookies += cookies.split(";")
                    .map { okhttp3.Cookie.parse(uri3, it) }
                parsedCookies += okhttp3.Cookie.parse(uri, "_gat_gtag_UA_210606414_1=1;")
                return parsedCookies.toKtorCookie(timestamp)
            }
        }
        return mutableListOf()
    }

    suspend fun getUpdateInfo(): String {
        val response = client.get("https://kr3st1k.me/piu/update.txt")
        return response.body<String>()
    }

    suspend fun getBgJson(): MutableList<BgInfo> {
        val response = client.get("https://kr3st1k.me/piu/piu_bg_database.json")
        val res = response.body<String>()
        val JSON = Json {isLenient = true}
        val list = JSON.decodeFromString(res) as MutableList<BgInfo>
        return list
    }

    suspend fun checkIfLoginSuccess(): Boolean {

        val t = client.get("https://am-pass.net")
        val stringBody: String = t.body()

        val tt = client.cookies("https://am-pass.net")

        val ttt = t.headers

        return stringBody.indexOf("bbs/logout.php") > 0;
    }

    //UNUSED
    suspend fun loginToAmPass(login: String, password: String, rememberMe: Boolean) {

        val firstReq = this.getDocument(client, "https://am-pass.net")

        val response: HttpResponse = client.submitForm(
            url = "https://am-pass.net/bbs/login_check.php",
            formParameters = parameters {
                append("url", "/")
                append("mb_id", login)
                append("mb_password", password)
                if (rememberMe)
                    append("auto_login", "on")
            }
        )

    }


    private fun getBackgroundImg(element: Element, addDomain: Boolean = true): String {
        val style = element.attr("style")
        return (if (addDomain) "https://www.piugame.com" else "") + style.substringAfter("background-image:url('")
            .substringBefore("')");
    }

    suspend fun getDocument(client: HttpClient, uri: String): Document {
        val req = client.get(uri)
        val reqBody: String = req.body()
        return Jsoup.parse(reqBody);
    }

    suspend fun getNewsBanners(): MutableList<NewsBanner> {

        val t = this.getDocument(client, "https://www.piugame.com")

        val r = t.select("a.img.resize.bgfix");
        val uniqueElements: MutableList<Element> = mutableListOf()
        val res: MutableList<NewsBanner> = mutableListOf();

        for (element in r) {
            if (!uniqueElements.any { it.attr("href") == element.attr("href") }) {
                uniqueElements.add(element)
            }
        }

        for (element in uniqueElements) {
            res.add(
                NewsBanner(
                    Utils.getWrId(element.attr("href"))?.toInt() ?: 0,
                    getBackgroundImg(element),
                    element.attr("href")
                )
            )
        }
        return res;
    }

    suspend fun getNewsList(): MutableList<NewsThumbnailObject> {
        val res: MutableList<NewsThumbnailObject> = mutableListOf();

        val t = this.getDocument(client, "https://www.piugame.com/phoenix_notice")
        val table = t.select("tbody")

        val trElements: Elements = table.select("tr")

        for (elem in trElements) {
            if (res.count() == 7)
                break
            val titleAndLinkElem = elem.select("td.w_tit").select("a");
            val title = titleAndLinkElem.text()
            val typeElem = elem.select("td.w_type").select("i")
            val type = typeElem.text()
            val link = titleAndLinkElem.attr("href")
            val id = Utils.getWrId(titleAndLinkElem.attr("href"))!!.toInt()

            if (type == "Notice" || type == "Event")
                res.add(NewsThumbnailObject(title, id, type, link))
        }

        return res;
    }

    suspend fun getUserInfo(): User {
        val t = this.getDocument(client, "https://www.piugame.com/my_page/play_data.php")

        val profileBox = t.select("div.box0.inner.flex.vc.bgfix")
        val avatarBox = t.select("div.re.bgfix")

        val backgroundUri = getBackgroundImg(profileBox.first()!!)
        val avatarUri = getBackgroundImg(avatarBox.first()!!, false)

        val profileTitleAndName = t.select("div.name_w").select("p")

        val titleName = profileTitleAndName.first()!!.text()
        val username = profileTitleAndName.last()!!.text()
        val recentGame = t.select("div.time_w").select("li").last()!!.text()
            .replace("Recently Access Games : ", "")
        val coinValue = t.select("i.tt.en").first()!!.text()

        return User(username, titleName, backgroundUri, avatarUri, recentGame, coinValue, true)
    }

    private fun parseBestUserScores(t: Document, res: MutableList<BestUserScore>, bgs: MutableList<BgInfo>): Boolean {

        if (t.select("div.no_con").isNotEmpty()) {
            res.add(
                BestUserScore(
                    "No scores",
                    "https://www.piugame.com/l_img/bg1.png",
                    "No scores",
                    "000,000",
                    "SSS+"
                )
            )
            return true
        }
        val scoreTable = t.select("ul.my_best_scoreList.flex.wrap")
        val scores = scoreTable.select("li").filter { element ->
            element.select("div.in").count() == 1
        }

        for (element in scores) {

            val songName = element.select("div.song_name").select("p").first()!!.text()
            var bg = bgs.find { tt -> tt.song_name == songName }?.jacket
            if (bg == null)
                bg = "https://www.piugame.com/l_img/bg1.png"

            val diffElems = element.select("div.numw.flex.vc.hc")

            var diff = ""

            val typeDiffImgUri = getBackgroundImg(
                element.select("div.stepBall_in.flex.vc.col.hc.wrap.bgfix.cont").first()!!, false
            )

            val typeDiff = Utils.parseTypeDifficultyFromUriBestScore(typeDiffImgUri)!!

            for (i in diffElems.select("img")) {
                diff += Utils.parseDifficultyFromUri(i.attr("src"))
            }

            diff = typeDiff.uppercase(Locale.ENGLISH) + diff

            val scoreInfoElement = element.select("ul.list.flex.vc.hc.wrap")

            val score = scoreInfoElement.select("span.num").text()

            var rank = ""

            val rankImg = scoreInfoElement.select("img").first()!!.attr("src")
            rank = Utils.parseRankFromUri(rankImg).toString()
            rank = rank.uppercase(Locale.ENGLISH).replace("_p", "+").replace("_P", "+")

            res.add(
                BestUserScore(
                    songName,
                    bg,
                    diff,
                    score,
                    rank
                )
            )

        }

        return t.select("button.icon").isEmpty()
    }

    suspend fun getBestUserScores(
        page: Int? = null,
        lvl: String = "",
        res: MutableList<BestUserScore> = mutableListOf(),
        bgs: MutableList<BgInfo>,
    ): Pair<MutableList<BestUserScore>, Boolean> {
        var uri = "https://www.piugame.com/my_page/my_best_score.php"
        var isRecent = false
        uri += "?lv=$lvl"
        if (page == null) {
            for (i in 1..2) {
                if (!isRecent) {
                    if (uri.contains("&page"))
                        uri = uri.dropLast(1) + i
                    else
                        uri += "&page=$i"
                    val t =
                        this.getDocument(client, uri)
                    isRecent = parseBestUserScores(t, res, bgs = bgs)
                }
            }
        }
        else
        {
            uri += "&page=$page"
            val t =
                this.getDocument(client, uri)
            isRecent = parseBestUserScores(t, res, bgs=bgs)
        }
        return Pair(res, isRecent)

    }

    suspend fun getLatestScores(length: Int): MutableList<LatestScore> {
        val res: MutableList<LatestScore> = mutableListOf();

        val t = this.getDocument(client, "https://www.piugame.com/my_page/recently_played.php")

        val scoreTable = t.select("ul.recently_playeList.flex.wrap")
        val scores = scoreTable.select("li").filter { element ->
            element.select("div.wrap_in").count() == 1
        }
        for (element in scores) {
            if (res.count() == length)
                break
            val songName = element.select("div.song_name.flex").select("p").text()

            val typeDiffImgUri = element.select("div.tw").select("img").attr("src")

            val typeDiff = Utils.parseTypeDifficultyFromUri(typeDiffImgUri)!!

            val bg = getBackgroundImg(element.select("div.in.bgfix").first()!!, false)

            val diffElems = element.select("div.imG")

            var diff = ""

            for (i in diffElems) {
                diff += Utils.parseDifficultyFromUri(i.select("img").attr("src"))
            }

            diff = typeDiff.uppercase(Locale.ENGLISH) + diff

            val scoreRankElement = element.select("div.li_in.ac")

            val score = scoreRankElement.select("i.tx").text()

            var rank = "F"

            if ("STAGE BREAK" !in score) {
                val rankImg = scoreRankElement.first()!!.select("img").attr("src")
                rank = Utils.parseRankFromUri(rankImg).toString()
                rank = rank.uppercase(Locale.ENGLISH).replace("_p", "+").replace("_P", "+")
                    .replace("X_", "Broken ")
            }

            val datePlay = element.select("p.recently_date_tt").text()
            res.add(
                LatestScore(
                    songName,
                    bg,
                    diff,
                    score,
                    rank,
                    Utils.convertDateFromSite(datePlay)
                )
            )
        }

        return res
    }
}

