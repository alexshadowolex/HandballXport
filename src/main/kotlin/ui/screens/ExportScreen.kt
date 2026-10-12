package alex.handball.ui.screens

import alex.handball.Model
import alex.handball.addQuotationMarks
import alex.handball.backgroundCoroutineScope
import alex.handball.config.AppConfig
import alex.handball.customToString
import alex.handball.getEndDateForPlannerScreen
import alex.handball.handler.ExcelColumnIndices
import alex.handball.handler.ExcelExportHandler
import alex.handball.model.Fixture
import alex.handball.model.League
import alex.handball.model.LeaguesDatabase
import alex.handball.normalizeTeamName
import alex.handball.toJavaLocalDate
import alex.handball.ui.dateRangeShowGamesSegment
import alex.handball.ui.deleteFixtureButton
import alex.handball.ui.fixtureInfoHeader
import alex.handball.ui.fixtureInfoLine
import alex.handball.ui.screenHeaderRow
import alex.handball.ui.sectionDivider
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

private val displayedFixtures = mutableStateListOf<Fixture>()
private lateinit var fromDate: MutableState<String>
private lateinit var toDate: MutableState<String>
val isExcelExportGenerated = mutableStateOf(false)

@Composable
fun exportScreen(model: Model) {
    val scaffoldState = rememberScaffoldState()
    fromDate = remember { mutableStateOf(
        Clock.System.todayIn(TimeZone.currentSystemDefault()).toJavaLocalDate().customToString()
    )}
    toDate = remember { mutableStateOf(getEndDateForPlannerScreen().customToString()) }

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
                    onShowClick = { parsedFromDate, parsedToDate ->
                        displayedFixtures.clear()
                        displayedFixtures.addAll(model.leaguesDatabase.getFixturesFromDateRange(
                            parsedFromDate,
                            parsedToDate
                        ))
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
                                backgroundCoroutineScope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar(
                                        message = "Reset all unlocked games",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            enabled = displayedFixtures.isNotEmpty()
                        ) {
                            // TODO delete all selected games
                            Text("Reset all unlocked Games")
                        }
                    }

                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp)
                    ) {
                        Row (
                            modifier = Modifier
                                .fillMaxWidth(0.7F)
                                .align(Alignment.End)
                        ) {
                            Button(
                                onClick = {
                                    //setAllLockedSwitchValues(isLocked = true)
                                },
                                modifier = Modifier
                                    .fillMaxWidth(0.4F),
                                enabled = displayedFixtures.isNotEmpty()
                            ) {
                                Text("Lock All")
                            }

                            Button(
                                onClick = {
                                    //setAllLockedSwitchValues(isLocked = false)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 2.dp),
                                enabled = displayedFixtures.isNotEmpty()
                            ) {
                                Text("Unlock All")
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
                                    displayedFixtures,
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
                                    )
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
                            enabled = displayedFixtures.isNotEmpty()
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

                gamePlanList(scaffoldState)
            }
        }
    }
}


@Composable
private fun gamePlanList(
    scaffoldState: ScaffoldState
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
            if (displayedFixtures.isNotEmpty()) {
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
                        text = "Delete?",
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
                displayedFixtures.forEach { fixture ->

                    if (currentDate != null) {
                        Divider(
                            color = MaterialTheme.colors.secondary,
                            thickness = if (currentDate != fixture.date) {
                                2.dp
                            } else {
                                1.dp
                            }
                        )
                    }

                    fixtureDisplayRow (
                        fixture,
                        scaffoldState
                    )

                    currentDate = fixture.date
                }
            } else {
                // TODO: Text should change depending on if we already searched
                Text("None")
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
    fixture: Fixture,
    scaffoldState: ScaffoldState
) {
    Row(
        modifier = Modifier
            .padding(bottom = 5.dp, top = 5.dp)
            .fillMaxSize()
    ) {

        deleteFixtureButton(onClick = {
            displayedFixtures.remove(fixture)

            backgroundCoroutineScope.launch {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = "Removed game " +
                            "${fixture.homeTeam.normalizeTeamName()} vs. " +
                            "${fixture.guestTeam.normalizeTeamName()} " +
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
            fixtureInfoLine(fixture)
        }

        lockFixtureSuggestionChangeSwitch()
    }
}

private fun triggerRecomposition() {
    // TODO: this is only a 1b solution to trigger recomposition
    try {
        /*val lastElement = fixturePlanners.removeLast()
        fixturePlanners.add(lastElement)*/
        displayedFixtures[0] = displayedFixtures[0].copy()
    } catch (_: Exception) {

    }
}

fun removeFixturesOfLeagueFromUi(league: League) {
    displayedFixtures.removeIf { it.league == league }
}


fun addFixturesOfLeagueToUi(league: League, leaguesDatabase: LeaguesDatabase) {
    val parsedFromDate = fromDate.value.toJavaLocalDate()
    val parsedToDate = toDate.value.toJavaLocalDate()

    if(parsedFromDate == null || parsedToDate == null) {
        return
    }
    val allFixtures = leaguesDatabase.getFixturesFromDateRange(parsedFromDate, parsedToDate)

    allFixtures.forEachIndexed { index, fixture ->
        if(!displayedFixtures.contains(fixture) && fixture.league == league) {
            displayedFixtures.add(index, fixture)
        }
    }
}


@Composable
private fun lockFixtureSuggestionChangeSwitch() {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp)
    ) {
        val isFixtureLocked = mutableStateOf(false)
        Row (
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column (
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = if (isFixtureLocked.value) {
                        "Locked"
                    } else {
                        "Not Locked"
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
                    checked = isFixtureLocked.value,
                    onCheckedChange = {
                        isFixtureLocked.value = it
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                )
            }
        }
    }
}