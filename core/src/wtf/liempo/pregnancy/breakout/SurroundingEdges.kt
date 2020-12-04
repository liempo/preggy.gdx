package wtf.liempo.pregnancy.breakout

import com.badlogic.gdx.math.Vector2
import ktx.math.vec2
import wtf.liempo.pregnancy.Game
import wtf.liempo.pregnancy.breakout.BreakoutUtils.translate

enum class SurroundingEdges(
        val start: Vector2,
        val end: Vector2) {

    CEILING(
            start = vec2(0f, translate(Game.GAME_HEIGHT)),
            end = vec2(translate(Game.GAME_WIDTH), translate(Game.GAME_HEIGHT)),
    ),
    FLOOR(
            start = vec2(0f, translate(Game.GAME_HEIGHT * 0.10f)),
            end = vec2(translate(Game.GAME_WIDTH), translate(Game.GAME_HEIGHT * 0.10f)),
    ),
    LEFT(
            start = vec2(0f, 0f),
            end = vec2(0f, translate(Game.GAME_HEIGHT)),
    ),
    RIGHT(
            start = vec2(translate(Game.GAME_WIDTH), 0f),
            end = vec2(translate(Game.GAME_WIDTH), translate(Game.GAME_HEIGHT)),
    )
}