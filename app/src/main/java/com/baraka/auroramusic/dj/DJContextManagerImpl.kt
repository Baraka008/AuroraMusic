package com.baraka.auroramusic.dj

import com.baraka.auroramusic.dj.interfaces.DJContextManager
import com.baraka.auroramusic.dj.models.DJCommand
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DJContextManagerImpl @Inject constructor() : DJContextManager {
    private var lastPlayCommand: DJCommand.PlayMusic? = null

    override fun updateContext(command: DJCommand) {
        if (command is DJCommand.PlayMusic) {
            lastPlayCommand = command
        }
    }

    override fun getMergedCommand(newIntent: DJCommand): DJCommand {
        return when (newIntent) {
            is DJCommand.ChangeMood -> {
                val base = lastPlayCommand ?: DJCommand.PlayMusic()
                base.copy(mood = newIntent.mood)
            }
            is DJCommand.IncreaseEnergy -> {
                val base = lastPlayCommand ?: DJCommand.PlayMusic()
                val newEnergy = (base.energy ?: 0.5f) + newIntent.amount
                base.copy(energy = newEnergy.coerceIn(0.0f, 1.0f))
            }
            is DJCommand.DecreaseEnergy -> {
                val base = lastPlayCommand ?: DJCommand.PlayMusic()
                val newEnergy = (base.energy ?: 0.5f) - newIntent.amount
                base.copy(energy = newEnergy.coerceIn(0.0f, 1.0f))
            }
            else -> newIntent
        }
    }

    override fun clear() {
        lastPlayCommand = null
    }
}
