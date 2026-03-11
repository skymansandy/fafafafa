package com.fafafafa.buildsound.settings

import com.fafafafa.buildsound.player.SoundPlayer
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.TextBrowseFolderListener
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.TitledSeparator
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.JBUI
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JSlider

internal class BuildSoundConfigurable : Configurable {

    private var failureEnabledCheckBox: JBCheckBox? = null
    private var failureSoundFileField: TextFieldWithBrowseButton? = null
    private var focusOnFailureCheckBox: JBCheckBox? = null

    private var successEnabledCheckBox: JBCheckBox? = null
    private var successSoundFileField: TextFieldWithBrowseButton? = null
    private var focusOnSuccessCheckBox: JBCheckBox? = null

    private var volumeSlider: JSlider? = null

    override fun getDisplayName(): String = "FaFaFaFa"

    override fun createComponent(): JComponent {
        failureEnabledCheckBox = JBCheckBox("Enable build failure sound")

        failureSoundFileField = TextFieldWithBrowseButton().apply {
            addBrowseFolderListener(
                TextBrowseFolderListener(
                    FileChooserDescriptorFactory.createSingleFileDescriptor("wav")
                        .withTitle("Select Sound File")
                        .withDescription("Choose a .WAV file to play on build failures"),
                ),
            )
        }

        val failureButtonPanel = JPanel(FlowLayout(FlowLayout.TRAILING, 0, 0)).apply {
            add(
                JButton("Test Sound").apply {
                    addActionListener {
                        SoundPlayer.playSound(
                            customSoundPath = failureSoundFileField?.text ?: "",
                            volume = volumeSlider?.value ?: 100,
                            defaultResource = "/sounds/fahhh.wav",
                        )
                    }
                },
            )
            add(
                JButton("Reset to Default").apply {
                    addActionListener { failureSoundFileField?.text = "" }
                },
            )
        }

        focusOnFailureCheckBox = JBCheckBox("Focus IDE window on build failure")

        successEnabledCheckBox = JBCheckBox("Enable build success sound")

        successSoundFileField = TextFieldWithBrowseButton().apply {
            addBrowseFolderListener(
                TextBrowseFolderListener(
                    FileChooserDescriptorFactory.createSingleFileDescriptor("wav")
                        .withTitle("Select Sound File")
                        .withDescription("Choose a .WAV file to play on build success"),
                ),
            )
        }

        val successButtonPanel = JPanel(FlowLayout(FlowLayout.TRAILING, 0, 0)).apply {
            add(
                JButton("Test Sound").apply {
                    addActionListener {
                        SoundPlayer.playSound(
                            customSoundPath = successSoundFileField?.text ?: "",
                            volume = volumeSlider?.value ?: 100,
                            defaultResource = "/sounds/drumroll.wav",
                        )
                    }
                },
            )
            add(
                JButton("Reset to Default").apply {
                    addActionListener { successSoundFileField?.text = "" }
                },
            )
        }

        focusOnSuccessCheckBox = JBCheckBox("Focus IDE window on build success")

        volumeSlider = JSlider(0, 100).apply {
            majorTickSpacing = 25
            minorTickSpacing = 5
            paintTicks = true
            paintLabels = true
        }

        return FormBuilder.createFormBuilder()
            .addComponent(TitledSeparator("Build Failure Sound"))
            .addComponent(focusOnFailureCheckBox!!, JBUI.scale(4))
            .addComponent(failureEnabledCheckBox!!, JBUI.scale(4))
            .addLabeledComponent(JBLabel("Custom sound file (WAV):"), failureSoundFileField!!)
            .addTooltip("Leave empty to use the built-in sound (fahhh)")
            .addComponent(failureButtonPanel)
            .addComponent(TitledSeparator("Build Success Sound"), JBUI.scale(8))
            .addComponent(focusOnSuccessCheckBox!!, JBUI.scale(4))
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
        val settings = BuildSoundSettings.getInstance().state
        return failureEnabledCheckBox?.isSelected != settings.enabled ||
            failureSoundFileField?.text != settings.customSoundPath ||
            focusOnFailureCheckBox?.isSelected != settings.focusOnFailure ||
            successEnabledCheckBox?.isSelected != settings.successEnabled ||
            successSoundFileField?.text != settings.successCustomSoundPath ||
            focusOnSuccessCheckBox?.isSelected != settings.focusOnSuccess ||
            volumeSlider?.value != settings.volume
    }

    override fun apply() {
        val settings = BuildSoundSettings.getInstance().state
        settings.enabled = failureEnabledCheckBox?.isSelected ?: true
        settings.customSoundPath = failureSoundFileField?.text ?: ""
        settings.focusOnFailure = focusOnFailureCheckBox?.isSelected ?: false
        settings.successEnabled = successEnabledCheckBox?.isSelected ?: true
        settings.successCustomSoundPath = successSoundFileField?.text ?: ""
        settings.focusOnSuccess = focusOnSuccessCheckBox?.isSelected ?: false
        settings.volume = volumeSlider?.value ?: 100
    }

    override fun reset() {
        val settings = BuildSoundSettings.getInstance().state
        failureEnabledCheckBox?.isSelected = settings.enabled
        failureSoundFileField?.text = settings.customSoundPath
        focusOnFailureCheckBox?.isSelected = settings.focusOnFailure
        successEnabledCheckBox?.isSelected = settings.successEnabled
        successSoundFileField?.text = settings.successCustomSoundPath
        focusOnSuccessCheckBox?.isSelected = settings.focusOnSuccess
        volumeSlider?.value = settings.volume
    }

    override fun disposeUIResources() {
        failureEnabledCheckBox = null
        failureSoundFileField = null
        focusOnFailureCheckBox = null
        successEnabledCheckBox = null
        successSoundFileField = null
        focusOnSuccessCheckBox = null
        volumeSlider = null
    }
}
