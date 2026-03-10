package com.fafafafa.buildsound.listener

import com.fafafafa.buildsound.player.SoundPlayer
import com.intellij.openapi.diagnostic.Logger
import com.intellij.task.ProjectTaskListener
import com.intellij.task.ProjectTaskManager

internal class BuildFailureSoundPlayer : ProjectTaskListener {

    private val LOG = Logger.getInstance(BuildFailureSoundPlayer::class.java)

    override fun finished(result: ProjectTaskManager.Result) {
        if (result.hasErrors() || result.isAborted) {
            LOG.info("Build failed — playing FaFaFaFa failure sound")
            SoundPlayer.playFailureSound()
        } else {
            LOG.info("Build succeeded — playing FaFaFaFa success sound")
            SoundPlayer.playSuccessSound()
        }
    }
}