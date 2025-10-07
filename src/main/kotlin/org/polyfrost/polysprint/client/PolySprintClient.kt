/*
 * PolySprint - Toggle sprint and sneak with a keybind.
 *  Copyright (C) 2023  Polyfrost
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.polyfrost.polysprint.client

import dev.deftu.omnicore.api.client.commands.OmniClientCommands
import dev.deftu.omnicore.api.client.commands.command
import dev.deftu.omnicore.api.client.input.keybindings.OmniKeyBindings
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.KeyInputEvent
import org.polyfrost.oneconfig.api.event.v1.events.MouseInputEvent
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import org.polyfrost.oneconfig.utils.v1.dsl.createScreen
import org.polyfrost.polysprint.PolySprintConstants

object PolySprintClient {
    var isSprintHeld = false
        private set
    var isSneakHeld = false
        private set

    fun initialize() {
        PolySprintConfig.preload()
        HudManager.register(PolySprintHud())

        eventHandler { _: KeyInputEvent ->
            processInput()
        }.register()

        eventHandler { _: MouseInputEvent ->
            processInput()
        }.register()

        OmniClientCommands.command(PolySprintConstants.ID) {
            runs { ctx ->
                ctx.source.openScreen(PolySprintConfig.createScreen())
            }
        }.register()
    }

    @JvmStatic
    fun invertSprintHeld() {
        isSprintHeld = !isSprintHeld
    }

    @JvmStatic
    fun invertSneakHeld() {
        isSneakHeld = !isSneakHeld
    }

    private fun processInput() {
        if (!PolySprintConfig.isEnabled) {
            return
        }

        val sprintCode = OmniKeyBindings.sprint.boundValue
        if (!PolySprintConfig.keybindToggleSprint && sprintCode.isPressed) {
            if (isToggleSprintEnabled && !isSprintHeld) {
                PolySprintConfig.invertToggleSprintState()
            }

            isSprintHeld = true
        } else {
            isSprintHeld = false
        }

        val sneakCode = OmniKeyBindings.sneak.boundValue
        if (!PolySprintConfig.keybindToggleSneak && sneakCode.isPressed) {
            if (isToggleSneakEnabled && !isSneakHeld) {
                PolySprintConfig.invertToggleSneakState()
            }

            isSneakHeld = true
        } else {
            isSneakHeld = false
        }
    }
}
