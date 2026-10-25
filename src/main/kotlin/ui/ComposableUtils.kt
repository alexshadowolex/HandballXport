package alex.handball.ui

import alex.handball.backgroundCoroutineScope
import alex.handball.customToString
import alex.handball.model.Fixture
import alex.handball.model.NameNormalizer
import alex.handball.toJavaLocalDate
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun screenHeaderRow(headline: String) {
    Row {
        Text(
            text = headline,
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun expandableCard(title: String, content: @Composable () -> Unit) {
    var expanded by remember { mutableStateOf (false) }

    Card(
        elevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                expanded = !expanded
            }
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(8.dp)
            )
            if (expanded) {
                content()
            }
        }
    }
}


@Composable
fun sectionDivider(thickness: Dp) {
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 10.dp),
        color = MaterialTheme.colors.secondary,
        thickness = thickness
    )
}

/**
 * Displays a vertical divider used to visually separate columns or side-by-side content.
 *
 * The divider fills the available height, applies vertical padding,
 * and uses the primary theme color.
 */
@Composable
fun columnDivider() {
    Divider(
        modifier = Modifier
            .fillMaxHeight()
            .padding(vertical = 5.dp)
            .width(2.dp)
            .background(MaterialTheme.colors.primary)
    )
}


@Composable
fun dateRangeShowGamesSegment(
    fromDate: MutableState<String>,
    toDate: MutableState<String>,
    scaffoldState: ScaffoldState,
    onShowClick: (parsedFromDate: LocalDate, parsedToDate: LocalDate) -> Unit
) {
    Row {
        Text(
            text = "Wähle einen Datumsbereich aus",
            style = MaterialTheme.typography.h6
        )
    }

    Row (
        modifier = Modifier
            .padding(top = 10.dp)
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth(0.7f)
        ) {
            Row {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                ) {
                    // TODO: Swap out with date picker and make field not editable
                    TextField(
                        label = {
                            Text(
                                color = MaterialTheme.colors.onPrimary,
                                text = "Startdatum"
                            )
                        },
                        placeholder = {
                            Text(
                                text = "Format: TT.MM.JJJJ",
                                color = MaterialTheme.colors.onPrimary
                            )
                        },
                        value = fromDate.value,
                        onValueChange = { value ->
                            fromDate.value = value
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(
                            cursorColor = MaterialTheme.colors.onPrimary
                        )
                    )
                }

                Spacer(
                    modifier = Modifier
                        .width(10.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    // TODO: Swap out with date picker and make field not editable
                    TextField(
                        label = {
                            Text(
                                color = MaterialTheme.colors.onPrimary,
                                text = "Enddatum"
                            )
                        },
                        placeholder = {
                            Text("Format: TT.MM.JJJJ")
                        },
                        value = toDate.value,
                        onValueChange = { value ->
                            toDate.value = value
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(
                            cursorColor = MaterialTheme.colors.onPrimary
                        )
                    )
                }
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
                    val parsedFromDate = fromDate.value.toJavaLocalDate()
                    val parsedToDate = toDate.value.toJavaLocalDate()

                    if(parsedFromDate == null || parsedToDate == null) {
                        backgroundCoroutineScope.launch {
                            scaffoldState.snackbarHostState.showSnackbar(
                                message = "Fehler beim Übernehmen einer der Datumsangaben",
                                duration = SnackbarDuration.Short
                            )
                        }
                        return@Button
                    }

                    onShowClick(parsedFromDate, parsedToDate)
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text("Zeige Spiele")
            }
        }
    }
}


@Composable
fun RowScope.fixtureInfoHeader() {
    Text(
        text = "Datum",
        modifier = Modifier
            .fillMaxWidth(0.1F)
            .fillMaxHeight()
            .padding(end = 5.dp)
            .align(Alignment.CenterVertically)
    )

    Text(
        text = "Zeit",
        modifier = Modifier
            .fillMaxWidth(0.085F)
            .fillMaxHeight()
            .padding(end = 5.dp)
            .align(Alignment.CenterVertically)
    )

    Text(
        text = "Liga",
        modifier = Modifier
            .fillMaxWidth(0.105F)
            .fillMaxHeight()
            .padding(end = 5.dp)
            .align(Alignment.CenterVertically)
    )

    Text(
        text = "H-Nr.",
        modifier = Modifier
            .fillMaxWidth(0.095F)
            .fillMaxHeight()
            .padding(end = 5.dp)
            .align(Alignment.CenterVertically)
    )

    Text(
        text = "Heimmannschaft",
        modifier = Modifier
            .fillMaxWidth(0.5F)
            .fillMaxHeight()
            .padding(end = 5.dp)
            .align(Alignment.CenterVertically)
    )

    Text(
        text = "Gastmannschaft",
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(end = 5.dp)
            .align(Alignment.CenterVertically)
    )
}

@Composable
fun RowScope.deleteFixtureButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(30.dp)
            .align(Alignment.CenterVertically)
            .padding(end = 5.dp)
            .pointerHoverIcon(PointerIcon.Hand)
    ) {
        Icon(
            imageVector = Icons.Outlined.Delete,
            contentDescription = "delete_icon",
            tint = Color.Unspecified
        )
    }
}

@Composable
fun RowScope.fixtureInfoLine(fixture: Fixture, nameNormalizer: NameNormalizer) {
    Text(
        text = fixture.date.customToString(),
        modifier = Modifier
            .fillMaxWidth(0.1F)
            .fillMaxHeight()
            .padding(end = 5.dp)
            .align(Alignment.CenterVertically)
    )

    Text(
        text = fixture.time,
        modifier = Modifier
            .fillMaxWidth(0.085F)
            .fillMaxHeight()
            .padding(end = 5.dp)
            .align(Alignment.CenterVertically)
    )

    Text(
        text = fixture.league.leagueName,
        modifier = Modifier
            .fillMaxWidth(0.105F)
            .fillMaxHeight()
            .padding(end = 5.dp)
            .align(Alignment.CenterVertically)
    )

    Text(
        text = fixture.gym.gymId,
        modifier = Modifier
            .fillMaxWidth(0.095F)
            .fillMaxHeight()
            .padding(end = 5.dp)
            .align(Alignment.CenterVertically)
    )

    Text(
        text = nameNormalizer.normalizeTeamName(fixture.homeTeam),
        modifier = Modifier
            .fillMaxWidth(0.5F)
            .fillMaxHeight()
            .padding(end = 5.dp)
            .align(Alignment.CenterVertically)
    )

    Text(
        text = nameNormalizer.normalizeTeamName(fixture.guestTeam),
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(end = 5.dp)
            .align(Alignment.CenterVertically)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HelpIcon(
    tooltipText: String,
    modifier: Modifier = Modifier
) {
    TooltipArea(
        tooltip = {
            Surface(
                modifier = Modifier.widthIn(max = 320.dp),
                color = MaterialTheme.colors.surface,
                shape = RoundedCornerShape(8.dp),
                elevation = 8.dp,
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.15f)
                )
            ) {
                Text(
                    text = tooltipText,
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 10.dp
                    ),
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.body2
                )
            }
        },
        modifier = modifier,
        delayMillis = 400,
        tooltipPlacement = TooltipPlacement.CursorPoint()
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Hilfe",
            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
        )
    }
}


@Composable
fun LeagueLinkHelpDialog(
    onDismiss: () -> Unit,
    isNuLiga: Boolean
) {
    var nuLigaOrHandballNet: String
    var headerText = "Der Link muss auf die "
    var stepList: List<String>
    if(isNuLiga) {
        nuLigaOrHandballNet = "NuLiga"
        headerText += "Gesamtübersicht aller Spiele der Liga zeigen. Dabei sind alle Spiele auf derselben Seite zu sehen."
        stepList = listOf(
            "Gehe auf https://hvs-handball.liga.nu/",
            "Klicke links auf den korrekten Wettbewerb (z.B.\"HVS 2026/2027\")",
            "Klicke auf die korrekte Liga (bzw. vorher auf \"MHV\", wenn es eine MHV-Liga ist)",
            "Klicke rechts auf \"Spielplan (Gesamt)\"",
            "Auf der Seite siehst du jetzt alle Spiele der Liga. Kopiere den Link aus der Adressleiste des Browsers.",
            "Beispiel-Link: https://hvs-handball.liga.nu/cgi-bin/WebObjects/nuLigaHBDE.woa/wa/groupPage?displayTyp=vorrunde&displayDetail=meetings&championship=MHV+2026%2F27&group=485338"
        )
    } else {
        nuLigaOrHandballNet = "Handball.net"
        headerText += "Gesamtübersicht aller Spiele der Liga im Spielkalender zeigen. Dabei sind alle Spiele auf unterschiedliche Seiten verteilt - der Link jedoch soll nur auf die erste Seite zeigen."
        stepList = listOf(
            "Gehe auf https://www.handball.net/",
            "Klicke auf \"Spielbetrieb\" und dann auf \"Spielkalender\"",
            "Wähle im Filter \"Verband\" den richtigen Landesverband aus (z.B. \"Handballverband Sachsen\")",
            "Wähle im Filter \"Altersklasse\" die richtige Altersklasse aus (z.B. \"Erwachsene\")",
            "Wähle im Filter \"Geschlecht\" das richtige Geschlecht aus (z.B. \"Männlich\")",
            "Wähle im Filter \"Spielklasse\" die richtige Spielklasse aus (z.B. \"Oberliga Männlich\")",
            "Wähle im Filter \"Datum\" einen Datumsbereich aus, in dem alle Spiele der Liga erfasst sind. (z.B. 30.07.2026 bis 30.07.2027)",
            "Auf der Seite siehst du jetzt die erste Seite von allen Spielen der Liga. Kopriere den Link aus der Adressleiste des Browsers.",
            "Beispiel-Link: https://www.handball.net/spielbetrieb/spielkalender?verband=14&altersklasse=1&geschlecht=male&spielklasse=5&date_from=2026-07-30&date_to=2027-07-30&season=2026%2F2027"
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Wie finde ich den richtigen Link? Anleitung für $nuLigaOrHandballNet")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(headerText)

                stepList.forEachIndexed { index, stepText ->
                    LinkHelpStep(
                        number = index + 1,
                        text = stepText
                    )
                }

                Text(
                    text = "Achte darauf, dass du die richtige Liga ausgewählt hast.",
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Verstanden")
            }
        }
    )
}

@Composable
private fun LinkHelpStep(
    number: Int,
    text: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.size(28.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number.toString(),
                    color = MaterialTheme.colors.onPrimary,
                    style = MaterialTheme.typography.body2
                )
            }
        }

        Text(
            text = text,
            modifier = Modifier.weight(1f)
        )
    }
}