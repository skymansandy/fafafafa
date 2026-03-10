package com.fafafafa.buildsound.settings

import com.fafafafa.buildsound.BuildSoundSettings
import com.fafafafa.buildsound.player.SoundPlayer
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.TitledSeparator
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.JBUI
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JSlider

internal class BuildSoundSettings : Configurable {

    private var failureEnabledCheckBox: JBCheckBox? = null
    private var failureSoundFileField: TextFieldWithBrowseButton? = null

    private var successEnabledCheckBox: JBCheckBox? = null
    private var successSoundFileField: TextFieldWithBrowseButton? = null

    private var volumeSlider: JSlider? = null

    override fun getDisplayName(): String = "FaFaFaFa"

    override fun createComponent(): JComponent {

        failureEnabledCheckBox = JBCheckBox("Enable build failure sound")

        failureSoundFileField = TextFieldWithBrowseButton().apply {
            addBrowseFolderListener(
                "Select Sound File",
                "Choose a .WAV file to play on build failures",
                null,
                FileChooserDescriptorFactory.createSingleFileDescriptor("wav"),
            )
        }

        val failureTestButton = JButton("Test Sound").apply {
            addActionListener {
                SoundPlayer.playSound(
                    customSoundPath = failureSoundFileField?.text ?: "",
                    volume = volumeSlider?.value ?: 100,
                    defaultResource = "/sounds/fahhh.wav"
                )
            }
        }

        val failureResetButton = JButton("Reset to Default").apply {
            addActionListener { failureSoundFileField?.text = "" }
        }

        val failureButtonPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            add(failureTestButton)
            add(Box.createHorizontalStrut(8))
            add(failureResetButton)
        }

        successEnabledCheckBox = JBCheckBox("Enable build success sound")

        successSoundFileField = TextFieldWithBrowseButton().apply {
            addBrowseFolderListener(
                "Select Sound File",
                "Choose a .WAV file to play on build success",
                null,
                FileChooserDescriptorFactory.createSingleFileDescriptor("wav"),
            )
        }

        val successTestButton = JButton("Test Sound").apply {
            addActionListener {
                SoundPlayer.playSound(
                    customSoundPath = successSoundFileField?.text ?: "",
                    volume = volumeSlider?.value ?: 100,
                    defaultResource = "/sounds/drumroll.wav"
                )
            }
        }

        val successResetButton = JButton("Reset to Default").apply {
            addActionListener { successSoundFileField?.text = "" }
        }

        val successButtonPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            add(successTestButton)
            add(Box.createHorizontalStrut(8))
            add(successResetButton)
        }

        volumeSlider = JSlider(0, 100).apply {
            majorTickSpacing = 25
            minorTickSpacing = 5
            paintTicks = true
            paintLabels = true
        }

        return FormBuilder.createFormBuilder()
            .addComponent(TitledSeparator("Build Failure Sound"))
            .addComponent(failureEnabledCheckBox!!, JBUI.scale(4))
            .addLabeledComponent(JBLabel("Custom sound file (WAV):"), failureSoundFileField!!)
            .addTooltip("Leave empty to use the built-in sound (fahhh)")
            .addComponent(failureButtonPanel)
            .addComponent(TitledSeparator("Build Success Sound"), JBUI.scale(8))
            .addComponent(successEnabledCheckBox!!, JBUI.scale(4))
            .addLabeledComponent(JBLabel("Custom sound file (WAV):"), successSoundFileField!!)
            .addTooltip("Leave empty to use the built-in sound (drumroll)")
            .addComponent(successButtonPanel)
            .addSeparator(JBUI.scale(8))
            .addLabeledComponent(JBLabel("Volume:"), volumeSlider!!)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    override fun isModified(): Boolean {
        val settings = BuildSoundSettings.Companion.getInstance().state
        return failureEnabledCheckBox?.isSelected != settings.enabled
                || failureSoundFileField?.text != settings.customSoundPath
                || successEnabledCheckBox?.isSelected != settings.successEnabled
                || successSoundFileField?.text != settings.successCustomSoundPath
                || volumeSlider?.value != settings.volume
    }

    override fun apply() {
        val settings = BuildSoundSettings.Companion.getInstance().state
        settings.enabled = failureEnabledCheckBox?.isSelected ?: true
        settings.customSoundPath = failureSoundFileField?.text ?: ""
        settings.successEnabled = successEnabledCheckBox?.isSelected ?: true
        settings.successCustomSoundPath = successSoundFileField?.text ?: ""
        settings.volume = volumeSlider?.value ?: 100
    }

    override fun reset() {
        val settings = BuildSoundSettings.Companion.getInstance().state
        failureEnabledCheckBox?.isSelected = settings.enabled
        failureSoundFileField?.text = settings.customSoundPath
        successEnabledCheckBox?.isSelected = settings.successEnabled
        successSoundFileField?.text = settings.successCustomSoundPath
        volumeSlider?.value = settings.volume
    }

    override fun disposeUIResources() {
        failureEnabledCheckBox = null
        failureSoundFileField = null
        successEnabledCheckBox = null
        successSoundFileField = null
        volumeSlider = null
    }
}
