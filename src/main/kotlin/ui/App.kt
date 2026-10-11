package alex.handball.ui

import alex.handball.isWindowsInDarkMode
import alex.handball.ui.navigation.NavController
import alex.handball.ui.navigation.NavigationHost
import alex.handball.ui.navigation.composable
import alex.handball.ui.navigation.rememberNavController
import alex.handball.ui.screens.homeScreen
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

val lightColorPalette = lightColors(
    primary = Color(0xff4466ff),
    onPrimary = Color.White,
    secondary = Color(0xff0b5b8e),
    background = Color.White,
    onBackground = Color.Black,
)

val darkColorPalette = darkColors(
    primary = Color(0xff2244bb),
    onPrimary = Color.White,
    secondary = Color(0xff5bbbfe),
    background = Color.DarkGray,
    onBackground = Color.White,
)

@Composable
@Preview
fun app() {

    var isInDarkMode by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (true) {
            isInDarkMode = isWindowsInDarkMode()
            delay(1.seconds)
        }
    }


    val screens = Screen.entries
    val navController by rememberNavController(Screen.HomeScreen.name)
    val currentScreen by remember {
        navController.currentScreen
    }

    MaterialTheme(
        colors = if (isInDarkMode) {
            darkColorPalette
        } else {
            lightColorPalette
        }
    ) {
        Scaffold {
            Column {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row {
                        NavigationRail(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(90.dp)
                        ) {
                            screens.forEach {
                                NavigationRailItem(
                                    selected = currentScreen == it.name,
                                    icon = {
                                        Icon(
                                            imageVector = it.icon,
                                            contentDescription = it.label
                                        )
                                    },
                                    label = {
                                        Text(it.label)
                                    },
                                    alwaysShowLabel = true,
                                    onClick = {
                                        navController.navigate(it.name)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            customNavigationHost(
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}


// Navigation
enum class Screen(
    val label: String,
    val icon: ImageVector
) {
    HomeScreen(
        label = "Home",
        icon = Icons.Filled.Home
    )
}

@Composable
fun customNavigationHost(
    navController: NavController
) {
    NavigationHost(navController) {
        composable(Screen.HomeScreen.name) {
            homeScreen()
        }
    }.build()
}