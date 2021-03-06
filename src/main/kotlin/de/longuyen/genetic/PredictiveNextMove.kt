package de.longuyen.genetic

import de.longuyen.MAX_DISTANCE
import de.longuyen.MAX_VELOCITY
import de.longuyen.BRAIN_SIZES
import de.longuyen.SYMMETRICAL
import de.longuyen.core.Context
import de.longuyen.core.DecisionMaker

class PredictiveNextMove(private val chromosome: Chromosome) : DecisionMaker {
    override fun jump(context: Context): Boolean {
        val valRawInput = context.encode()
        val input = doubleArrayOf(
            valRawInput.velocity / MAX_VELOCITY,
            valRawInput.birdX / MAX_DISTANCE,
            valRawInput.birdY / MAX_DISTANCE,
            valRawInput.topPipeX / MAX_DISTANCE,
            valRawInput.topPipeY / MAX_DISTANCE,
            valRawInput.bottomPipeX / MAX_DISTANCE,
            valRawInput.bottomPipeY / MAX_DISTANCE,
            valRawInput.topPipeDistance / MAX_DISTANCE,
            valRawInput.bottomPipeDistance / MAX_DISTANCE
        )
        val net = Brain(BRAIN_SIZES)
        net.loadParameters(chromosome.data, SYMMETRICAL)
        val ret = net.forwardPropagation(input)
        return ret[0] > 0.5
    }
}