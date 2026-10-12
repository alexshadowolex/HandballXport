package alex.handball.model

import alex.handball.LocalDateSerializer
import alex.handball.getHtmlElementsFromHandballNet
import alex.handball.getSetDataFromJsonFile
import alex.handball.handler.EMPTY_GYM
import alex.handball.handler.Gym
import alex.handball.handler.GymHandler
import alex.handball.json
import alex.handball.logger
import alex.handball.toJavaLocalDate
import java.time.LocalDate
import kotlinx.serialization.Serializable
import org.jsoup.nodes.Element
import java.io.File


class LeaguesDatabase(private val gymHandler: GymHandler) {
    // TODO: Check which leagues (seasons) exist online and suggest them
    val leaguesToFixtures = mutableMapOf</*Fixture's League*/ League, /*Matches in that league*/ List<Fixture>>()
    var leagues = mutableSetOf</*League*/ League>()
        private set(value) {
            field = value
            leaguesDataSaveFile.writeText(json.encodeToString(field))
        }

    private val leaguesDataSaveFile = File("data\\saveData\\leagues.json")
    private val UNKNOWN_TEXT = "n.n."
    private val UNKNOWN_TIME = "00:00"


    init {
        leagues = getSetDataFromJsonFile(leaguesDataSaveFile, League::class)
    }


    /**
     * Helper function to initialize the loading of data
     */
    suspend fun initializeLoadingAllLeagueAndFixtureData() {
        leaguesToFixtures.clear()
        val leaguesToUnparsedHtmlFixtures = readDataFromHandballNet()
        parseHtmlToLeaguesWithFixtures(leaguesToUnparsedHtmlFixtures)

        logger.info("Following Leagues have no fixtures: ${
            leaguesToFixtures
                .filter { it.value.isEmpty() }
                .map { it.key.leagueName }
                .ifEmpty { listOf("none") }
                .joinToString("|")
        }")
    }

    /**
     * TODO Rewrite KDoc
     * Sole reading of the data from NuLiga
     * @return A map with the league is the key and the unparsed HTML as the value
     */
    suspend fun readDataFromHandballNet(): MutableMap<League, List<Element>> {
        logger.info("Starting to read all leagues and fixtures data from handball.net")

        val leaguesToUnparsedHtmlFixtures = mutableMapOf<
                /*Fixture's League*/ League,
                /*Unparsed HTML fixtures in that league*/ List<Element>
                >()

        val rootElementClassName = "matches-list matches-list--panel"
        val elementCssQuery = "app-match-card"

        for(league in leagues) {
            val currentLink = league.leagueLink

            val unparsedHtmlFixtures = getHtmlElementsFromHandballNet(currentLink, rootElementClassName, elementCssQuery)

            if (unparsedHtmlFixtures.isNotEmpty()) {
                leaguesToUnparsedHtmlFixtures[league] = unparsedHtmlFixtures
            }
        }

        logger.info("Finished reading all leagues and fixtures data from handball.net")
        return leaguesToUnparsedHtmlFixtures
    }

    /**
     * TODO Rewrite KDoc
     * Starts the parsing process of the league's HTML to get the list of fixtures
     * @param leaguesToUnparsedHtmlFixtures Map of leagues to unparsed HTML of that league
     * @return Map of leagues to the list of its fixtures
     */
    private fun parseHtmlToLeaguesWithFixtures(
        leaguesToUnparsedHtmlFixtures: MutableMap<League, List<Element>>
    ) {

        for(currentLeagueWithUnparsedHtmlFixtures in leaguesToUnparsedHtmlFixtures) {
            val league = currentLeagueWithUnparsedHtmlFixtures.key
            val htmlFixtures = currentLeagueWithUnparsedHtmlFixtures.value
            val fixtures = parseHtmlElements(htmlFixtures, league)
            leaguesToFixtures[league] = fixtures
        }
    }


    /**
     * TODO Rewrite KDoc
     * Parses the lines of a league's HTML to get a list of fixtures. The data in one line is
     * encoded in <td>-tags
     * @param htmlFixtures The unparsed HTML
     * @param league The given league
     * @return A List of Fixtures for that league
     */
    private fun parseHtmlElements(
        htmlFixtures: List<Element>,
        league: League
    ): List<Fixture> {

        return htmlFixtures.mapNotNull { currentHtmlMatch ->

            league.distinguishingLeagueNameSubstring?.let { distinguishingSubstring ->
                val leagueName = currentHtmlMatch
                    .selectFirst(".league-name")
                    ?.text()
                    .orEmpty()

                if (!leagueName.contains(distinguishingSubstring)) {
                    return@mapNotNull null
                }
            }

            val date = currentHtmlMatch
                .selectFirst(".match-date")
                ?.text()
                ?.substringAfter(", ", missingDelimiterValue = "")
                ?.trim()
                ?.takeIf { it.isNotBlank() }
                ?.let { dateText ->
                    runCatching {
                        dateText.toJavaLocalDate()
                    }.getOrNull()
                }

            val homeTeam = currentHtmlMatch
                .selectFirst(".home-team .team-name")
                ?.text()
                ?.trim()
                ?.takeIf { it.isNotBlank() }
                ?: UNKNOWN_TEXT

            val guestTeam = currentHtmlMatch
                .selectFirst(".away-team .team-name")
                ?.text()
                ?.trim()
                ?.takeIf { it.isNotBlank() }
                ?: UNKNOWN_TEXT

            val time = currentHtmlMatch
                .selectFirst(".time")
                ?.text()
                ?.replace("UHR", "")
                ?.trim()
                ?.takeIf { it.isNotBlank() && it.matches(Regex("""^(?:[01]\d|2[0-3]):[0-5]\d$""")) }
                ?: UNKNOWN_TIME

            val gameId = currentHtmlMatch
                .select("div.detail-text")
                .firstOrNull {
                    it.selectFirst("span.detail-label")
                        ?.text()
                        ?.trim() == "Spielnummer"
                }
                ?.selectFirst("span.detail-value")
                ?.text()
                ?.trim()
                ?.takeIf { it.isNotBlank() }
                ?: UNKNOWN_TEXT

            val gymName = currentHtmlMatch
                .select("div.detail-text")
                .firstOrNull {
                    it.selectFirst("span.detail-label")
                        ?.text()
                        ?.trim() == "Halle"
                }
                ?.selectFirst("span.detail-value")
                ?.text()
                ?.trim()
                ?.takeIf { it.isNotBlank() }
                ?.substringBeforeLast("(")
                ?.trim()
                ?: UNKNOWN_TEXT

            val gym = if (gymName == UNKNOWN_TEXT) {
                EMPTY_GYM
            } else {
                gymHandler.getGymByName(gymName) ?: EMPTY_GYM
            }

            Fixture(
                date = date,
                time = time,
                league = league,
                homeTeam = homeTeam,
                guestTeam = guestTeam,
                gym = gym,
                gameId = gameId
            )
        }
    }


    /**
     * Adds a league to the list and writes the result into the file
     * @param league League class of the new league
     */
    fun addLeagueToDatabase(league: League) {
        val tmpList = leagues
        tmpList.add(league)
        leagues = tmpList
    }


    /**
     * Removes a league from the list and writes the result into the file
     * @param league League class of the league to be removed
     */
    fun removeLeagueFromDatabase(league: League) {
        val tmpList = leagues
        tmpList.remove(league)

        leagues = tmpList
    }


    // getFixturesFromDateRange
}

@Serializable
data class Fixture(
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate?,
    val time: String,
    val league: League,
    val homeTeam: String,
    val guestTeam: String,
    val gym: Gym,
    val gameId: String
)

@Serializable
data class League(
    val leagueLink: String,
    val leagueName: String,
    val isNuLigaLeague: Boolean,
    val distinguishingLeagueNameSubstring: String?
)