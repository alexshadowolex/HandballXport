package alex.handball.ui.screens

import alex.handball.backgroundCoroutineScope
import alex.handball.model.NameNormalizer
import alex.handball.toJavaLocalDate
import alex.handball.ui.screenHeaderRow

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

@Composable
fun nameNormalizerScreen(
    nameNormalizer: NameNormalizer
) {
    var newClubName by remember { mutableStateOf("") }

    val scaffoldState = rememberScaffoldState()
    teamAbbreviations = nameNormalizer.teamAbbreviations.toMutableStateList()

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
                            text = "Wähle einen Datumsbereich aus",
                            style = MaterialTheme.typography.h6
                        )
                    }

                    Row(
                        modifier = Modifier
                            .padding(top = 10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                        ) {
                            Row {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth(0.5f)
                                ) {
                                    Text("Textfield inc")
                                }

                                Spacer(
                                    modifier = Modifier
                                        .width(10.dp)
                                )

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Text("Textfield inc")
                                }
                            }
                        }

                        Spacer(
                            modifier = Modifier
                                .width(10.dp)
                        )

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
                                Text("Zeige Spiele")
                            }
                        }
                    }
                }
            }
        }
    }
}