package alex.handball

import alex.handball.handler.GymHandler
import alex.handball.model.LeaguesDatabase
import alex.handball.ui.app
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.io.PrintWriter
import java.io.StringWriter
import javax.swing.JOptionPane
import kotlin.system.exitProcess

val logger: org.slf4j.Logger = LoggerFactory.getLogger("Main")

val json = Json {
    prettyPrint = true
}

fun main() = try {
    application {
        setupLogging()

        val application = Application()

        Window(
            state = WindowState(
                size = DpSize(1200.dp, 900.dp)
            ),
            title = "HandballXport",
            onCloseRequest = ::exitApplication,
            icon = painterResource("logo.png"),
            resizable = false
        ) {
            app(application)
        }
    }
} catch (e: Throwable) {
    JOptionPane.showMessageDialog(
        null,
        e.message + "\n" +
                StringWriter().also {
                    e.printStackTrace(PrintWriter(it))
                },
        "InfoBox: File Debugger",
        JOptionPane.INFORMATION_MESSAGE
    )
    logger.error("Error while executing program.", e)
    exitProcess(0)
}


data class Model(
    val leaguesDatabase: LeaguesDatabase
)

data class Handler(
    val gymHandler: GymHandler
)