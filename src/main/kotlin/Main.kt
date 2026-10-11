package alex.handball

import alex.handball.ui.app
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import org.slf4j.LoggerFactory
import java.io.PrintWriter
import java.io.StringWriter
import javax.swing.JOptionPane
import kotlin.system.exitProcess

val logger: org.slf4j.Logger = LoggerFactory.getLogger("Main")

fun main() = try {
    application {
        setupLogging()

        Window(
            state = WindowState(
                size = DpSize(1200.dp, 900.dp)
            ),
            title = "Referee Manager",
            onCloseRequest = ::exitApplication,
            icon = painterResource("logo.png"),
            resizable = false
        ) {
            app()
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
