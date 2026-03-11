package com.fafafafa.buildsound.player

import com.fafafafa.buildsound.settings.BuildSoundSettings
import com.intellij.openapi.diagnostic.Logger
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.FloatControl
import javax.sound.sampled.LineEvent

internal object SoundPlayer {

    private val LOG = Logger.getInstance(SoundPlayer::class.java)

    fun playFailureSound() {
        val settings = BuildSoundSettings.getInstance().state
        if (!settings.enabled) return

        playSound(
            customSoundPath = settings.customSoundPath,
            volume = settings.volume,
            defaultResource = "/sounds/fahhh.wav",
        )
    }

    fun playSuccessSound() {
        val settings = BuildSoundSettings.getInstance().state
        if (!settings.successEnabled) return

        playSound(
            customSoundPath = settings.successCustomSoundPath,
            volume = settings.volume,
            defaultResource = "/sounds/drumroll.wav",
        )
    }

    fun playSound(customSoundPath: String, volume: Int, defaultResource: String = "/sounds/fahhh.wav") {
        Thread({
            try {
                val audio = getAudioStream(customSoundPath, defaultResource)
                if (audio != null) {
                    playStream(audio, volume)
                }
            } catch (e: Exception) {
                LOG.warn("Failed to play sound", e)
            }
        }, "FaFaFaFa-SoundPlayer").start()
    }

    private fun getAudioStream(customPath: String, defaultResource: String): AudioInputStream? {
        // Try custom sound file first
        if (customPath.isNotBlank()) {
            val file = File(customPath)
            if (file.exists() && file.canRead()) {
                return try {
                    AudioSystem.getAudioInputStream(BufferedInputStream(FileInputStream(file)))
                } catch (e: Exception) {
                    LOG.warn("Failed to load custom sound: $customPath", e)
                    null
                }
            }
        }

        // Try bundled sound
        val resource = SoundPlayer::class.java.getResourceAsStream(defaultResource)
        if (resource != null) {
            return try {
                AudioSystem.getAudioInputStream(BufferedInputStream(resource))
            } catch (e: Exception) {
                LOG.warn("Failed to load bundled sound", e)
                null
            }
        }

        return null
    }

    private fun playStream(audioStream: AudioInputStream, volume: Int) {
        val clip = AudioSystem.getClip()
        clip.open(audioStream)
        setVolume(clip, volume)
        clip.start()
        clip.addLineListener { event ->
            if (event.type == LineEvent.Type.STOP) {
                clip.close()
                audioStream.close()
            }
        }
    }

    private fun setVolume(clip: Clip, volume: Int) {
        try {
            val gainControl = clip.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
            val range = gainControl.maximum - gainControl.minimum
            val gain = gainControl.minimum + (range * volume / 100f)
            gainControl.value = gain.coerceIn(gainControl.minimum, gainControl.maximum)
        } catch (_: Exception) {
            // Volume control not available, play at default volume
        }
    }
}
