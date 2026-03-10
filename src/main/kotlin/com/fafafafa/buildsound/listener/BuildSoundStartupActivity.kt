package com.fafafafa.buildsound.listener

import com.fafafafa.buildsound.settings.BuildSoundSettings
import com.fafafafa.buildsound.player.SoundPlayer
import com.intellij.build.BuildViewManager
import com.intellij.build.events.FailureResult
import com.intellij.build.events.FinishBuildEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.wm.WindowManager

internal class BuildSoundStartupActivity : ProjectActivity {

    private val LOG = Logger.getInstance(BuildSoundStartupActivity::class.java)

    override suspend fun execute(project: Project) {
        LOG.info("FaFaFaFa: Registering build listener for project ${project.name}")
        val buildViewManager = project.getService(BuildViewManager::class.java) ?: return
        @Suppress("UnstableApiUsage")
        buildViewManager.addListener({ _, event ->
            if (event is FinishBuildEvent) {
                if (event.result is FailureResult) {
                    LOG.info("Build finished with failure — playing FaFaFaFa failure sound")
                    SoundPlayer.playFailureSound()
                    focusIdeIfEnabled(project, onFailure = true)
                } else {
                    LOG.info("Build finished successfully — playing FaFaFaFa success sound")
                    SoundPlayer.playSuccessSound()
                    focusIdeIfEnabled(project, onFailure = false)
                }
            }
        }, project)
    }

    private fun focusIdeIfEnabled(project: Project, onFailure: Boolean) {
        val state = BuildSoundSettings.getInstance().state
        val shouldFocus = if (onFailure) state.focusOnFailure else state.focusOnSuccess
        if (!shouldFocus) return
        ApplicationManager.getApplication().invokeLater {
            WindowManager.getInstance().getFrame(project)?.apply {
                isVisible = true
                toFront()
                requestFocus()
            }
        }
    }
}
