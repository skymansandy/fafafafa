package com.fafafafa.buildsound.settings

import com.fafafafa.buildsound.BuildSoundSettings
import com.fafafafa.buildsound.player.SoundPlayer
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JSlider

internal class BuildSoundSettings : Configurable {

    private var enabledCheckBox: JBCheckBox? = null
    private var soundFileField: TextFieldWithBrowseButton? = null
    private var volumeSlider: JSlider? = null
    private var testButton: JButton? = null
    private var resetButton: JButton? = null

    override fun getDisplayName(): String = "FaFaFaFa"

    override fun createComponent(): JComponent {

        enabledCheckBox = JBCheckBox("Enable build failure sound")

        soundFileField = TextFieldWithBrowseButton().apply {
            addBrowseFolderListener(
                "Select Sound File",
                "Choose a .WAV file to play on build failures",
                null,
                FileChooserDescriptorFactory.createSingleFileDescriptor("wav"),
            )
        }

        volumeSlider = JSlider(0, 100).apply {
            majorTickSpacing = 25
            minorTickSpacing = 5
            paintTicks = true
            paintLabels = true
        }

        testButton = JButton("Test Sound").apply {
            addActionListener {
                SoundPlayer.playSound(
                    customSoundPath = soundFileField?.text ?: "",
                    volume = volumeSlider?.value ?: 100
                )
            }
        }

        resetButton = JButton("Reset to Default").apply {
            addActionListener { soundFileField?.text = "" }
        }

        val buttonPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            add(testButton!!)
            add(Box.createHorizontalStrut(8))
            add(resetButton!!)
        }

        return FormBuilder.createFormBuilder()
            .addComponent(enabledCheckBox!!)
            .addSeparator()
            .addLabeledComponent(JBLabel("Custom sound file (WAV):"), soundFileField!!)
            .addTooltip("Leave empty to use the built-in sound")
            .addLabeledComponent(JBLabel("Volume:"), volumeSlider!!)
            .addComponent(buttonPanel)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    override fun isModified(): Boolean {
        val settings = BuildSoundSettings.Companion.getInstance().state
        return enabledCheckBox?.isSelected != settings.enabled
                || soundFileField?.text != settings.customSoundPath
                || volumeSlider?.value != settings.volume
    }

    override fun apply() {
        val settings = BuildSoundSettings.Companion.getInstance().state
        settings.enabled = enabledCheckBox?.isSelected ?: true
        settings.customSoundPath = soundFileField?.text ?: ""
        settings.volume = volumeSlider?.value ?: 100
    }

    override fun reset() {
        val settings = BuildSoundSettings.Companion.getInstance().state
        enabledCheckBox?.isSelected = settings.enabled
        soundFileField?.text = settings.customSoundPath
        volumeSlider?.value = settings.volume
    }

    override fun disposeUIResources() {
        enabledCheckBox = null
        soundFileField = null
        volumeSlider = null
        testButton = null
        resetButton = null
    }
}