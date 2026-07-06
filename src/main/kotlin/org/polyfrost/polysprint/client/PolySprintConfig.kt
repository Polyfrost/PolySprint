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
import net.minecraft.client.Minecraft
import org.polyfrost.oneconfig.api.config.v1.Config
import org.polyfrost.oneconfig.api.config.v1.annotations.Include
import org.polyfrost.oneconfig.api.config.v1.annotations.Keybind
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.ui.v1.keybind.KeybindHelper
import org.polyfrost.oneconfig.api.ui.v1.keybind.KeybindManager

object PolySprintConfig : Config(
    //VigilanceMigrator(File("./config/simpletogglesprint.toml").absolutePath),
    "polysprint.json",
    "/assets/polysprint/polysprint_dark.svg",
    "PolySprint",
    Category.COMBAT
) {
    @JvmStatic @Switch(title = "Enabled")
    var isEnabled = true

    @Switch(
        title = "Enable Toggle Sprint",
        subcategory = "Toggle Sprint"
    )
    var toggleSprint = true

    @Switch(
        title = "Enable Toggle Sneak",
        subcategory = "Toggle Sneak"
    )
    var toggleSneak = false

    @JvmStatic @Switch(title = "Disable W-Tap Sprint")
    var disableWTapSprint = false

    @JvmField
    @Include
    var toggleSprintState = false

    @JvmField
    @Include
    var toggleSneakState = false

    @Switch(
        title = "Separate Keybind for Toggle Sprint",
        subcategory = "Toggle Sprint",
        description = "Use a separate keybind for Toggle Sprint."
    )
    var keybindToggleSprint = false

    @Keybind(
        title = "Toggle Sprint Keybind",
        subcategory = "Toggle Sprint"
    )
    var keybindToggleSprintKey = KeybindHelper.builder().key(InputConstants.UNKNOWN.value).action { pressed: Boolean ->
        if (keybindToggleSprint && pressed && isEnabled && isToggleSprintEnabled) {
            invertToggleSprintState()
        }

        true
    }.build()

    @Switch(
        title = "Separate Keybind for Toggle Sneak",
        subcategory = "Toggle Sneak",
        description = "Use a separate keybind for Toggle Sneak."
    )
    var keybindToggleSneak = false

    @Keybind(
        title = "Toggle Sneak Keybind",
        subcategory = "Toggle Sneak"
    )
    var keybindToggleSneakKey = KeybindHelper.builder().key(InputConstants.UNKNOWN.value).action { pressed: Boolean ->
        if (keybindToggleSneak && pressed && isEnabled && isToggleSneakEnabled) {
            invertToggleSneakState()
        }

        true
    }.build()

    @Switch(
        title = "Fly Boost",
        subcategory = "Fly Boost"
    )
    var toggleFlyBoost = false

    @JvmStatic @Slider(
        title = "Fly Boost Amount",
        subcategory = "Fly Boost",
        min = 1.0F,
        max = 10.0F,
        step = 1.0F
    )
    var flyBoostAmount = 4.0F

    init {
        addDependency("keybindToggleSprint", "toggleSprint")
        addDependency("keybindToggleSneak", "toggleSneak")
        addDependency("flyBoostAmount", "toggleFlyBoost")
        addDependency("keybindToggleSprintKey", "keybindToggleSprint")
        addDependency("keybindToggleSneakKey", "keybindToggleSneak")
        addCallback("toggleSprint") { syncToggleSprintToVanilla() }
        addCallback("toggleSneak") { syncToggleSneakToVanilla() }
        addCallback("keybindToggleSprint") { syncToggleSprintToVanilla() }
        addCallback("keybindToggleSneak") { syncToggleSneakToVanilla() }

        KeybindManager.register(keybindToggleSprintKey)
        KeybindManager.register(keybindToggleSneakKey)
    }

    fun syncTogglesFromVanilla(persist: Boolean = false) {
        syncToggleSprintFromVanilla(persist)
        syncToggleSneakFromVanilla(persist)
    }

    fun syncTogglesToVanilla() {
        syncToggleSprintToVanilla()
        syncToggleSneakToVanilla()
    }

    fun syncToggleSprintFromVanilla(persist: Boolean = false) {
        if (keybindToggleSprint) return
        val options = Minecraft.getInstance().options ?: return
        val vanillaToggleSprint = options.toggleSprint().get()
        val changed = toggleSprint != vanillaToggleSprint

        toggleSprint = vanillaToggleSprint

        if (persist && changed) {
            save()
        }
    }

    fun syncToggleSneakFromVanilla(persist: Boolean = false) {
        if (keybindToggleSneak) return
        val options = Minecraft.getInstance().options ?: return
        val vanillaToggleSneak = options.toggleCrouch().get()
        val changed = toggleSneak != vanillaToggleSneak

        toggleSneak = vanillaToggleSneak

        if (persist && changed) {
            save()
        }
    }

    private fun syncToggleSprintToVanilla() {
        val options = Minecraft.getInstance().options ?: return
        options.toggleSprint().set(toggleSprint && !keybindToggleSprint)
        options.save()
    }

    private fun syncToggleSneakToVanilla() {
        val options = Minecraft.getInstance().options ?: return
        options.toggleCrouch().set(toggleSneak && !keybindToggleSneak)
        options.save()
    }

    fun reassertToggledKeys() {
        val options = Minecraft.getInstance().options ?: return
        if (keybindToggleSprint && toggleSprintState) {
            (options.keySprint as StickyKeyBindingSetter).`polySprint$toggle`(true)
        }
        if (keybindToggleSneak && toggleSneakState) {
            (options.keyShift as StickyKeyBindingSetter).`polySprint$toggle`(true)
        }
    }

    fun resyncSprintKeyState() {
        val options = Minecraft.getInstance().options ?: return
        (options.keySprint as StickyKeyBindingSetter).`polySprint$toggle`(toggleSprintState)
    }

    fun invertToggleSprintState() {
        toggleSprintState = !toggleSprintState
        (Minecraft.getInstance().options.keySprint as StickyKeyBindingSetter).`polySprint$toggle`(PolySprintConfig.toggleSprintState)
        save()
    }

    fun invertToggleSneakState() {
        toggleSneakState = !toggleSneakState
        (Minecraft.getInstance().options.keyShift as StickyKeyBindingSetter).`polySprint$toggle`(PolySprintConfig.toggleSneakState)
        save()
    }
}
