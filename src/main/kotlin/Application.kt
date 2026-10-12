package alex.handball

import alex.handball.handler.GymHandler
import alex.handball.model.LeaguesDatabase
import androidx.compose.runtime.mutableStateOf

class Application {
    private val gymHandler =
        GymHandler()

    private val leaguesDatabase =
        LeaguesDatabase(
            gymHandler
        )

    lateinit var model: Model
        private set

    lateinit var handler: Handler
        private set

    var isInitialized = mutableStateOf(false)
        private set

    suspend fun initialize() {
        loadAll(
            gymHandler = gymHandler,
            leaguesDatabase = leaguesDatabase
        )

        model = Model(
            leaguesDatabase = leaguesDatabase
        )

        handler = Handler(
            gymHandler = gymHandler
        )

        isInitialized.value = true
    }
}
