package com.baraka.auroramusic.dj.interfaces

import com.baraka.auroramusic.dj.models.DJCommand

interface DJContextManager {
    fun updateContext(command: DJCommand)
    fun getMergedCommand(newIntent: DJCommand): DJCommand
    fun clear()
}
