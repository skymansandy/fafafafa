package com.fafafafa.buildsound.listener

import com.fafafafa.buildsound.BuildSoundSettings
import com.fafafafa.buildsound.player.SoundPlayer
import com.intellij.build.BuildViewManager
import com.intellij.build.events.FinishBuildEvent
import com.intellij.build.events.impl.FailureResultImpl
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
        buildViewManager.addListener({ _, event ->
            if (event is FinishBuildEvent) {
                if (event.result is FailureResultImpl) {
                    LOG.info("Build finished with failure — playing FaFaFaFa failure sound")
                    SoundPlayer.playFailureSound()
                    focusIdeIfEnabled(project)
                } else {
                    LOG.info("Build finished successfully — playing FaFaFaFa success sound")
                    SoundPlayer.playSuccessSound()
                }
            }
        }, project)
    }

    private fun focusIdeIfEnabled(project: Project) {
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
