package alex.handball.ui

import alex.handball.backgroundCoroutineScope
import alex.handball.customToString
import alex.handball.model.Fixture
import alex.handball.normalizeTeamName
import alex.handball.toJavaLocalDate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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


@Composable
fun dateRangeShowGamesSegment(
    fromDate: MutableState<String>,
    toDate: MutableState<String>,
    scaffoldState: ScaffoldState,
    onShowClick: (parsedFromDate: LocalDate, parsedToDate: LocalDate) -> Unit
) {
    Row {
        Text(
            text = "Select Date Range",
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
                                text = "From Date"
                            )
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
                                text = "To Date"
                            )
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
                                message = "Error while parsing one of the dates.",
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
                Text("Show Games")
            }
        }
    }
}


@Composable
fun RowScope.fixtureInfoHeader() {
    Text(
        text = "Date",
        modifier = Modifier
            .fillMaxWidth(0.1F)
            .fillMaxHeight()
            .padding(end = 5.dp)
            .align(Alignment.CenterVertically)
    )

    Text(
        text = "Time",
        modifier = Modifier
            .fillMaxWidth(0.085F)
            .fillMaxHeight()
            .padding(end = 5.dp)
            .align(Alignment.CenterVertically)
    )

    Text(
        text = "League",
        modifier = Modifier
            .fillMaxWidth(0.105F)
            .fillMaxHeight()
            .padding(end = 5.dp)
            .align(Alignment.CenterVertically)
    )

    Text(
        text = "Gym ID",
        modifier = Modifier
            .fillMaxWidth(0.095F)
            .fillMaxHeight()
            .padding(end = 5.dp)
            .align(Alignment.CenterVertically),
        textAlign = TextAlign.Center
    )

    Text(
        text = "Home Team",
        modifier = Modifier
            .fillMaxWidth(0.36F)
            .fillMaxHeight()
            .padding(end = 5.dp)
            .align(Alignment.CenterVertically),
        textAlign = TextAlign.Center
    )

    Text(
        text = "Guest Team",
        modifier = Modifier
            .fillMaxWidth(0.525F)
            .fillMaxHeight()
            .padding(end = 5.dp)
            .align(Alignment.CenterVertically),
        textAlign = TextAlign.Center
    )

    Text(
        text = "Planned Referees",
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(end = 5.dp)
            .align(Alignment.CenterVertically),
        textAlign = TextAlign.Center
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
fun RowScope.fixtureInfoLine(fixture: Fixture) {
    Text(
        text = fixture.date.customToString(),
        modifier = Modifier
            .fillMaxWidth(0.09F)
            .fillMaxHeight()
            .padding(end = 5.dp)
            .align(Alignment.CenterVertically)
    )

    Text(
        text = fixture.time,
        modifier = Modifier
            .fillMaxWidth(0.07F)
            .fillMaxHeight()
            .padding(end = 5.dp)
            .align(Alignment.CenterVertically)
    )

    Text(
        text = fixture.league.leagueName,
        modifier = Modifier
            .fillMaxWidth(0.09F)
            .fillMaxHeight()
            .padding(end = 5.dp)
            .align(Alignment.CenterVertically)
    )

    Text(
        text = fixture.gym.gymId,
        modifier = Modifier
            .fillMaxWidth(0.08F)
            .fillMaxHeight()
            .padding(end = 5.dp)
            .align(Alignment.CenterVertically)
    )

    Text(
        text = fixture.homeTeam.normalizeTeamName(),
        modifier = Modifier
            .fillMaxWidth(0.3F)
            .fillMaxHeight()
            .padding(end = 5.dp)
            .align(Alignment.CenterVertically)
    )

    Text(
        text = fixture.guestTeam.normalizeTeamName(),
        modifier = Modifier
            .fillMaxWidth(0.4F)
            .fillMaxHeight()
            .padding(end = 5.dp)
            .align(Alignment.CenterVertically)
    )
}