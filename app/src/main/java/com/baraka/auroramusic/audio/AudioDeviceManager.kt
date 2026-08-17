package com.baraka.auroramusic.audio

import android.content.Context
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class DeviceInfo(
    val name: String,
    val type: DeviceType
)

enum class DeviceType {
    SPEAKER, BLUETOOTH, WIRED, OTHER
}

@Singleton
class AudioDeviceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    
    private val _currentDevice = MutableStateFlow(getInitialDevice())
    val currentDevice: StateFlow<DeviceInfo> = _currentDevice.asStateFlow()

    private val deviceCallback = object : AudioDeviceCallback() {
        override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>?) {
            updateCurrentDevice()
        }

        override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>?) {
            updateCurrentDevice()
        }
    }

    init {
        audioManager.registerAudioDeviceCallback(deviceCallback, Handler(Looper.getMainLooper()))
    }

    private fun updateCurrentDevice() {
        val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        // Find the "best" output device. Usually the one being used for music.
        // Simple heuristic: look for Bluetooth or Wired first.
        val bestDevice = devices.firstOrNull { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP }
            ?: devices.firstOrNull { it.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES || it.type == AudioDeviceInfo.TYPE_WIRED_HEADSET }
            ?: devices.firstOrNull { it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }
            ?: devices.firstOrNull()

        _currentDevice.value = bestDevice?.toDeviceInfo() ?: DeviceInfo("Unknown", DeviceType.OTHER)
    }

    private fun getInitialDevice(): DeviceInfo {
        val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        val bestDevice = devices.firstOrNull { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP }
            ?: devices.firstOrNull { it.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES || it.type == AudioDeviceInfo.TYPE_WIRED_HEADSET }
            ?: devices.firstOrNull { it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }
            ?: devices.firstOrNull()
        
        return bestDevice?.toDeviceInfo() ?: DeviceInfo("Phone Speaker", DeviceType.SPEAKER)
    }

    private fun AudioDeviceInfo.toDeviceInfo(): DeviceInfo {
        val type = when (this.type) {
            AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> DeviceType.SPEAKER
            AudioDeviceInfo.TYPE_BLUETOOTH_A2DP, AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> DeviceType.BLUETOOTH
            AudioDeviceInfo.TYPE_WIRED_HEADPHONES, AudioDeviceInfo.TYPE_WIRED_HEADSET, AudioDeviceInfo.TYPE_USB_HEADSET -> DeviceType.WIRED
            else -> DeviceType.OTHER
        }
        
        val name = if (productName.isNullOrBlank()) {
            when (type) {
                DeviceType.SPEAKER -> "Phone Speaker"
                DeviceType.BLUETOOTH -> "Bluetooth Device"
                DeviceType.WIRED -> "Wired Headphones"
                else -> "Audio Device"
            }
        } else {
            productName.toString()
        }
        
        return DeviceInfo(name, type)
    }
}
