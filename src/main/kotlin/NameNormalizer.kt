package alex.handball

object NameNormalizer {
    private val romanNumerals = setOf(
        "I",
        "II",
        "III",
        "IV",
        "V"
    )

    private val teamAbbreviations = setOf(
        "AH",
        "BSG",
        "BSC",
        "BSV",
        "DJK",
        "ESV",
        "EHV",
        "FC",
        "HC",
        "HV",
        "HSV",
        "HSG",
        "LHV",
        "MTV",
        "NSG",
        "OSV",
        "SC",
        "SG",
        "SV",
        "TSV",
        "TV",
        "USG",
        "USV",
        "TU",
        "SSV",
        "TBSV",
        "MSV",
        "SF",
        "SHV",
        "HCS",
        "HVH"
    )

    private val hallAbbreviations = setOf(
        "SH",
        "GY",
        "WM",
        "JOYNEXT",
        "BSZ",
        "GSH",
        "FSZ"
    )

    private val specialTeamNames = mapOf(
        "TUS" to "TuS",
        "VFB" to "VfB",
        "VFL" to "VfL",
        "DHFK" to "DHfK",

        "LVB" to "LVB",
        "SV04" to "SV04",
        "HCE" to "HCE",
        "MOGONO" to "MoGoNo",
        "LO" to "LO",

        "GROSSENHAIN" to "Großenhain",
        "MEISSEN" to "Meißen",
        "WEISSERITZTAL" to "Weißeritztal",
        "WEISS" to "Weiß",
        "ROSSWEINER" to "Roßweiner",

        "ROL" to "",
        "MA" to ""
    )

    private val specialHallNames = mapOf(
        "2.SPIELFELD" to "2. Spielfeld",
        "BALLSPORTARENA" to "BallsportARENA"
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
            specialNames = specialTeamNames
        )

    fun normalizeGymName(name: String): String =
        normalizeName(
            name = name,
            abbreviations = hallAbbreviations,
            specialNames = specialHallNames
        )
}