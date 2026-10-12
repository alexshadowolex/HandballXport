package alex.handball.handler

import alex.handball.getHtmlElementsFromHandballNet
import alex.handball.logger
import kotlinx.serialization.Serializable

class GymHandler {
    private val gymLinkPrefix = "https://www.handball.net"
    private val link = "https://www.handball.net/spielbetrieb/hallen?verband=14"
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

        val gymHtmlList = getHtmlElementsFromHandballNet(link, rootElementClassName, elementCssQuery)

        gyms.addAll(gymHtmlList.map {
            val gymName = it.select("h3.entity-name").first()!!.text()
            val gymLinkSuffix = it.attr("href")
            val gymLink = gymLinkPrefix + gymLinkSuffix
            val gymId = gymLinkSuffix.substringAfterLast("/")

            Gym(
                gymId = gymId,
                gymName = gymName,
                gymLink = gymLink
            )
        })

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