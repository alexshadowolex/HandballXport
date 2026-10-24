package alex.handball.ui.screens

import alex.handball.backgroundCoroutineScope
import alex.handball.logger
import alex.handball.model.NameNormalizer
import alex.handball.model.SpecialGymName
import alex.handball.model.SpecialTeamName
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
    var newSpecialTeamNameBefore by remember { mutableStateOf("") }
    var newSpecialTeamNameAfter by remember { mutableStateOf("") }
    var newGymAbbreviation by remember { mutableStateOf("") }
    var newSpecialGymNameBefore by remember { mutableStateOf("") }
    var newSpecialGymNameAfter by remember { mutableStateOf("") }

    val scaffoldState = rememberScaffoldState()
    teamAbbreviations = nameNormalizer.teamAbbreviations.toMutableStateList()
    gymAbbreviations = nameNormalizer.gymAbbreviations.toMutableStateList()
    specialTeamNames = nameNormalizer.specialTeamNames.toMutableStateList()
    specialGymNames = nameNormalizer.specialGymNames.toMutableStateList()

    val scrollState = rememberScrollState(0)
    val scrollBarAdapter = rememberScrollbarAdapter(scrollState)

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
                    ) {
                        Column (
                            modifier = Modifier
                                .fillMaxWidth(0.98f)
                                .verticalScroll(scrollState)
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

                            Row(
                                modifier = Modifier
                                    .padding(top = 10.dp)
                                    .fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth(0.8f)
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
                                            Text("z.B. \"HV\" oder \"LHV\"")
                                        }
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = {
                                            if (newTeamAbbreviation.isEmpty()) {
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

                            sectionDivider(5.dp)

                            Row {
                                Column {
                                    Text(
                                        text = "Besondere Team-Namen",
                                        style = MaterialTheme.typography.h6
                                    )
                                    Text(
                                        text = "Hier werden die Abkürzungen und Team-Namen erfasst, die nicht komplett groß geschrieben werden sollen bzw. auch die, die komplett falsch geschrieben sind. z.B. die Abkürzung \"VfB\" hat einen kleinen Buchstaben. Damit die richtig angezeigt wird, muss im ersten Feld die Variante, wie sie auf handball.net zu finden ist, erfasst werden - also \"VFB\" - und im zweiten Feld die korrekte Variante für die Anzeige - also \"VfB\". Dasselbe gilt auch für falsch geschriebene Team-Namen, z.B. \"GROSSENHAIN\" -> \"Großenhain\".",
                                        style = MaterialTheme.typography.subtitle1,
                                        modifier = Modifier.padding(top = 5.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .padding(top = 10.dp)
                                    .fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth(0.4f)
                                        .padding(end = 5.dp)
                                ) {
                                    TextField(
                                        value = newSpecialTeamNameBefore,
                                        onValueChange = { newSpecialTeamNameBefore = it },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.End),
                                        label = {
                                            Text("Besonderer Team-Name in handball.net")
                                        },
                                        placeholder = {
                                            Text("z.B. \"VFB\" oder \"GROSSENHAIN\"")
                                        }
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth(0.66f)
                                        .padding(end = 5.dp)
                                ) {
                                    TextField(
                                        value = newSpecialTeamNameAfter,
                                        onValueChange = { newSpecialTeamNameAfter = it },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.End),
                                        label = {
                                            Text("Besonderer Team-Name korrekt geschrieben")
                                        },
                                        placeholder = {
                                            Text("z.B. \"VfB\" oder \"Großenhain\"")
                                        }
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = {
                                            if (newSpecialTeamNameBefore.isEmpty()) {
                                                logger.info("New special team name was empty, aborting adding-process")
                                                backgroundCoroutineScope.launch {
                                                    scaffoldState.snackbarHostState.showSnackbar("Bitte gib einen Wert mindestens im ersten Textfeld ein!")
                                                }
                                                return@Button
                                            }
                                            val newSpecialTeamName = SpecialTeamName(newSpecialTeamNameBefore, newSpecialTeamNameAfter)
                                            specialTeamNames.add(newSpecialTeamName)
                                            nameNormalizer.addSpecialTeamName(newSpecialTeamName)
                                            newSpecialTeamNameBefore = ""
                                            newSpecialTeamNameAfter = ""
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        Text("Füge besonderen Team-Namen hinzu")
                                    }
                                }
                            }

                            sectionDivider(2.dp)

                            Row {
                                Column {
                                    expandableCard(
                                        title = "Besondere Team-Namen",
                                        content = {
                                            ExpandableTagList(
                                                items = specialTeamNames,

                                                onRemove = { index ->
                                                    val removedElement = specialTeamNames.removeAt(index)
                                                    nameNormalizer.removeSpecialTeamName(removedElement)
                                                },

                                                itemContent = { specialTeamName ->

                                                    Column {

                                                        Text(
                                                            text = specialTeamName.key,
                                                            style = MaterialTheme.typography.body2
                                                        )

                                                        Text(
                                                            text = specialTeamName.value,
                                                            style = MaterialTheme.typography.caption,
                                                            color = MaterialTheme.colors.onSurface.copy(
                                                                alpha = 0.6f
                                                            )
                                                        )
                                                    }
                                                }
                                            )
                                        }
                                    )
                                }
                            }

                            sectionDivider(5.dp)

                            Row {
                                Column {
                                    Text(
                                        text = "Hallen-Abkürzungen",
                                        style = MaterialTheme.typography.h6
                                    )
                                    Text(
                                        text = "Analog zu den Team-Abkürzungen werden hier die Abkürzungen von Hallen erfasst, die komplett groß geschrieben werden sollen (z.B. \"SH\", \"BSZ\").",
                                        style = MaterialTheme.typography.subtitle1,
                                        modifier = Modifier.padding(top = 5.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .padding(top = 10.dp)
                                    .fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth(0.8f)
                                        .padding(end = 5.dp)
                                ) {
                                    TextField(
                                        value = newGymAbbreviation,
                                        onValueChange = { newGymAbbreviation = it },
                                        modifier = Modifier
                                            .fillMaxWidth(0.5F)
                                            .align(Alignment.End),
                                        label = {
                                            Text("Hallen-Abkürzung")
                                        },
                                        placeholder = {
                                            Text("z.B. \"SH\" oder \"BSZ\"")
                                        }
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = {
                                            if (newGymAbbreviation.isEmpty()) {
                                                logger.info("New gym abbreviation was empty, aborting adding-process")
                                                backgroundCoroutineScope.launch {
                                                    scaffoldState.snackbarHostState.showSnackbar("Bitte gib einen Wert im Textfeld ein!")
                                                }
                                                return@Button
                                            }
                                            gymAbbreviations.add(newGymAbbreviation)
                                            nameNormalizer.addGymAbbreviation(newGymAbbreviation)
                                            newGymAbbreviation = ""
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        Text("Füge Hallen-Abkürzung hinzu")
                                    }
                                }
                            }

                            sectionDivider(2.dp)

                            Row {
                                Column {
                                    expandableCard(
                                        title = "Hallen-Abkürzungen",
                                        content = {
                                            ExpandableTagList(
                                                items = gymAbbreviations,

                                                onRemove = { index ->
                                                    val removedElement = gymAbbreviations.removeAt(index)
                                                    nameNormalizer.removeGymAbbreviation(removedElement)
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

                            sectionDivider(5.dp)

                            Row {
                                Column {
                                    Text(
                                        text = "Besondere Hallen-Namen",
                                        style = MaterialTheme.typography.h6
                                    )
                                    Text(
                                        text = "Analog zu den besonderen Team-Namen werden hier die Abkürzungen und Hallen-Namen erfasst, die nicht komplett groß geschrieben werden sollen bzw. auch die, die komplett falsch geschrieben sind. z.B. die Abkürzung \"2.SPIELFELD\" ist komplett groß geschrieben, vermisst ein Leerzeichen und hat eine Zahl im Namen. Damit das richtig angezeigt wird, muss im ersten Feld die Variante, wie sie auf handball.net zu finden ist, erfasst werden - also \"2.SPIELFELD\" - und im zweiten Feld die korrekte Variante für die Anzeige - also \"2. Spielfeld\". Dasselbe gilt auch für falsch geschriebene Hallen-Namen.",
                                        style = MaterialTheme.typography.subtitle1,
                                        modifier = Modifier.padding(top = 5.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .padding(top = 10.dp)
                                    .fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth(0.4f)
                                        .padding(end = 5.dp)
                                ) {
                                    TextField(
                                        value = newSpecialGymNameBefore,
                                        onValueChange = { newSpecialGymNameBefore = it },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.End),
                                        label = {
                                            Text("Besonderer Hallen-Name in handball.net")
                                        },
                                        placeholder = {
                                            Text("z.B. \"2.SPIELFELD\"")
                                        }
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth(0.66f)
                                        .padding(end = 5.dp)
                                ) {
                                    TextField(
                                        value = newSpecialGymNameAfter,
                                        onValueChange = { newSpecialGymNameAfter = it },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.End),
                                        label = {
                                            Text("Besonderer Hallen-Name korrekt geschrieben")
                                        },
                                        placeholder = {
                                            Text("z.B. \"2. Spielfeld\"")
                                        }
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = {
                                            if (newSpecialGymNameBefore.isEmpty()) {
                                                logger.info("New special gym name was empty, aborting adding-process")
                                                backgroundCoroutineScope.launch {
                                                    scaffoldState.snackbarHostState.showSnackbar("Bitte gib einen Wert mindestens im ersten Textfeld ein!")
                                                }
                                                return@Button
                                            }
                                            val newSpecialGymName = SpecialGymName(newSpecialGymNameBefore, newSpecialGymNameAfter)
                                            specialGymNames.add(newSpecialGymName)
                                            nameNormalizer.addSpecialGymName(newSpecialGymName)
                                            newSpecialGymNameBefore = ""
                                            newSpecialGymNameAfter = ""
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        Text("Füge besonderen Hallen-Namen hinzu")
                                    }
                                }
                            }

                            sectionDivider(2.dp)

                            Row {
                                Column {
                                    expandableCard(
                                        title = "Besondere Hallen-Namen",
                                        content = {
                                            ExpandableTagList(
                                                items = specialGymNames,

                                                onRemove = { index ->
                                                    val removedElement = specialGymNames.removeAt(index)
                                                    nameNormalizer.removeSpecialGymName(removedElement)
                                                },

                                                itemContent = { specialGymName ->

                                                    Column {

                                                        Text(
                                                            text = specialGymName.key,
                                                            style = MaterialTheme.typography.body2
                                                        )

                                                        Text(
                                                            text = specialGymName.value,
                                                            style = MaterialTheme.typography.caption,
                                                            color = MaterialTheme.colors.onSurface.copy(
                                                                alpha = 0.6f
                                                            )
                                                        )
                                                    }
                                                }
                                            )
                                        }
                                    )
                                }
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