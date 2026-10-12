package alex.handball.ui.screens

import alex.handball.backgroundCoroutineScope
import alex.handball.logger
import alex.handball.model.League
import alex.handball.model.LeaguesDatabase
import alex.handball.ui.expandableCard
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.Desktop
import java.net.URI

private var leaguesStateList = mutableStateListOf<League>()
private var isCurrentlyGettingAndSortingData: MutableState<Boolean> = mutableStateOf(false)

@Composable
fun leaguesListScreen(
    leaguesDatabase: LeaguesDatabase
) {
    var newLeagueName by remember { mutableStateOf("") }
    var newLeagueDistinguishingNameSubstring by remember { mutableStateOf("") }
    var newLeagueLink by remember { mutableStateOf("") }
    val labelPlanLeague = "Plan"
    val labelNoPlanLeague = "Not Plan"

    var newLeaguePlanLeague by remember { mutableStateOf(labelPlanLeague) }

    val scaffoldState = rememberScaffoldState()
    leaguesStateList = leaguesDatabase.leagues.toMutableStateList()

    MaterialTheme {
        Scaffold (
            scaffoldState = scaffoldState
        ) {
            Column(
                modifier = Modifier
                    .padding(all = 15.dp)
            ) {
                screenHeaderRow(headline = "League List Editor")

                Spacer(
                    modifier = Modifier
                        .height(20.dp)
                )

                Row {
                    Column {
                        Text(
                            text = "Add new League",
                        )
                    }
                }


                Row(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                ) {
                    Column {
                        Row {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(0.3F)
                            ) {
                                TextField(
                                    label = {
                                        Text(
                                            color = MaterialTheme.colors.onPrimary,
                                            text = "League Name (short)"
                                        )
                                    },
                                    value = newLeagueName,
                                    onValueChange = { value ->
                                        newLeagueName = value
                                    },
                                    singleLine = true
                                )
                            }

                            Spacer(
                                modifier = Modifier
                                    .width(5.dp)
                            )

                            Column {
                                Row {
                                    RadioButton(
                                        selected = newLeaguePlanLeague == labelPlanLeague,
                                        onClick = {
                                            newLeaguePlanLeague = labelPlanLeague
                                        }
                                    )
                                    Text(
                                        text = labelPlanLeague,
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    )
                                }
                            }

                            Column {
                                Row {
                                    RadioButton(
                                        selected = newLeaguePlanLeague == labelNoPlanLeague,
                                        onClick = {
                                            newLeaguePlanLeague = labelNoPlanLeague
                                        }
                                    )
                                    Text(
                                        text = labelNoPlanLeague,
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 20.dp)
                            ) {
                                TextField(
                                    label = {
                                        Text(
                                            color = MaterialTheme.colors.onPrimary,
                                            text = "Distinguishing League Name Substring (e.g. \"St. 1\")"
                                        )
                                    },
                                    value = newLeagueDistinguishingNameSubstring,
                                    onValueChange = { value ->
                                        newLeagueDistinguishingNameSubstring = value
                                    },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }
                        }

                        Row (
                            modifier = Modifier
                                .padding(top = 5.dp)
                        ) {

                            Column (
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                            ) {
                                TextField(
                                    label = {
                                        Text(
                                            color = MaterialTheme.colors.onPrimary,
                                            text = "League Link handball.net"
                                        )
                                    },
                                    value = newLeagueLink,
                                    onValueChange = { value ->
                                        newLeagueLink = value
                                    },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }

                            Spacer(
                                modifier = Modifier
                                    .width(10.dp)
                            )

                            Column (
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Button(
                                    onClick = {
                                        // TODO: Add validation
                                        backgroundCoroutineScope.launch {
                                            val tmpLeagueName = newLeagueName
                                            val tmpLeagueDistinguishingNameSubstring = newLeagueDistinguishingNameSubstring.ifBlank { null }
                                            val tmpLeaguePlanLeague = newLeaguePlanLeague == labelPlanLeague
                                            val tmpLeagueLink = newLeagueLink

                                            val newLeague = League(
                                                leagueLink = tmpLeagueLink,
                                                leagueName = tmpLeagueName,
                                                distinguishingLeagueNameSubstring = tmpLeagueDistinguishingNameSubstring
                                            )
                                            leaguesDatabase.addLeagueToDatabase(newLeague)

                                            newLeagueName = ""
                                            newLeagueLink = ""
                                            newLeagueDistinguishingNameSubstring = ""
                                            leaguesStateList.add(newLeague)

                                            backgroundCoroutineScope.launch {
                                                scaffoldState.snackbarHostState.showSnackbar(
                                                    message = "League $tmpLeagueName was added to the list!",
                                                    duration = SnackbarDuration.Short
                                                )
                                            }

                                            backgroundCoroutineScope.launch {
                                                isCurrentlyGettingAndSortingData.value = true
                                                leaguesDatabase.initializeLoadingAllLeagueAndFixtureData()
                                                // TODO
                                                //addFixturesOfLeagueToUi(newLeague, leaguesDatabase)
                                                isCurrentlyGettingAndSortingData.value = false
                                            }
                                        }
                                    },
                                    enabled = !isCurrentlyGettingAndSortingData.value,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Text("Add To List")
                                }
                            }
                        }
                    }
                }


                sectionDivider(thickness = 3.dp)

                Row {
                    leagueList(
                        leaguesDatabase,
                        scaffoldState
                    )
                }
            }
        }
    }
}


@Composable
private fun leagueList(
    leaguesDatabase: LeaguesDatabase,
    scaffoldState: ScaffoldState
) {
    val scrollState = rememberScrollState(0)
    val scrollBarAdapter = rememberScrollbarAdapter(scrollState)
    Column (
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxWidth(0.98F)
    ) {
        if(leaguesStateList.isNotEmpty()) {
            leaguesStateList.forEach { league ->
                leagueRow(
                    league,
                    leaguesDatabase,
                    scaffoldState
                )

                sectionDivider(thickness = 1.dp)
            }
        } else {
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

@Composable
private fun leagueRow(
    league: League,
    leaguesDatabase: LeaguesDatabase,
    scaffoldState: ScaffoldState
) {
    Row(
        modifier = Modifier.padding(bottom = 5.dp, top = 5.dp)
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth(0.7F)
        ) {
            expandableCard(
                title = league.leagueName,
                content = {
                    Column (
                        modifier = Modifier
                            .padding(8.dp)
                    ) {

                        sectionDivider(thickness = 5.dp)

                        Row {
                            Text(
                                text = "Link: "
                            )
                            Text(
                                text = league.leagueLink,
                                modifier = Modifier
                                    .clickable {
                                        backgroundCoroutineScope.launch {
                                            withContext(Dispatchers.IO) {
                                                try {
                                                    Desktop.getDesktop()
                                                        .browse(URI.create(league.leagueLink))
                                                } catch (e: Throwable) {
                                                    logger.warn("Caught exception while trying to open link ${league.leagueLink}: ", e)
                                                }
                                            }
                                        }
                                    }
                                    .pointerHoverIcon(PointerIcon.Hand)
                            )
                        }

                        sectionDivider(thickness = 5.dp)

                        Row {
                            Text(
                                text = "Plan League: false"
                            )
                        }

                        sectionDivider(thickness = 5.dp)

                        Row {
                            Text(
                                text = "Distinguishing League Name Substring: ${league.distinguishingLeagueNameSubstring ?: "<none>"}"
                            )
                        }
                    }
                }
            )
        }
        Button(
            onClick = {
                backgroundCoroutineScope.launch {
                    leaguesDatabase.removeLeagueFromDatabase(league)
                    // TODO
                    //removeFixturesOfLeagueFromUi(league)
                    leaguesStateList.remove(league)

                    backgroundCoroutineScope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = "League ${league.leagueName} was removed from the list!",
                            duration = SnackbarDuration.Short
                        )
                    }

                    backgroundCoroutineScope.launch {
                        isCurrentlyGettingAndSortingData.value = true
                        leaguesDatabase.initializeLoadingAllLeagueAndFixtureData()
                        isCurrentlyGettingAndSortingData.value = false
                    }
                }
            },
            enabled = !isCurrentlyGettingAndSortingData.value,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically)
        ) {
            Text("Remove")
        }
    }
}