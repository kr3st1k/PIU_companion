package com.kr3st1k.pumptracker.core.network.parsers

import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.kr3st1k.pumptracker.core.helpers.Utils
import com.kr3st1k.pumptracker.core.helpers.Utils.getBackgroundImg
import com.kr3st1k.pumptracker.core.helpers.Utils.parseTypeDifficultyFromUri
import com.kr3st1k.pumptracker.core.network.data.score.Pumbility
import com.kr3st1k.pumptracker.core.network.data.score.PumbilityScore

//import java.util.Locale

object PumbilityParser : Parser<Pumbility>() {

    private fun parsePumbilityScore(element: Element): PumbilityScore {

        val score = element
            .select("div.score")
            .first()!!
            .select("i.tt.en")
            .first()!!
            .text()

        val songName = element.select("div.profile_name").first()!!.select("p.t1").first()!!.text()

        val typeDiffImgUri = element.select("div.tw").select("img").attr("src")

        var typeDiff = parseTypeDifficultyFromUri(typeDiffImgUri)!!

        typeDiff = when (typeDiff) {
            "c" -> "CO-OP x"
            "u" -> "UCS"
            else -> typeDiff.uppercase()
        }
        val bg = getBackgroundImg(element.select("div.re.bgfix").first()!!, false)

        val diffElems = element.select("div.imG")

        var diff = ""

        for (i in diffElems) {
            diff += Utils.parseDifficultyFromUri(i.select("img").attr("src"))
        }

        diff = typeDiff + diff


        val rankImg = element.select("div.grade_wrap").first()!!.select("img").attr("src")
        var rank = Utils.parseRankFromUri(rankImg).toString()
        rank = rank.replace("_p", "+").replace("_P", "+")
            .replace("X_", "Broken ")

        val datePlay = element.select("div.date").first()!!.select("i.tt").text()


        return PumbilityScore(
            songName,
            bg,
            diff,
            score,
            rank,
            datePlay
        )
    }


    override fun parse(document: Document): Pumbility {
        val res: MutableList<PumbilityScore> = mutableListOf()

        val user = UserParser.parse(document)

        val scoreTable = document
            .select("div.rating_rangking_list_w.top_songSt.pumblitiySt.mgT1")
            .first()!!
            .select("ul.list")
            .first()!!
        val scores = scoreTable.select("li").filter { element ->
            element.select("div.in.flex.vc.wrap").count() == 1
        }
        for (element in scores) {
            res.add(parsePumbilityScore(element))
        }
        return Pumbility(user, res)
    }

}