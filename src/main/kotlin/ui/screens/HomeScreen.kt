package alex.handball.ui.screens

import alex.handball.ui.screenHeaderRow
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun homeScreen() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .padding(all = 15.dp)
        ) {
            screenHeaderRow(headline = "Home")
        }
    }
}