package alex.handball.ui.screens

import alex.handball.*
import alex.handball.config.AppConfig
import alex.handball.handler.ExcelColumnIndices
import alex.handball.handler.ExcelExportHandler
import alex.handball.model.Fixture
import alex.handball.model.League
import alex.handball.model.LeaguesDatabase
import alex.handball.model.NameNormalizer
import alex.handball.ui.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.todayIn
import java.time.LocalDate

private val displayFixtures = mutableStateListOf<DisplayFixture>()
private lateinit var fromDate: MutableState<LocalDate>
private lateinit var toDate: MutableState<LocalDate>
val isExcelExportGenerated = mutableStateOf(false)

@Composable
fun exportScreen(model: Model) {
    val nameNormalizer = model.nameNormalizer
    val scaffoldState = rememberScaffoldState()
    fromDate = remember { mutableStateOf(Clock.System.todayIn(TimeZone.currentSystemDefault()).toJavaLocalDate()) }
    toDate = remember { mutableStateOf(getEndDateForPlannerScreen()) }

    MaterialTheme {
        Scaffold (
            scaffoldState = scaffoldState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(all = 15.dp)
            ) {
                screenHeaderRow(headline = "Export")

                Spacer(
                    modifier = Modifier
                        .height(20.dp)
                )

                dateRangeShowGamesSegment(
                    fromDate,
                    toDate,
                    scaffoldState,
                    onShowClick = { fromDate, toDate ->
                        displayFixtures.clear()
                        displayFixtures.addAll(model.leaguesDatabase.getFixturesFromDateRange(
                            fromDate,
                            toDate
                        ).map {
                            DisplayFixture(it)
                        })
                    }
                )

                sectionDivider(thickness = 1.5.dp)

                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column (
                        modifier = Modifier
                            .fillMaxWidth(0.4F)
                            .padding(start = 5.dp)
                    ) {
                        Button(
                            onClick = {
                                deleteAllMarkedDisplayFixtures()
                                backgroundCoroutineScope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar(
                                        message = "Alle markierten Spiele wurden gelöscht",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            enabled = displayFixtures.isNotEmpty()
                        ) {
                            Text("Lösche alle markierten Spiele")
                        }
                    }

                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp)
                    ) {
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.End)
                        ) {
                            Button(
                                onClick = {
                                    setAllMarkedForDeletionSwitches(isMarkedForDeleting = true)
                                },
                                modifier = Modifier
                                    .fillMaxWidth(0.5F),
                                enabled = displayFixtures.isNotEmpty()
                            ) {
                                Text("Setze alle Löschmarkierungen")
                            }

                            Button(
                                onClick = {
                                    setAllMarkedForDeletionSwitches(isMarkedForDeleting = false)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 2.dp),
                                enabled = displayFixtures.isNotEmpty()
                            ) {
                                Text("Entferne alle Löschmarkierungen")
                            }
                        }
                    }
                }

                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column (
                        modifier = Modifier
                            .fillMaxWidth(0.4F)
                            .padding(start = 5.dp)
                    ) {
                        Button(
                            onClick = {
                                val excelExportHandler = ExcelExportHandler(
                                    displayFixtures.map { it.fixture },
                                    ExcelColumnIndices(
                                        dateIndex = 0,
                                        timeIndex = 1,
                                        leagueNameIndex = 2,
                                        gameNumberIndex = 3,
                                        homeTeamIndex = 4,
                                        guestTeamIndex = 5,
                                        gymNameIndex = 6
                                    ),
                                    headerNames = listOf(
                                        "Datum",
                                        "Uhrzeit",
                                        "Liga",
                                        "SpNr",
                                        "Heim",
                                        "Gast",
                                        "Halle"
                                    ),
                                    nameNormalizer
                                )
                                val fileName = excelExportHandler.generateExcelExport()
                                backgroundCoroutineScope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar(
                                        message = if(fileName != null) {
                                            AppConfig.lastGeneratedExcelExportName = fileName
                                            isExcelExportGenerated.value = true
                                            "Generated Excel file ${fileName.addQuotationMarks()} successfully"
                                        } else {
                                            "Error while generating Excel file, try again!"
                                        },
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            enabled = displayFixtures.isNotEmpty()
                        ) {
                            Text("Exportiere Spiele nach Excel")
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth(0.7F)
                            .padding(start = 5.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        if(isExcelExportGenerated.value) {
                            Text(
                                style = MaterialTheme.typography.body1,
                                text = "Öffne Ordner zum letzten generierten Excel Export",
                                modifier = Modifier
                                    .clickable {
                                        backgroundCoroutineScope.launch {
                                            if (AppConfig.lastGeneratedExcelExportName.isNotEmpty()) {
                                                Runtime.getRuntime().exec(
                                                    "explorer.exe " +
                                                            "/select," +
                                                            "${System.getProperty("user.dir")}\\${AppConfig.excelExportOutputDirectoryName}\\" +
                                                            AppConfig.lastGeneratedExcelExportName
                                                )
                                            }
                                        }
                                    }
                                    .pointerHoverIcon(PointerIcon.Hand),
                                textDecoration = TextDecoration.Underline,
                                color = MaterialTheme.colors.secondary,
                            )
                        }
                    }
                }


                sectionDivider(thickness = 3.dp)

                gamePlanList(scaffoldState, nameNormalizer)
            }
        }
    }
}


@Composable
private fun gamePlanList(
    scaffoldState: ScaffoldState,
    nameNormalizer: NameNormalizer
) {
    val scrollState = rememberScrollState(0)
    val scrollBarAdapter = rememberScrollbarAdapter(scrollState)
    Row {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxWidth(0.98F)
                .fillMaxHeight()
        ) {
            if (displayFixtures.isNotEmpty()) {
                Row (
                    modifier = Modifier
                        .padding(start = 30.dp, bottom = 5.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.88f)
                    ) {
                        fixtureInfoHeader()
                    }

                    Text(
                        text = "Löschen?",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 5.dp)
                            .align(Alignment.CenterVertically),
                        textAlign = TextAlign.Center
                    )
                }

                Divider(
                    color = MaterialTheme.colors.secondary,
                    thickness = 1.5.dp
                )

                var currentDate: LocalDate? = null
                displayFixtures.forEach { displayFixture ->
                    if (currentDate != null) {
                        Divider(
                            color = MaterialTheme.colors.secondary,
                            thickness = if (currentDate != displayFixture.fixture.date) {
                                2.dp
                            } else {
                                1.dp
                            }
                        )
                    }

                    fixtureDisplayRow (
                        displayFixture,
                        scaffoldState,
                        nameNormalizer
                    )

                    currentDate = displayFixture.fixture.date
                }
            } else {
                // TODO: Text should change depending on if we already searched
                Text("Keine")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            VerticalScrollbar(
                adapter = scrollBarAdapter,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(start = 2.dp, end = 1.dp)
            )
        }
    }
}


@Composable
private fun fixtureDisplayRow(
    displayFixture: DisplayFixture,
    scaffoldState: ScaffoldState,
    nameNormalizer: NameNormalizer
) {
    Row(
        modifier = Modifier
            .padding(bottom = 5.dp, top = 5.dp)
            .fillMaxSize()
    ) {

        deleteFixtureButton(onClick = {
            displayFixtures.remove(displayFixture)

            backgroundCoroutineScope.launch {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = "Removed game " +
                            "${nameNormalizer.normalizeTeamName(displayFixture.fixture.homeTeam)} vs. " +
                            "${nameNormalizer.normalizeTeamName(displayFixture.fixture.guestTeam)} " +
                            "from display list!",
                    duration = SnackbarDuration.Short
                )
            }
        })

        Row (
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .align(Alignment.CenterVertically)
        ) {
            fixtureInfoLine(displayFixture.fixture, nameNormalizer)
        }

        markFixtureForDeleteSwitch(displayFixture)
    }
}

fun removeFixturesOfLeagueFromUi(league: League) {
    displayFixtures.removeIf { it.fixture.league == league }
}


fun addFixturesOfLeagueToUi(league: League, leaguesDatabase: LeaguesDatabase) {
    try {
        val allDisplayFixtures =
            leaguesDatabase.getFixturesFromDateRange(fromDate.value, toDate.value).map { DisplayFixture(it) }

        allDisplayFixtures.forEachIndexed { index, displayFixture ->
            if (!displayFixtures.contains(displayFixture) && displayFixture.fixture.league == league) {
                displayFixtures.add(index, displayFixture)
            }
        }
    } catch (e: Exception) {
        logger.warn("Error in addFixturesOfLeagueToUi: ${e.message}")
    }
}


fun deleteAllMarkedDisplayFixtures() {
    displayFixtures.removeIf { it.isMarkedForDeleting.value }
}

fun setAllMarkedForDeletionSwitches(isMarkedForDeleting: Boolean) {
    displayFixtures.forEach {
        it.isMarkedForDeleting.value = isMarkedForDeleting
    }
}

@Composable
private fun markFixtureForDeleteSwitch(displayFixture: DisplayFixture) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp)
    ) {
        val isFixtureMarkedForDelete = mutableStateOf(displayFixture.isMarkedForDeleting.value)
        Row (
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column (
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = if (isFixtureMarkedForDelete.value) {
                        "Ja"
                    } else {
                        "Nein"
                    },
                    modifier = Modifier
                        .align(Alignment.Start)
                )
            }

            Column (
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxWidth()
            ) {
                Switch(
                    checked = isFixtureMarkedForDelete.value,
                    onCheckedChange = {
                        isFixtureMarkedForDelete.value = it
                        displayFixture.isMarkedForDeleting.value = it
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                )
            }
        }
    }
}

data class DisplayFixture(
    val fixture: Fixture,
    var isMarkedForDeleting: MutableState<Boolean> = mutableStateOf(false)
)