package com.fafafafa.buildsound

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@Service(Service.Level.APP)
@State(name = "FaFaFaFaSettings", storages = [Storage("fafafafa.xml")])
class BuildSoundSettings : PersistentStateComponent<BuildSoundSettings.State> {

    data class State(
        var enabled: Boolean = true,
        var customSoundPath: String = "",
        var volume: Int = 100
    )

    private var myState = State()

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    companion object {
        fun getInstance(): BuildSoundSettings =
            ApplicationManager.getApplication().getService(BuildSoundSettings::class.java)
    }
}
