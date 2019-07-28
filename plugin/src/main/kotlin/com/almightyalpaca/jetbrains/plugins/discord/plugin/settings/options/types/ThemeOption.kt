/*
 * Copyright 2017-2019 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types

import com.almightyalpaca.jetbrains.plugins.discord.plugin.components.ApplicationComponent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.gui.themes.ThemeDialog
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionCreator
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionHolder
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.impl.OptionProviderImpl
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.gbc
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.label
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.throwing
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Source
import com.intellij.openapi.util.JDOMExternalizerUtil
import kotlinx.coroutines.future.asCompletableFuture
import org.jdom.Element
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.Box
import javax.swing.JButton
import javax.swing.JPanel
import kotlin.reflect.KProperty

fun OptionCreator<in ThemeValue>.themeChooser(description: String) = OptionProviderImpl(this, ThemeOption(description))

class ThemeOption(description: String) : Option<ThemeValue>(description), ThemeValue.Provider {
    private val source: Source = ApplicationComponent.instance.source

    private val listeners = mutableListOf<(ThemeValue) -> Unit>()
    override fun addChangeListener(listener: (ThemeValue) -> Unit) {
        listeners += listener
    }

    var currentValue: String? = null
    var componentValue: String? = null
        private set

    private val componentImpl = JButton().apply button@{
        isEnabled = false
        text = "Loading..."

        addActionListener {
            val themes = source.getThemesOrNull()

            if (themes != null) {
                val dialog = ThemeDialog(themes, componentValue)
                val result = dialog.showAndGet()

                if (result) {
                    componentValue = dialog.value
                    text = themes[componentValue]!!.name
                }
            }
        }
    }

    override val component by lazy {
        JPanel().apply {
            layout = GridBagLayout()

            add(label(description), gbc {
                gridx = 0
                gridy = 0
                gridwidth = 1
                gridheight = 1
                anchor = GridBagConstraints.WEST
            })

            add(componentImpl, gbc {
                gridx = 1
                gridy = 0
                gridwidth = 1
                gridheight = 1
                anchor = GridBagConstraints.WEST
            })

            add(Box.createHorizontalGlue(), gbc {
                gridx = 2
                gridy = 0
                gridwidth = 1
                gridheight = 1
                anchor = GridBagConstraints.WEST
                fill = GridBagConstraints.HORIZONTAL
                weightx = 1.0
            })
        }
    }

    override var isComponentEnabled by throwing<Boolean> { UnsupportedOperationException() } // TODO

    init {
        source.getThemesAsync().asCompletableFuture().thenAcceptAsync { themes ->
            if (this.currentValue == null || currentValue !in themes.keys) {
                currentValue = themes.default.id
            }

            componentValue = currentValue

            componentImpl.isEnabled = true
            componentImpl.text = themes[componentValue]!!.name
        }
    }

    private val value = ThemeValue(this)
    override fun getValue(thisRef: OptionHolder, property: KProperty<*>) = value

    override val isModified: Boolean
        get() = currentValue != componentValue

    override val isDefault: Boolean
        get() = source.getThemesOrNull()?.default?.id?.equals(currentValue) ?: false

    override fun apply() {
        currentValue = componentValue
    }

    override fun reset() {
        componentValue = currentValue
    }

    override fun writeXml(element: Element, key: String) {
        JDOMExternalizerUtil.writeField(element, key, currentValue)
    }

    override fun readXml(element: Element, key: String) {
        JDOMExternalizerUtil.readField(element, key)?.let { s -> currentValue = s }
    }
}

class ThemeValue(private val option: ThemeOption) : SimpleValue<String?>() {
    override fun get() = option.currentValue
    override fun getComponent() = option.componentValue
    override fun set(value: String?) {
        option.currentValue = value
    }

    override val description: String
        get() = option.description

    interface Provider : SimpleValue.Provider<String?> {
        override fun getValue(thisRef: OptionHolder, property: KProperty<*>): ThemeValue
    }
}
