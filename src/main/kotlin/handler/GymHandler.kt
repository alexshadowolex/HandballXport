package alex.handball.handler

import alex.handball.getHtmlElementsFromHandballNet
import alex.handball.getHtmlElementsFromNuLiga
import alex.handball.logger
import kotlinx.serialization.Serializable

class GymHandler {
    private val gymLinkPrefixHandballNet = "https://www.handball.net"
    private val allGymsLinkHandballNet = "https://www.handball.net/spielbetrieb/hallen?verband=14"

    private val gymLinkPrefixNuLiga = "https://hvs-handball.liga.nu"
    private val allGymsLinksNuLiga = listOf(
        "https://hvs-handball.liga.nu/cgi-bin/WebObjects/nuLigaHBDE.woa/wa/locationSearch?searchPattern=DE.MI.02.01&federation=HVS&regionName=Region+Leipzig&federations=HVS",
        "https://hvs-handball.liga.nu/cgi-bin/WebObjects/nuLigaHBDE.woa/wa/locationSearch?searchPattern=DE.MI.02.02&federation=HVS&regionName=Region+Sachsen-Mitte&federations=HVS",
        "https://hvs-handball.liga.nu/cgi-bin/WebObjects/nuLigaHBDE.woa/wa/locationSearch?searchPattern=DE.MI.02.03&federation=HVS&regionName=Region+S%C3%BCdwestsachsen&federations=HVS",
        "https://hvs-handball.liga.nu/cgi-bin/WebObjects/nuLigaHBDE.woa/wa/locationSearch?searchPattern=DE.MI.02.04&federation=HVS&regionName=Region+Ostsachsen&federations=HVS"
    )

    private val gyms = mutableSetOf<Gym>()

    fun addGym(gym: Gym) {
        gyms.add(gym)
    }

    fun getGymById(gymId: String): Gym? {
        return gyms.find { it.gymId == gymId }
    }

    fun getGymByName(gymName: String): Gym? {
        return gyms.find { it.gymName.lowercase() == gymName.lowercase() }
    }

    suspend fun initializeLoadingAllGymData() {
        logger.info("Starting to read all gym data from handball.net")
        val rootElementClassName = "entity-cards-grid"
        val elementCssQuery = "a.entity-card"

        val gymHtmlListHandballNet = getHtmlElementsFromHandballNet(allGymsLinkHandballNet, rootElementClassName, elementCssQuery)

        gyms.addAll(gymHtmlListHandballNet.map {
            val gymName = it.select("h3.entity-name").first()!!.text()
            val gymLinkSuffix = it.attr("href")
            val gymLink = gymLinkPrefixHandballNet + gymLinkSuffix
            val gymId = gymLinkSuffix.substringAfterLast("/")

            Gym(
                gymId = gymId,
                gymName = gymName,
                gymLink = gymLink
            )
        })

        allGymsLinksNuLiga.forEach { allGymsLinkNuLiga ->
            val gymHtmlListNuLiga = getHtmlElementsFromNuLiga(allGymsLinkNuLiga)
            gyms.addAll(gymHtmlListNuLiga.map {
                val firstTdTagElement = it.select("td").first()!!
                val aTagElement = firstTdTagElement.select("a").first()!!
                val gymName = aTagElement.text()
                val gymLinkSuffix = aTagElement.attr("href")
                val gymLink = gymLinkPrefixNuLiga + gymLinkSuffix
                val gymId = firstTdTagElement.ownText().replace("(", "").replace(")", "")

                Gym(
                    gymId = gymId,
                    gymName = gymName,
                    gymLink = gymLink
                )
            })
        }

        logger.info("Finished reading all gym data from handball.net")
    }
}

@Serializable
data class Gym (
    val gymId: String,
    val gymName: String,
    val gymLink: String
)

val EMPTY_GYM = Gym("n.n.", "Nicht festgelegt", "")