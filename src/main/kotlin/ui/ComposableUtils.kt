package alex.handball.ui

import alex.handball.backgroundCoroutineScope
import alex.handball.customToString
import alex.handball.model.Fixture
import alex.handball.model.NameNormalizer
import alex.handball.toJavaLocalDate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
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
                            Text("Format: TT.MM.JJJJ")
                        },
                        value = fromDate.value,
                        onValueChange = { value ->
                            fromDate.value = value
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
                            .fillMaxWidth()
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