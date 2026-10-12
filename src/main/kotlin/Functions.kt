package alex.handball

import alex.handball.handler.GymHandler
import alex.handball.model.League
import alex.handball.model.LeaguesDatabase
import com.github.tkuenneth.nativeparameterstoreaccess.NativeParameterStoreAccess
import com.github.tkuenneth.nativeparameterstoreaccess.WindowsRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Paths
import java.time.format.DateTimeFormatterBuilder
import kotlin.reflect.KClass
import kotlin.system.exitProcess

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


/**
 * Gets json-encoded data from a json file and parses it into a mutable set
 * @param dataFile the save file
 * @param clazz the class which is held in the set
 * @return A set with the given class
 */
@Suppress("UNCHECKED_CAST")
fun <T: Any> getSetDataFromJsonFile(dataFile: File, clazz: KClass<T>): MutableSet<T> {


    if(!dataFile.exists()) {
        logger.info("Created file \"${dataFile.name}\". Will start with empty set")
        dataFile.parentFile?.mkdirs()
        dataFile.createNewFile()
        return mutableSetOf()
    }

    return try {
        when(clazz) {
            String::class -> json.decodeFromString<MutableSet<String>>(dataFile.readText()) as MutableSet<T>
            League::class -> json.decodeFromString<MutableSet<League>>(dataFile.readText()) as MutableSet<T>
            else -> {
                logger.error("Type $clazz was not defined in function getJsonDataFromFile, aborting.")
                exitProcess(-1)
            }
        }
    } catch (e: Exception) {
        logger.error("Reading file \"${dataFile.name}\" failed with exception: ", e)
        logger.warn("Following will be the faulty json-data to debug: " + dataFile.readText())
        logger.info("Starting with empty set for ${dataFile.name}")
        return mutableSetOf()
    }
}


@OptIn(ExperimentalCoroutinesApi::class)
private val webDispatcher = Dispatchers.IO.limitedParallelism(8)

suspend fun getHtmlElementsFromHandballNet(
    link: String,
    rootElementClassName: String,
    elementCssQuery: String
): List<Element> = coroutineScope {

    // load page 1
    val firstPage = loadDocumentWithRetry(link) ?: return@coroutineScope listOf()

    val rootElement = firstPage
        .getElementsByClass(rootElementClassName).first()

    val result = rootElement
        ?.select(elementCssQuery)
        ?.toMutableList()
        ?: mutableListOf()

    // read max amount of pages XX from String "von XX"
    val maximumPage = firstPage
        .selectFirst(".pagination-info .total-pages")
        ?.text()
        ?.substringAfter("von")
        ?.trim()
        ?.toIntOrNull()
        ?: 1

    if (maximumPage <= 1) {
        return@coroutineScope result
    }

    // parallel read pages 2..n
    val pages = (2..maximumPage).map { page ->
        async(webDispatcher) {
            val currentLink = "$link&page=$page"

            val document = loadDocumentWithRetry(currentLink)

            document
                ?.getElementsByClass(rootElementClassName)?.first()
                ?.select(elementCssQuery)
                ?: emptyList()
        }
    }

    pages.awaitAll()
        .forEach { result.addAll(it) }

    result
}

private suspend fun loadDocumentWithRetry(
    link: String
): Document? = withContext(webDispatcher) {

    val maximumAmountReconnectTries = 10

    repeat(maximumAmountReconnectTries) { attempt ->

        try {
            return@withContext Jsoup.connect(link)
                .get()

        } catch (e: Exception) {

            logger.warn(
                "Caught exception while reading from url {}",
                link,
                e
            )

            if (attempt < maximumAmountReconnectTries - 1) {
                logger.info(
                    "Starting reconnect attempt {}",
                    attempt + 1
                )
            }
        }
    }

    logger.error("After $maximumAmountReconnectTries attempts aborted connecting to link $link.")
    return@withContext null
}


suspend fun loadAll(gymHandler: GymHandler, leaguesDatabase: LeaguesDatabase) = coroutineScope {
    /**
     * Gyms and clubs are not dependent on each other or
     * anything at all, so they can be loaded at the same time.
     */
    val gymJob = async {
        gymHandler.initializeLoadingAllGymData()

        logger.info("Finished loading all gym data")
    }

    // Before loading the leagues, we need all gym-data before.
    val leaguesJob = async {
        gymJob.await()

        leaguesDatabase.initializeLoadingAllLeagueAndFixtureData()

        logger.info("Finished loading all league and fixture data")
    }

    // Wait, until everything is loaded
    awaitAll(
        gymJob,
        leaguesJob
    )
}