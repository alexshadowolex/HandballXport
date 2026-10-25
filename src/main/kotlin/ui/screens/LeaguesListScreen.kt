package alex.handball.ui.screens

import alex.handball.backgroundCoroutineScope
import alex.handball.logger
import alex.handball.model.League
import alex.handball.model.LeaguesDatabase
import alex.handball.ui.HelpIcon
import alex.handball.ui.LeagueLinkHelpDialog
import alex.handball.ui.expandableCard
import alex.handball.ui.screenHeaderRow
import alex.handball.ui.sectionDivider
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val labelNuLiga = "NuLiga"
    val labelHandballNet = "Handball.net"

    var newLeagueNuLigaLeague by remember { mutableStateOf(labelNuLiga) }

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
                screenHeaderRow(headline = "Ligen Editor")

                Spacer(
                    modifier = Modifier
                        .height(20.dp)
                )

                Row(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Hier kannst du weitere Ligen hinzufügen oder entfernen. Beim Programmstart und beim Hinzufügen/Entfernen werden die Spiele aller eingetragenen Ligen geladen."
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .fillMaxWidth()
                ) {
                    Column {
                        Row {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(0.33F)
                            ) {
                                Row {
                                    TextField(
                                        label = {
                                            Text(
                                                color = MaterialTheme.colors.onPrimary,
                                                text = "Liga Name (kurz)"
                                            )
                                        },
                                        value = newLeagueName,
                                        onValueChange = { value ->
                                            newLeagueName = value
                                        },
                                        singleLine = true,
                                        placeholder = {
                                            Text("z.B. \"VLMO\"")
                                        },
                                        colors = TextFieldDefaults.textFieldColors(
                                            cursorColor = MaterialTheme.colors.onPrimary
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth(0.9f)
                                    )

                                    HelpIcon(
                                        tooltipText = "Dieser Name ist die Bezeichnung, die in der List und im Excel-Export zu sehen ist. Das könnte sein \"VLMO\", \"ROLM\" oder \"RLmJB\". Es empfielt sich, jeden Namen nur einmal zu verwenden - aber es ist auch möglich, einen Namen mehrfach zu vergeben.",
                                        modifier = Modifier.padding(start = 5.dp)
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(0.5f)
                                    .padding(start = 10.dp)
                            ) {
                                Row {
                                    TextField(
                                        label = {
                                            Text(
                                                color = MaterialTheme.colors.onPrimary,
                                                text = "Unterscheidungstext im Liganame"
                                            )
                                        },
                                        placeholder = {
                                            Text("z.B. \"St. 1\"")
                                        },
                                        value = newLeagueDistinguishingNameSubstring,
                                        onValueChange = { value ->
                                            newLeagueDistinguishingNameSubstring = value
                                        },
                                        singleLine = true,
                                        colors = TextFieldDefaults.textFieldColors(
                                            cursorColor = MaterialTheme.colors.onPrimary
                                        ),
                                        enabled = newLeagueNuLigaLeague == labelHandballNet,
                                        modifier = Modifier
                                            .fillMaxWidth(0.9f)
                                    )

                                    HelpIcon(
                                        tooltipText = "Wird nur bei handball.net-Quelle benötigt. Die Liste von allen Spielen einer Liga kann Spiele von z.B. unterschiedlichen Staffeln beinhalten. So werden z.B. in der Verbandsliga von Sachsen unter einem Link die Spiele der Staffeln Ost und West aufgeführt. Analoges kann auch bei anderen Ligen passieren, z.B. RLM St. 1 und St. 2; Pokalligen sind oft in den ROLM-Ligen mit erfasst. Sobald ein Link 2 Ligen beinhaltet, muss zur korrekten Unterscheidung ein Teil-Text erfasst werden, der in jedem Spiel im Ligenname vorhanden ist. Am Beispiel Verbandsliga würde man dann eine Liga VLMW erfassen mit dem Unterscheidungstext \"West\" und eine Liga VLMO mit demselben Link, aber dem Unterscheidungstext \"Ost\".",
                                        modifier = Modifier.padding(start = 5.dp)
                                    )
                                }
                            }

                            Column (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 20.dp)
                            ) {
                                Row (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Text("Quelle")

                                    HelpIcon(
                                        tooltipText = "Das Programm erlaubt das Laden von Spielen aus NuLiga und auch von handball.net. Wird der Button auf die falsche Quelle gestellt, dann werden von der Liga die Spiele nicht angezeigt. Das kann durch Entfernen und Hinzufügen der Liga wieder korrigiert werden.",
                                        modifier = Modifier.padding(start = 5.dp)
                                    )
                                }
                                Row (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Row {
                                        RadioButton(
                                            selected = newLeagueNuLigaLeague == labelNuLiga,
                                            onClick = {
                                                newLeagueNuLigaLeague = labelNuLiga
                                             }
                                        )
                                        Text(
                                            text = labelNuLiga,
                                            modifier = Modifier.align(Alignment.CenterVertically)
                                        )
                                    }

                                    Row {
                                        RadioButton(
                                            selected = newLeagueNuLigaLeague == labelHandballNet,
                                            onClick = {
                                                newLeagueNuLigaLeague = labelHandballNet
                                            }
                                        )
                                        Text(
                                            text = labelHandballNet,
                                            modifier = Modifier.align(Alignment.CenterVertically)
                                        )
                                    }
                                }
                            }
                        }

                        Row (
                            modifier = Modifier
                                .padding(top = 5.dp)
                        ) {

                            Column (
                                modifier = Modifier
                                    .fillMaxWidth(0.665f)
                            ) {
                                Row {
                                    TextField(
                                        label = {
                                            Text(
                                                color = MaterialTheme.colors.onPrimary,
                                                text = "Liga Link handball.net/NuLiga"
                                            )
                                        },
                                        placeholder = {
                                            Text("z.B. https://www.handball.net/spielbetrieb/... oder https://hvs-handball.liga.nu/cgi-bin/...")
                                        },
                                        value = newLeagueLink,
                                        onValueChange = { value ->
                                            newLeagueLink = value
                                        },
                                        singleLine = true,
                                        modifier = Modifier
                                            .fillMaxWidth(0.95f),
                                        colors = TextFieldDefaults.textFieldColors(
                                            cursorColor = MaterialTheme.colors.onPrimary
                                        )
                                    )

                                    HelpIcon(
                                        tooltipText = "Der Link muss zu einer bestimmten Seite zeigen, über die alle Spiele der Liga erreichbar sind. Es funktioniert pro System nur je ein Direktlink. Zur genauen Erklärung, wie man zu dem Link kommt, unten auf den Text \"Wie finde ich den richtigen Link?\" klicken.",
                                        modifier = Modifier.padding(start = 5.dp)
                                    )
                                }
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
                                            val tmpIsNuLigaLeague = newLeagueNuLigaLeague == labelNuLiga
                                            val tmpLeagueLink = newLeagueLink

                                            val newLeague = League(
                                                leagueLink = tmpLeagueLink,
                                                leagueName = tmpLeagueName,
                                                isNuLigaLeague = tmpIsNuLigaLeague,
                                                distinguishingLeagueNameSubstring = tmpLeagueDistinguishingNameSubstring
                                            )
                                            leaguesDatabase.addLeagueToDatabase(newLeague)

                                            newLeagueName = ""
                                            newLeagueLink = ""
                                            newLeagueDistinguishingNameSubstring = ""
                                            leaguesStateList.add(newLeague)

                                            backgroundCoroutineScope.launch {
                                                scaffoldState.snackbarHostState.showSnackbar(
                                                    message = "Liga $tmpLeagueName wurde zur Liste hinzugefügt!",
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
                                    Text("Zur Liste hinzufügen")
                                }
                            }
                        }

                        Row (
                            modifier = Modifier
                                .padding(top = 5.dp)
                        ) {
                            var showLinkHelp by remember { mutableStateOf(false) }

                            TextButton(
                                onClick = { showLinkHelp = true },
                                contentPadding = PaddingValues(horizontal = 0.dp, vertical = 4.dp)
                            ) {
                                Text("Wie finde ich den richtigen Link?")
                            }

                            if (showLinkHelp) {
                                LeagueLinkHelpDialog(
                                    onDismiss = { showLinkHelp = false },
                                    isNuLiga = newLeagueNuLigaLeague == labelNuLiga
                                )
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

                if(!league.wasLeagueLoadedSuccessfully) {
                    Row(
                        modifier = Modifier
                            .padding(start = 24.dp)
                    ) {
                        Text(
                            text = "Liga konnte nicht geladen werden!",
                            color = Color.Red
                        )
                    }
                }

                sectionDivider(thickness = 1.dp)
            }
        } else {
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
                                text = "NuLiga Liga: ${league.isNuLigaLeague}"
                            )
                        }

                        sectionDivider(thickness = 5.dp)

                        Row {
                            Text(
                                text = "Unterscheidungstext im Liganame: ${league.distinguishingLeagueNameSubstring ?: "<keiner>"}"
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
                            message = "Liga ${league.leagueName} wurde von der Liste enfernt!",
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
            Text("Entfernen")
        }
    }
}