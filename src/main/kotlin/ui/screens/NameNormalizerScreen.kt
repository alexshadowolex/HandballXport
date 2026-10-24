package alex.handball.ui.screens

import alex.handball.backgroundCoroutineScope
import alex.handball.logger
import alex.handball.model.NameNormalizer
import alex.handball.model.SpecialGymName
import alex.handball.model.SpecialTeamName
import alex.handball.ui.expandableCard
import alex.handball.ui.screenHeaderRow
import alex.handball.ui.sectionDivider
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun nameNormalizerScreen(nameNormalizer: NameNormalizer) {
    var newTeamAbbreviation by remember { mutableStateOf("") }
    var newSpecialTeamNameBefore by remember { mutableStateOf("") }
    var newSpecialTeamNameAfter by remember { mutableStateOf("") }
    var newGymAbbreviation by remember { mutableStateOf("") }
    var newSpecialGymNameBefore by remember { mutableStateOf("") }
    var newSpecialGymNameAfter by remember { mutableStateOf("") }

    val scaffoldState = rememberScaffoldState()
    val teamAbbreviations = remember(nameNormalizer) {
        nameNormalizer.teamAbbreviations.toMutableStateList()
    }
    val gymAbbreviations = remember(nameNormalizer) {
        nameNormalizer.gymAbbreviations.toMutableStateList()
    }
    val specialTeamNames = remember(nameNormalizer) {
        nameNormalizer.specialTeamNames.toMutableStateList()
    }
    val specialGymNames = remember(nameNormalizer) {
        nameNormalizer.specialGymNames.toMutableStateList()
    }

    val scrollState = rememberScrollState()
    val scrollBarAdapter = rememberScrollbarAdapter(scrollState)

    MaterialTheme {
        Scaffold(scaffoldState = scaffoldState) {
            Column(
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxSize()
            ) {
                screenHeaderRow(headline = "Namennormalisierer")
                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Hier können Sonderfälle definiert werden, die bei der Normalisierung der Namen von Hallen und Teams berücksichtigt werden sollen.\n" +
                            "Bei handball.net sind alle Team- und Hallennamen komplett groß geschrieben, was mit dem Namennormalisierer für die Anzeige und den Export angepasst wird.\n" +
                            "Dabei wird bei jedem Wort und Teilwort einfach der erste Buchstabe groß geschrieben und alle weiteren werden klein geschrieben. Neue Worte werden über Leerzeichen, Bindestriche und Klammern identifiziert.\n" +
                            "Manche Worte sollen aber komplett groß geschrieben werden (z.B. \"HV\"), manche Worte teils groß und teils klein (z.B. \"VfB\") und manche Worte sind komplett falsch geschrieben. Diese Sonderfälle können hier definiert werden.",
                    style = MaterialTheme.typography.subtitle1
                )

                sectionDivider(thickness = 5.dp)

                Row(modifier = Modifier.padding(top = 10.dp)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(0.98f)
                            .verticalScroll(scrollState)
                    ) {
                        AbbreviationSection(
                            title = "Team-Abkürzungen",
                            description = "Hier werden die Abkürzungen von Team-Namen erfasst, die komplett groß geschrieben werden sollen (z.B. \"HV\", \"LHV\").",
                            fieldLabel = "Team-Abkürzung",
                            placeholder = "z.B. \"HV\" oder \"LHV\"",
                            buttonLabel = "Füge Team-Abkürzung hinzu",
                            value = newTeamAbbreviation,
                            onValueChange = { newTeamAbbreviation = it },
                            items = teamAbbreviations,
                            onAdd = {
                                teamAbbreviations.add(it)
                                nameNormalizer.addTeamAbbreviation(it)
                                newTeamAbbreviation = ""
                            },
                            onRemove = { index ->
                                nameNormalizer.removeTeamAbbreviation(teamAbbreviations.removeAt(index))
                            },
                            onInvalid = {
                                showValidationMessage(scaffoldState, "Bitte gib einen Wert im Textfeld ein!")
                            }
                        )

                        sectionDivider(5.dp)

                        SpecialNameSection(
                            title = "Besondere Team-Namen",
                            description = "Hier werden die Abkürzungen und Team-Namen erfasst, die nicht komplett groß geschrieben werden sollen bzw. auch die, die komplett falsch geschrieben sind. z.B. die Abkürzung \"VfB\" hat einen kleinen Buchstaben. Damit die richtig angezeigt wird, muss im ersten Feld die Variante, wie sie auf handball.net zu finden ist, erfasst werden - also \"VFB\" - und im zweiten Feld die korrekte Variante für die Anzeige - also \"VfB\". Dasselbe gilt auch für falsch geschriebene Team-Namen, z.B. \"GROSSENHAIN\" -> \"Großenhain\".",
                            beforeLabel = "Besonderer Team-Name in handball.net",
                            beforePlaceholder = "z.B. \"VFB\" oder \"GROSSENHAIN\"",
                            afterLabel = "Besonderer Team-Name korrekt geschrieben",
                            afterPlaceholder = "z.B. \"VfB\" oder \"Großenhain\"",
                            buttonLabel = "Füge besonderen Team-Namen hinzu",
                            before = newSpecialTeamNameBefore,
                            after = newSpecialTeamNameAfter,
                            onBeforeChange = { newSpecialTeamNameBefore = it },
                            onAfterChange = { newSpecialTeamNameAfter = it },
                            items = specialTeamNames,
                            onAdd = { before, after ->
                                val item = SpecialTeamName(before, after)
                                specialTeamNames.add(item)
                                nameNormalizer.addSpecialTeamName(item)
                                newSpecialTeamNameBefore = ""
                                newSpecialTeamNameAfter = ""
                            },
                            onRemove = { index ->
                                nameNormalizer.removeSpecialTeamName(specialTeamNames.removeAt(index))
                            },
                            onInvalid = {
                                showValidationMessage(scaffoldState, "Bitte gib einen Wert mindestens im ersten Textfeld ein!")
                            }
                        )

                        sectionDivider(5.dp)

                        AbbreviationSection(
                            title = "Hallen-Abkürzungen",
                            description = "Analog zu den Team-Abkürzungen werden hier die Abkürzungen von Hallen erfasst, die komplett groß geschrieben werden sollen (z.B. \"SH\", \"BSZ\").",
                            fieldLabel = "Hallen-Abkürzung",
                            placeholder = "z.B. \"SH\" oder \"BSZ\"",
                            buttonLabel = "Füge Hallen-Abkürzung hinzu",
                            value = newGymAbbreviation,
                            onValueChange = { newGymAbbreviation = it },
                            items = gymAbbreviations,
                            onAdd = {
                                gymAbbreviations.add(it)
                                nameNormalizer.addGymAbbreviation(it)
                                newGymAbbreviation = ""
                            },
                            onRemove = { index ->
                                nameNormalizer.removeGymAbbreviation(gymAbbreviations.removeAt(index))
                            },
                            onInvalid = {
                                showValidationMessage(scaffoldState, "Bitte gib einen Wert im Textfeld ein!")
                            }
                        )

                        sectionDivider(5.dp)

                        SpecialNameSection(
                            title = "Besondere Hallen-Namen",
                            description = "Analog zu den besonderen Team-Namen werden hier die Abkürzungen und Hallen-Namen erfasst, die nicht komplett groß geschrieben werden sollen bzw. auch die, die komplett falsch geschrieben sind. z.B. die Abkürzung \"2.SPIELFELD\" ist komplett groß geschrieben, vermisst ein Leerzeichen und hat eine Zahl im Namen. Damit das richtig angezeigt wird, muss im ersten Feld die Variante, wie sie auf handball.net zu finden ist, erfasst werden - also \"2.SPIELFELD\" - und im zweiten Feld die korrekte Variante für die Anzeige - also \"2. Spielfeld\". Dasselbe gilt auch für falsch geschriebene Hallen-Namen.",
                            beforeLabel = "Besonderer Hallen-Name in handball.net",
                            beforePlaceholder = "z.B. \"2.SPIELFELD\"",
                            afterLabel = "Besonderer Hallen-Name korrekt geschrieben",
                            afterPlaceholder = "z.B. \"2. Spielfeld\"",
                            buttonLabel = "Füge besonderen Hallen-Namen hinzu",
                            before = newSpecialGymNameBefore,
                            after = newSpecialGymNameAfter,
                            onBeforeChange = { newSpecialGymNameBefore = it },
                            onAfterChange = { newSpecialGymNameAfter = it },
                            items = specialGymNames,
                            onAdd = { before, after ->
                                val item = SpecialGymName(before, after)
                                specialGymNames.add(item)
                                nameNormalizer.addSpecialGymName(item)
                                newSpecialGymNameBefore = ""
                                newSpecialGymNameAfter = ""
                            },
                            onRemove = { index ->
                                nameNormalizer.removeSpecialGymName(specialGymNames.removeAt(index))
                            },
                            onInvalid = {
                                showValidationMessage(scaffoldState, "Bitte gib einen Wert mindestens im ersten Textfeld ein!")
                            }
                        )
                    }

                    VerticalScrollbar(
                        adapter = scrollBarAdapter,
                        modifier = Modifier
                            .padding(start = 2.dp, end = 1.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AbbreviationSection(
    title: String,
    description: String,
    fieldLabel: String,
    placeholder: String,
    buttonLabel: String,
    value: String,
    onValueChange: (String) -> Unit,
    items: SnapshotStateList<String>,
    onAdd: (String) -> Unit,
    onRemove: (Int) -> Unit,
    onInvalid: () -> Unit
) {
    SectionHeading(title, description)

    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth(0.8f)
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .align(Alignment.End)
                    .padding(end = 5.dp),
                label = {
                    Text(
                        text = fieldLabel,
                        color = MaterialTheme.colors.onPrimary
                    )
                },
                placeholder = { Text(placeholder) },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = MaterialTheme.colors.onPrimary
                )
            )
        }

        Column (
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Button(
                onClick = {
                    if (value.isBlank()) {
                        logger.info("Abbreviation was empty, aborting adding-process")
                        onInvalid()
                    } else {
                        onAdd(value)
                    }
                }
            ) {
                Text(buttonLabel)
            }
        }
    }

    sectionDivider(2.dp)
    expandableCard(title = title) {
        ExpandableTagList(
            items = items,
            onRemove = onRemove,
            itemContent = { Text(it, style = MaterialTheme.typography.body2) }
        )
    }
}

@Composable
private fun <T> SpecialNameSection(
    title: String,
    description: String,
    beforeLabel: String,
    beforePlaceholder: String,
    afterLabel: String,
    afterPlaceholder: String,
    buttonLabel: String,
    before: String,
    after: String,
    onBeforeChange: (String) -> Unit,
    onAfterChange: (String) -> Unit,
    items: List<T>,
    onAdd: (String, String) -> Unit,
    onRemove: (Int) -> Unit,
    onInvalid: () -> Unit
) {
    SectionHeading(title, description)

    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth(0.8f)
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TextField(
                    value = before,
                    onValueChange = onBeforeChange,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(end = 5.dp),
                    label = {
                        Text(
                            text = beforeLabel,
                            color = MaterialTheme.colors.onPrimary
                        )
                    },
                    placeholder = { Text(beforePlaceholder) },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        cursorColor = MaterialTheme.colors.onPrimary
                    )
                )

                TextField(
                    value = after,
                    onValueChange = onAfterChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 5.dp),
                    label = {
                        Text(
                            text = afterLabel,
                            color = MaterialTheme.colors.onPrimary
                        )
                    },
                    placeholder = { Text(afterPlaceholder) },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        cursorColor = MaterialTheme.colors.onPrimary
                    )
                )
            }
        }

        Column (
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Button(
                onClick = {
                    if (before.isBlank()) {
                        logger.info("Special name was empty, aborting adding-process")
                        onInvalid()
                    } else {
                        onAdd(before, after)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(buttonLabel)
            }
        }
    }

    sectionDivider(2.dp)
    expandableCard(title = title) {
        ExpandableTagList(
            items = items,
            onRemove = onRemove,
            itemContent = { item ->
                when (item) {
                    is SpecialTeamName -> SpecialNameTag(item.key, item.value)
                    is SpecialGymName -> SpecialNameTag(item.key, item.value)
                }
            }
        )
    }
}

@Composable
private fun SectionHeading(title: String, description: String) {
    Column {
        Text(text = title, style = MaterialTheme.typography.h6)
        Text(
            text = description,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(top = 5.dp)
        )
    }
}

@Composable
private fun SpecialNameTag(key: String, value: String) {
    Column {
        Text(text = key, style = MaterialTheme.typography.body2)
        Text(
            text = value,
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
    }
}

private fun showValidationMessage(
    scaffoldState: ScaffoldState,
    message: String
) {
    backgroundCoroutineScope.launch {
        scaffoldState.snackbarHostState.showSnackbar(message)
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
            .padding(5.dp),
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
    ) {
        items.forEachIndexed { index, item ->
            TagBubble(onRemove = { onRemove(index) }) {
                itemContent(item)
            }
        }
    }
}

@Composable
private fun TagBubble(
    onRemove: () -> Unit,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colors.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.15f),
                shape = RoundedCornerShape(50)
            )
            .padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.padding(end = 4.dp)) {
            content()
        }

        IconButton(
            onClick = onRemove,
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
