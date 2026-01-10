package th.co.cjexpress.store.sage.utils

object ScreenHolder {
    private var currentScreen: String? = null

    fun setScreen(screen: String) {
        currentScreen = screen
    }

    fun getCurrentScreen(): String? = currentScreen
}
