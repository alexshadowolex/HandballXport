package alex.handball.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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