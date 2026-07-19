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

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import org.lwjgl.glfw.GLFW
import org.polyfrost.oneconfig.api.commands.v1.CommandManager
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
    private var initialized = false
    private var sprintLastPhysicallyDown = false
    private var sneakLastPhysicallyDown = false

    private var lastFlying = false

    fun initialize() {
        PolySprintConfig.preload()
        PolySprintConfig.syncTogglesFromVanilla()
        HudManager.register(PolySprintHud(), PolySprintConstants.ID, "assets/polysprint/polysprint_dark.svg")

        eventHandler { _: KeyInputEvent ->
            processInput()
        }.register()

        eventHandler { _: MouseInputEvent ->
            processInput()
        }.register()

        eventHandler { event: SprintStateEvent.End ->
            if (event.type == SprintStateEvent.Type.FLY && PolySprintConfig.toggleFlyBoost) {
                PolySprintConfig.resyncSprintKeyState()
            }
        }.register()

        CommandManager.register(CommandManager.literal(PolySprintConstants.ID).executes {
            PolySprintConfig.syncTogglesFromVanilla()
            //? if =26.2 {
            /*Minecraft.getInstance().setScreenAndShow(PolySprintConfig.createScreen())
            *///?} else {
            Minecraft.getInstance().setScreen(PolySprintConfig.createScreen())
            //?}
            1
        })

        initialized = true
    }

    @JvmStatic
    fun syncTogglesFromVanillaOptions() {
        if (!initialized) {
            return
        }

        PolySprintConfig.syncTogglesFromVanilla(persist = true)
    }

    private fun processInput() {
        if (!PolySprintConfig.isEnabled) {
            return
        }

        if (HudManager.isGuiScreenOpen) {
            return
        }

        val flying = Minecraft.getInstance().player?.abilities?.flying == true

        if (lastFlying != flying) {
            if (PolySprintConfig.toggleSneakState && PolySprintConfig.unsneakOnFlightStart)
                PolySprintConfig.invertToggleSneakState()

            lastFlying = flying
        }

        val sprintKey = Minecraft.getInstance().options.keySprint
        val sprintPhysicallyDown = sprintKey.isPhysicallyDown()

        if (!flying
            && isToggleSprintEnabled
            && !PolySprintConfig.keybindToggleSprint
            && sprintPhysicallyDown
            && !sprintLastPhysicallyDown
        ) PolySprintConfig.invertToggleSprintState()

        sprintLastPhysicallyDown = sprintPhysicallyDown
        isSprintHeld = (!isToggleSprintEnabled || PolySprintConfig.keybindToggleSprint) && sprintPhysicallyDown

        val sneakKey = Minecraft.getInstance().options.keyShift
        val sneakPhysicallyDown = sneakKey.isPhysicallyDown()

        if (isToggleSneakEnabled
            && !PolySprintConfig.keybindToggleSneak
            && sneakPhysicallyDown
            && !sneakLastPhysicallyDown
        ) PolySprintConfig.invertToggleSneakState()

        sneakLastPhysicallyDown = sneakPhysicallyDown
        isSneakHeld = (!isToggleSneakEnabled || PolySprintConfig.keybindToggleSneak) && sneakPhysicallyDown
    }

    @JvmStatic
    fun isKeyPhysicallyDown(key: KeyMapping): Boolean = key.isPhysicallyDown()

    private fun KeyMapping.isPhysicallyDown(): Boolean {
        val key = InputConstants.getKey(saveString())
        return when (key.type) {
            InputConstants.Type.KEYSYM, InputConstants.Type.SCANCODE ->
                //? if <1.21.10
                /*InputConstants.isKeyDown(Minecraft.getInstance().window.window, key.value)*/
                //? if >=1.21.10
                InputConstants.isKeyDown(Minecraft.getInstance().window, key.value)

            InputConstants.Type.MOUSE ->
                //? if <1.21.10
                /*GLFW.glfwGetMouseButton(Minecraft.getInstance().window.window, key.value) == GLFW.GLFW_PRESS*/
                //? if >=1.21.10
                GLFW.glfwGetMouseButton(Minecraft.getInstance().window.handle(), key.value) == GLFW.GLFW_PRESS

            else -> false
        }
    }
}
