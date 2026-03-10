package com.fafafafa.buildsound.listener

import com.fafafafa.buildsound.BuildSoundSettings
import com.fafafafa.buildsound.player.SoundPlayer
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.WindowManager
import com.intellij.task.ProjectTaskListener
import com.intellij.task.ProjectTaskManager

internal class BuildFailureSoundPlayer(private val project: Project) : ProjectTaskListener {

    private val LOG = Logger.getInstance(BuildFailureSoundPlayer::class.java)

    override fun finished(result: ProjectTaskManager.Result) {
        if (result.hasErrors() || result.isAborted) {
            LOG.info("Build failed — playing FaFaFaFa failure sound")
            SoundPlayer.playFailureSound()
            focusIdeIfEnabled()
        } else {
            LOG.info("Build succeeded — playing FaFaFaFa success sound")
            SoundPlayer.playSuccessSound()
        }
    }

    private fun focusIdeIfEnabled() {
        if (!BuildSoundSettings.getInstance().state.focusOnFailure) return
        ApplicationManager.getApplication().invokeLater {
            WindowManager.getInstance().getFrame(project)?.apply {
                isVisible = true
                toFront()
                requestFocus()
            }
        }
    }
}