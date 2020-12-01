package wtf.liempo.pregnancy.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import wtf.liempo.pregnancy.Game

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        LwjglApplicationConfiguration().apply {
            width = 360; height = 640; title = "Pregnancy"
            LwjglApplication(Game(), this)
        }
    }
}