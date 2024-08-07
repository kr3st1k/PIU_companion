package com.kr3st1k.pumptracker.core.network.parsers

import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.kr3st1k.pumptracker.core.network.data.title.TitleItem
import com.kr3st1k.pumptracker.core.network.data.title.TitleShop

object TitleShopParser : Parser<TitleShop?>() {

    private fun parseTitleItem(element: Element): TitleItem {

        val name = element.attr("data-name").toString()

        val isAchieved = element.attr("class").toString() == "have"

        val isSelected =
            element.select("button.stateBox.bg2").isNotEmpty()

        val description =
            element.select("p.t3.tx").first()?.select("i.txt")?.first()?.text() ?: "No description"

        val value =
            element.select("div.in").first()?.select("div.state_w")?.first()?.select("form")
                ?.first()?.select("input")
                ?.attr("value")?.toString() ?: "null"

        return TitleItem(name, isAchieved, isSelected, description, value)

    }

    override fun parse(document: Document): TitleShop? {
        val avatarTable = document.select("ul.data_titleList2.flex.wrap")
        val boughtTitles = avatarTable.select("li.have")
        val nonBoughtTitles = avatarTable.select("li.not")

        if (avatarTable.first() == null)
            return null

        val titles: MutableList<TitleItem> = mutableListOf()

        for (element in boughtTitles)
            titles.add(parseTitleItem(element))

        for (element in nonBoughtTitles)
            titles.add(parseTitleItem(element))

        return TitleShop(UserParser.parse(document), titles)
    }
}