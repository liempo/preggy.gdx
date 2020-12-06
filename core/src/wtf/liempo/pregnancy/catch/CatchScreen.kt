package wtf.liempo.pregnancy.catch

import com.badlogic.gdx.graphics.Texture
import ktx.app.KtxScreen
import ktx.assets.load
import wtf.liempo.pregnancy.Game
import wtf.liempo.pregnancy.mainmenu.MainMenuScreen

class CatchScreen(private val game: Game): KtxScreen {

    enum class Assets(val path: String) {
        BEER("catch/beer.png"),
        CIGARETTE("catch/cigarette.png"),
        MILK("catch/milk.png"),
        WATER("catch/water.png"),
        JUICE("catch/juice.png"),
        GIRL("catch/girl.png"),
    }

    companion object {
        internal fun show(game: Game) {
            with (game) {
                assets.let {
                    for (asset in Assets.values())
                        game.assets.load<Texture>(asset.path)
                    it.finishLoading()
                }

                removeScreen<MainMenuScreen>()
                setScreen<CatchScreen>()
            }
        }
    }
}