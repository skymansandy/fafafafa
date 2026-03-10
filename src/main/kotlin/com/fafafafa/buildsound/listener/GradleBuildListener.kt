package com.fafafafa.buildsound.listener

import com.fafafafa.buildsound.player.SoundPlayer
import com.intellij.build.BuildViewManager
import com.intellij.build.events.FinishBuildEvent
import com.intellij.build.events.impl.FailureResultImpl
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

internal class BuildSoundStartupActivity : ProjectActivity {

    private val LOG = Logger.getInstance(BuildSoundStartupActivity::class.java)

    override suspend fun execute(project: Project) {
        LOG.info("FaFaFaFa: Registering build listener for project ${project.name}")
        val buildViewManager = project.getService(BuildViewManager::class.java) ?: return
        buildViewManager.addListener({ buildId, event ->
            if (event is FinishBuildEvent) {
                if (event.result is FailureResultImpl) {
                    LOG.info("Build finished with failure — playing FaFaFaFa failure sound")
                    SoundPlayer.playFailureSound()
                } else {
                    LOG.info("Build finished successfully — playing FaFaFaFa success sound")
                    SoundPlayer.playSuccessSound()
                }
            }
        }, project)
    }
}
