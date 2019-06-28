package com.almightyalpaca.jetbrains.plugins.discord.plugin.actions.items

import com.almightyalpaca.jetbrains.plugins.discord.plugin.actions.ActionComponent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.actions.ActionItem
import com.almightyalpaca.jetbrains.plugins.discord.plugin.actions.types.ToggleAction
import com.almightyalpaca.jetbrains.plugins.discord.plugin.icons.Icons
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings

class ProjectHideAction : ActionItem {
    override fun ActionComponent.create() {

        val hidden = ToggleAction.View {
            text = "Show project in Rich Presence"
            // description = ""
            icon = Icons.DISCORD_WHITE
            hoveredIcon = Icons.DISCORD_BLURPLE
        }

        val shown = ToggleAction.View {
            text = "Hide project in Rich Presence"
            // description = ""
            icon = Icons.DISCORD_BLURPLE
            hoveredIcon = Icons.DISCORD_WHITE
        }

        addAction(ToggleAction({ project -> project.settings.show }, shown, hidden))
    }
}