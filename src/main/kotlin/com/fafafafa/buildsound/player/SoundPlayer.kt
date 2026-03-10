package com.fafafafa.buildsound.player

import com.fafafafa.buildsound.BuildSoundSettings
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

        playSound(settings.customSoundPath, settings.volume)
    }

    fun playSound(customSoundPath: String, volume: Int) {
        Thread({
            try {
                val errorAudio = getAudioStream(customSoundPath)
                if (errorAudio != null) {
                    playStream(errorAudio, volume)
                }
            } catch (e: Exception) {
                LOG.warn("Failed to play build failure sound", e)
            }
        }, "FaFaFaFa-SoundPlayer").start()
    }

    private fun getAudioStream(customPath: String): AudioInputStream? {
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
        val resource = SoundPlayer::class.java.getResourceAsStream("/sounds/fahhh.wav")
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