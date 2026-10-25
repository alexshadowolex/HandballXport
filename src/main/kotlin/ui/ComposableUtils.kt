package alex.handball.ui

import alex.handball.backgroundCoroutineScope
import alex.handball.customToString
import alex.handball.model.Fixture
import alex.handball.model.NameNormalizer
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

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
    fromDate: MutableState<LocalDate>,
    toDate: MutableState<LocalDate>,
    scaffoldState: ScaffoldState,
    onShowClick: (fromDate: LocalDate, toDate: LocalDate) -> Unit
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
                    DatePickerField(
                        label = "Startdatum",
                        date = fromDate.value,
                        onDateSelected = { fromDate.value = it }
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
                    DatePickerField(
                        label = "Enddatum",
                        date = toDate.value,
                        onDateSelected = { toDate.value = it }
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
                .align(Alignment.CenterVertically)
        ) {
            Button(
                onClick = {

                    if(toDate.value < fromDate.value) {
                        backgroundCoroutineScope.launch {
                            scaffoldState.snackbarHostState.showSnackbar(
                                message = "Fehler: Das Enddatum darf nicht vor dem Startdatum liegen!",
                                duration = SnackbarDuration.Short
                            )
                        }
                        return@Button
                    }

                    onShowClick(fromDate.value, toDate.value)
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

@Composable
fun DatePickerField(
    label: String,
    date: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = date.format(
            DateTimeFormatter.ofPattern("dd.MM.yyyy")
        ),
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            IconButton(
                onClick = { showDatePicker = true },
                modifier = Modifier
                    .pointerHoverIcon(PointerIcon.Hand)
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "Datum auswählen"
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )

    if (showDatePicker) {
        DatePickerDialog(
            initialDate = date,
            onDateSelected = onDateSelected,
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
fun DatePickerDialog(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var displayedMonth by remember {
        mutableStateOf(YearMonth.from(initialDate))
    }

    var selectedDate by remember {
        mutableStateOf(initialDate)
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colors.surface
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            displayedMonth = displayedMonth.minusMonths(1)
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Vorheriger Monat"
                        )
                    }

                    Text(
                        text = displayedMonth.format(
                            DateTimeFormatter.ofPattern(
                                "MMMM yyyy",
                                Locale.GERMAN
                            )
                        ),
                        style = MaterialTheme.typography.h6
                    )

                    IconButton(
                        onClick = {
                            displayedMonth = displayedMonth.plusMonths(1)
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Nächster Monat"
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Wochentage
                Row {
                    listOf("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So")
                        .forEach { day ->
                            Box(
                                modifier = Modifier.size(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day,
                                    style = MaterialTheme.typography.caption
                                )
                            }
                        }
                }

                val firstDayOffset =
                    displayedMonth.atDay(1).dayOfWeek.value - 1
                val daysInMonth = displayedMonth.lengthOfMonth()

                val totalCells = ((firstDayOffset + daysInMonth + 6) / 7) * 7

                for (week in 0 until totalCells / 7) {
                    Row {
                        for (dayOfWeek in 0..6) {
                            val cellIndex = week * 7 + dayOfWeek
                            val dayNumber = cellIndex - firstDayOffset + 1

                            Box(
                                modifier = Modifier.size(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (dayNumber in 1..daysInMonth) {
                                    val date = displayedMonth.atDay(dayNumber)
                                    val isSelected = date == selectedDate

                                    Surface(
                                        modifier = Modifier.size(36.dp)
                                            .clickable {
                                                selectedDate = date
                                            },
                                        shape = CircleShape,
                                        color = if (isSelected) {
                                            MaterialTheme.colors.primary
                                        } else {
                                            Color.Transparent
                                        }
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = dayNumber.toString(),
                                                color = if (isSelected) {
                                                    contentColorFor(
                                                        MaterialTheme.colors.primary
                                                    )
                                                } else {
                                                    MaterialTheme.colors.onSurface
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Abbrechen")
                    }

                    TextButton(
                        onClick = {
                            onDateSelected(selectedDate)
                            onDismiss()
                        }
                    ) {
                        Text("Übernehmen")
                    }
                }
            }
        }
    }
}