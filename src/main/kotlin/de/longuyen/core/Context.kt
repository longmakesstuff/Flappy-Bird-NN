package de.longuyen.core

import de.longuyen.FIELD_HEIGHT
import de.longuyen.FIELD_WIDTH
import de.longuyen.PIPE_SPACE
import de.longuyen.PIPE_WIDTH
import java.awt.Point
import java.awt.Rectangle
import java.util.*

data class Result(val steps: Long, var fitness: Double)

class ContextEncode(
    val velocity: Float,
    val birdX: Float,
    val birdY: Float,
    val topPipeX: Float,
    val topPipeY: Float,
    val bottomPipeX: Float,
    val bottomPipeY: Float,
    val topPipeDistance: Float,
    val bottomPipeDistance: Float
)

class Context() {
    val bird = Bird()
    val pipes = mutableListOf<Pair<Rectangle, Rectangle>>()
    val crossedPipes = mutableListOf<Pair<Rectangle, Rectangle>>()

    fun encode() : ContextEncode {
        return ContextEncode(
            bird.velocityY,
            bird.x,
            bird.y,
            pipes[0].first.x.toFloat(),
            pipes[0].first.y.toFloat(),
            pipes[0].second.x.toFloat(),
            pipes[0].second.y.toFloat(),
            Point(bird.x.toInt(), bird.y.toInt()).distance(Point(pipes[0].first.x, pipes[0].first.y)).toFloat(),
            Point(bird.x.toInt(), bird.y.toInt()).distance(Point(pipes[0].second.x, pipes[0].second.y)).toFloat()
        )
    }

    fun run(random: Random, decisionMaker: DecisionMaker, callback: Callback, sleep: Long = 100, maxSteps: Long = 100000L): Result {
        var fitness = 0.0
        var steps = 0L
        var lost = false
        while (!lost && steps < maxSteps) {
            bird.update()
            if (steps % 90L == 0L) {
                val pipeSpace = PIPE_SPACE +  random.nextInt(20) - 10
                val topPipeHeight = random.nextInt(200 - 130 + 1) + 130
                val topPipe = Rectangle(FIELD_WIDTH - PIPE_WIDTH, 0, PIPE_WIDTH, topPipeHeight)

                val bottomPipe = Rectangle(topPipe.x, topPipe.y + topPipe.height + pipeSpace, PIPE_WIDTH, FIELD_HEIGHT - (topPipe.y + topPipe.height + pipeSpace))
                pipes.add(Pair(topPipe, bottomPipe))
            }

            val disappearedPipes = mutableListOf<Pair<Rectangle, Rectangle>>()

            for (pipe in crossedPipes) {
                pipe.first.x -= 3
                pipe.second.x -= 3
                if (pipe.first.x + pipe.first.width <= 0) {
                    disappearedPipes.add(pipe)
                }
            }
            for (pipe in pipes) {
                pipe.first.x -= 3
                pipe.second.x -= 3
                if (pipe.first.x + pipe.first.width <= 0) {
                    disappearedPipes.add(pipe)
                }
                if (pipe.first.x + pipe.first.width < bird.x) {
                    fitness += 15.0
                    crossedPipes.add(pipe)
                }
                if (pipe.first.contains(bird.x.toInt(), bird.y.toInt()) || pipe.second.contains(bird.x.toInt(), bird.y.toInt())) {
                    lost = true
                }
            }
            crossedPipes.removeAll(disappearedPipes)
            pipes.removeAll(disappearedPipes)
            pipes.removeAll(crossedPipes)

            fitness++
            steps++

            if (bird.y > FIELD_HEIGHT || bird.y < 0) {
                lost = true
            }

            callback.callback()
            if (decisionMaker.jump(this)) {
                bird.jump()
            }
            Thread.sleep(sleep)
        }
        bird.reset()
        pipes.clear()
        crossedPipes.clear()
        return Result(steps, fitness)
    }
}