package alex.handball.ui.screens

import alex.handball.backgroundCoroutineScope
import alex.handball.logger
import alex.handball.model.NameNormalizer
import alex.handball.model.SpecialGymName
import alex.handball.model.SpecialTeamName
import alex.handball.toJavaLocalDate
import alex.handball.ui.columnDivider
import alex.handball.ui.expandableCard
import alex.handball.ui.screenHeaderRow
import alex.handball.ui.sectionDivider

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
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
                                            .align(Alignment.End),
                                        label = {
                                            Text("Team-Abkürzung")
                                        },
                                        placeholder = {
                                            Text ("z.B. \"HV\" oder \"LHV\"")
                                        }
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = {
                                            if(newTeamAbbreviation.isEmpty()) {
                                                logger.info("New team abbreviation was empty, aborting adding-process")
                                                backgroundCoroutineScope.launch {
                                                    scaffoldState.snackbarHostState.showSnackbar("Bitte gib einen Wert im Textfeld ein!")
                                                }
                                                return@Button
                                            }
                                            teamAbbreviations.add(newTeamAbbreviation)
                                            nameNormalizer.addTeamAbbreviation(newTeamAbbreviation)
                                            newTeamAbbreviation = ""
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
                                            ExpandableTagList(
                                                items = teamAbbreviations,

                                                onRemove = { index ->
                                                    val removedElement = teamAbbreviations.removeAt(index)
                                                    nameNormalizer.removeTeamAbbreviation(removedElement)
                                                },

                                                itemContent = { abbreviation ->
                                                    Text(
                                                        text = abbreviation,
                                                        style = MaterialTheme.typography.body2
                                                    )
                                                }
                                            )
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> ExpandableTagList(
    items: List<T>,
    onRemove: (Int) -> Unit,
    itemContent: @Composable (T) -> Unit,
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 8.dp,
    verticalSpacing: Dp = 8.dp
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
    ) {
        items.forEachIndexed { index, item ->

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(
                        MaterialTheme.colors.surface
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colors.onSurface.copy(
                            alpha = 0.15f
                        ),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(
                        start = 12.dp,
                        end = 4.dp,
                        top = 4.dp,
                        bottom = 4.dp
                    ),

                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier.padding(
                        end = 4.dp
                    )
                ) {
                    itemContent(item)
                }

                IconButton(
                    onClick = {
                        onRemove(index)
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Eintrag entfernen",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}