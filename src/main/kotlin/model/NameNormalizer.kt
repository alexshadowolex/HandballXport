package alex.handball.model

import alex.handball.getSetDataFromJsonFile
import alex.handball.json
import kotlinx.serialization.Serializable
import java.io.File

class NameNormalizer {
    var teamAbbreviations = mutableSetOf<String>()
        private set(value) {
            field = value
            teamAbbreviationsSaveFile.writeText(json.encodeToString(field))
        }
    private val teamAbbreviationsSaveFile = File("data\\saveData\\teamAbbreviations.json")

    var gymAbbreviations = mutableSetOf<String>()
        private set(value) {
            field = value
            gymAbbreviationsSaveFile.writeText(json.encodeToString(field))
        }
    private val gymAbbreviationsSaveFile = File("data\\saveData\\gymAbbreviations.json")

    var specialTeamNames = mutableSetOf<SpecialTeamName>()
        private set(value) {
            field = value
            specialTeamNamesSaveFile.writeText(json.encodeToString(field))
        }
    private val specialTeamNamesSaveFile = File("data\\saveData\\specialTeamNames.json")

    var specialGymNames = mutableSetOf<SpecialGymName>()
        private set(value) {
            field = value
            specialGymNamesSaveFile.writeText(json.encodeToString(field))
        }
    private val specialGymNamesSaveFile = File("data\\saveData\\specialGymNames.json")

    init {
        teamAbbreviations = getSetDataFromJsonFile(teamAbbreviationsSaveFile, String::class).toMutableSet()
        gymAbbreviations = getSetDataFromJsonFile(gymAbbreviationsSaveFile, String::class).toMutableSet()
        specialTeamNames = getSetDataFromJsonFile(specialTeamNamesSaveFile, SpecialTeamName::class).toMutableSet()
        specialGymNames = getSetDataFromJsonFile(specialGymNamesSaveFile, SpecialGymName::class).toMutableSet()
    }

    fun removeTeamAbbreviation(teamAbbreviation: String) {
        val tmpList = teamAbbreviations
        tmpList.remove(teamAbbreviation)

        teamAbbreviations = tmpList
    }

    fun addTeamAbbreviation(teamAbbreviation: String) {
        val tmpList = teamAbbreviations
        tmpList.add(teamAbbreviation)

        teamAbbreviations = tmpList
    }

    fun removeSpecialTeamName(specialTeamName: SpecialTeamName) {
        val tmpList = specialTeamNames
        tmpList.remove(specialTeamName)

        specialTeamNames = tmpList
    }

    fun addSpecialTeamName(specialTeamName: SpecialTeamName) {
        val tmpList = specialTeamNames
        tmpList.add(specialTeamName)

        specialTeamNames = tmpList
    }

    private val romanNumerals = setOf(
        "I",
        "II",
        "III",
        "IV",
        "V",
        "VI"
    )

    private fun normalizeName(
        name: String,
        abbreviations: Set<String>,
        specialNames: Map<String, String> = emptyMap()
    ): String {

        return name
            .lowercase()
            .split(Regex("""(?=\s|"|\()|(?<=\s|"|\()"""))
            .joinToString("") { word ->

                word
                    .split(Regex("(?=[-/])|(?<=[-/])"))
                    .joinToString("") { part ->

                        when {
                            part == "-" || part == "/" || part == "\"" || part == "(" || part.isBlank() ->
                                part

                            specialNames.containsKey(part.uppercase()) ->
                                specialNames.getValue(part.uppercase())

                            part.uppercase() in abbreviations ->
                                part.uppercase()

                            part.uppercase() in romanNumerals ->
                                part.uppercase()

                            else ->
                                part.replaceFirstChar { it.titlecase() }
                        }
                    }
            }
    }

    fun normalizeTeamName(name: String): String =
        normalizeName(
            name = name,
            abbreviations = teamAbbreviations,
            specialNames = specialTeamNames.associate {
                it.key to it.value
            }
        )

    fun normalizeGymName(name: String): String =
        normalizeName(
            name = name,
            abbreviations = gymAbbreviations,
            specialNames = specialGymNames.associate {
                it.key to it.value
            }
        )
}

@Serializable
data class SpecialTeamName (
    val key: String,
    val value: String
)

@Serializable
data class SpecialGymName (
    val key: String,
    val value: String
)