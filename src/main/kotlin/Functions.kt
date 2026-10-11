package alex.handball

import com.github.tkuenneth.nativeparameterstoreaccess.NativeParameterStoreAccess
import com.github.tkuenneth.nativeparameterstoreaccess.WindowsRegistry
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import java.io.FileOutputStream
import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Paths
import java.time.format.DateTimeFormatterBuilder

private const val LOG_DIRECTORY = "logs"

/**
 * Sets up logging for the whole app with a MultiOutputStream
 */
fun setupLogging() {
    Files.createDirectories(Paths.get(LOG_DIRECTORY))

    val logFileName = DateTimeFormatterBuilder()
        .appendInstant(0)
        .toFormatter()
        .format(Clock.System.now().toJavaInstant())
        .replace(':', '-')

    val logFile = Paths.get(LOG_DIRECTORY, "${logFileName}.log").toFile().also {
        if (!it.exists()) {
            it.createNewFile()
        }
    }

    System.setOut(PrintStream(MultiOutputStream(System.out, FileOutputStream(logFile))))

    logger.info("Log file '${logFile.name}' has been created.")
}


/**
 * Checks if the OS is windows and is in dark mode.
 * @return {Boolean} true, if OS is windows and in dark mode, else false
 */
fun isWindowsInDarkMode(): Boolean {
    val windowsRegistryPath = "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize"
    val windowsRegistryLightThemeParameter = "AppsUseLightTheme"
    val darkModeRegistryHexValue = 0x0

    return if (NativeParameterStoreAccess.IS_WINDOWS) {
        WindowsRegistry.getWindowsRegistryEntry(
            windowsRegistryPath,
            windowsRegistryLightThemeParameter
        ) == darkModeRegistryHexValue
    } else {
        false
    }
}