package alex.handball.ui.screens

import alex.handball.backgroundCoroutineScope
import alex.handball.model.NameNormalizer
import alex.handball.model.SpecialGymName
import alex.handball.model.SpecialTeamName
import alex.handball.toJavaLocalDate
import alex.handball.ui.columnDivider
import alex.handball.ui.expandableCard
import alex.handball.ui.screenHeaderRow
import alex.handball.ui.sectionDivider

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

var teamAbbreviations = mutableStateListOf<String>()
var gymAbbreviations = mutableStateListOf<String>()
var specialTeamNames = mutableStateListOf<SpecialTeamName>()
var specialGymNames = mutableStateListOf<SpecialGymName>()

@Composable
fun nameNormalizerScreen(
    nameNormalizer: NameNormalizer
) {
    var newTeamAbbreviation by remember { mutableStateOf("") }

    val scaffoldState = rememberScaffoldState()
    teamAbbreviations = nameNormalizer.teamAbbreviations.toMutableStateList()
    gymAbbreviations = nameNormalizer.gymAbbreviations.toMutableStateList()
    specialTeamNames = nameNormalizer.specialTeamNames.toMutableStateList()
    specialGymNames = nameNormalizer.specialGymNames.toMutableStateList()

    val scrollState = rememberScrollState(0)

    MaterialTheme {
        Scaffold(
            scaffoldState = scaffoldState
        ) {
            Column(
                modifier = Modifier
                    .padding(all = 15.dp)
                    .fillMaxSize()
            ) {
                Column {
                    screenHeaderRow(headline = "Namennormalisierer")

                    Spacer(
                        modifier = Modifier
                            .height(20.dp)
                    )


                    Row {
                        Text(
                            text = "Hier können Sonderfälle definiert werden, die bei der Normalisierung der Namen von Hallen und Teams berücksichtigt werden sollen.\n" +
                                    "Bei handball.net sind alle Team- und Hallennamen komplett groß geschrieben, was mit dem Namennormalisierer für die Anzeige und den " +
                                    "Export angepasst wird.\n" +
                                    "Dabei wird bei jedem Wort und Teilwort einfach der erste Buchstabe groß geschrieben und alle weiteren werden klein geschrieben. " +
                                    "Neue Worte werden über Leerzeichen, Bindestriche und Klammern identifiziert.\n" +
                                    "Manche Worte sollen aber komplett groß geschrieben werden (z.B. \"HV\"), manche Worte teils groß und teils klein (z.B. \"VfB\") und " +
                                    "manche Worte sind komplett falsch geschrieben. Diese Sonderfälle können hier definiert werden.",
                            style = MaterialTheme.typography.subtitle1
                        )
                    }

                    sectionDivider(thickness = 5.dp)

                    Row(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .verticalScroll(scrollState)
                    ) {
                        Column (
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Row {
                                Column {
                                    Text(
                                        text = "Team-Abkürzungen",
                                        style = MaterialTheme.typography.h6
                                    )
                                    Text(
                                        text = "Hier werden die Abkürzungen von Team-Namen erfasst, die komplett groß geschrieben werden sollen (z.B. \"HV\", \"LHV\").",
                                        style = MaterialTheme.typography.subtitle1,
                                        modifier = Modifier.padding(top = 5.dp)
                                    )
                                }
                            }

                            Row (
                                modifier = Modifier
                                    .padding(top = 10.dp)
                                    .fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth(0.75f)
                                        .padding(end = 5.dp)
                                ) {
                                    TextField(
                                        value = newTeamAbbreviation,
                                        onValueChange = { newTeamAbbreviation = it },
                                        modifier = Modifier
                                            .fillMaxWidth(0.5F)
                                            .align(Alignment.End)
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = {

                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        Text("Füge Team-Abkürzung hinzu")
                                    }
                                }
                            }

                            sectionDivider(2.dp)

                            Row {
                                Column {
                                    expandableCard(
                                        title = "Team-Abkürzungen",
                                        content = {
                                            for(i in 0..<teamAbbreviations.size step 2) {
                                                val teamAbbreviation = teamAbbreviations[i]
                                                val secondTeamAbbreviation = if(i + 1 < teamAbbreviations.size - 1) {
                                                    teamAbbreviations[i + 1]
                                                } else {
                                                    null
                                                }
                                                Row (
                                                    modifier = Modifier
                                                        .padding(all = 5.dp)
                                                        .height(20.dp)
                                                ) {
                                                    Column {
                                                        Text(
                                                            text = teamAbbreviation
                                                        )
                                                    }

                                                    Column {
                                                        Text("Test")
                                                    }

                                                    if(secondTeamAbbreviation != null) {
                                                        columnDivider()

                                                        Column {
                                                            Text(
                                                                text = secondTeamAbbreviation
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}