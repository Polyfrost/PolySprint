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
import org.polyfrost.oneconfig.api.ui.v1.keybind.BindNotInScreen
import org.polyfrost.oneconfig.api.ui.v1.keybind.KeybindHelper
import org.polyfrost.oneconfig.api.ui.v1.keybind.KeybindManager
import org.polyfrost.oneconfig.api.ui.v1.keybind.OneConfigKeybind

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

    // Registered separately from the config field via refreshSprintKeybind. A keybind's action is transient and is
    // lost whenever the field is loaded from disk, so the registered bind must be rebuilt from the loaded keys plus
    // this stable action rather than registering the field object directly.
    private val sprintToggleAction: (Boolean) -> Boolean = { pressed ->
        if (keybindToggleSprint && pressed && isEnabled && isToggleSprintEnabled) {
            invertToggleSprintState()
        }

        true
    }

    @Keybind(
        title = "Toggle Sprint Keybind",
        subcategory = "Toggle Sprint"
    )
    var keybindToggleSprintKey = KeybindHelper.builder().key(InputConstants.UNKNOWN.value).action { _: Boolean -> true }.build()

    @Switch(
        title = "Separate Keybind for Toggle Sneak",
        subcategory = "Toggle Sneak",
        description = "Use a separate keybind for Toggle Sneak."
    )
    var keybindToggleSneak = false

    private val sneakToggleAction: (Boolean) -> Boolean = { pressed ->
        if (keybindToggleSneak && pressed && isEnabled && isToggleSneakEnabled) {
            invertToggleSneakState()
        }

        true
    }

    @Keybind(
        title = "Toggle Sneak Keybind",
        subcategory = "Toggle Sneak"
    )
    var keybindToggleSneakKey = KeybindHelper.builder().key(InputConstants.UNKNOWN.value).action { _: Boolean -> true }.build()

    private var registeredSprintKeybind: OneConfigKeybind? = null
    private var registeredSneakKeybind: OneConfigKeybind? = null

    @Switch(
        title = "Show Sprint Text while Flying",
        subcategory = "Flying"
    )
    var showSprintTextWhileFlying = true

    @Switch(
        title = "Unsneak on Flight Start",
        subcategory = "Flying"
    )
    var unsneakOnFlightStart = true

    @Switch(
        title = "Fly Boost",
        subcategory = "Flying"
    )
    var toggleFlyBoost = false

    @JvmStatic @Slider(
        title = "Fly Boost Amount",
        subcategory = "Flying",
        min = 1.0F,
        max = 10.0F,
        step = 1.0F
    )
    var flyBoostAmount = 4.0F

    init {
        addDependency("keybindToggleSprint", "toggleSprint")
        addDependency("keybindToggleSprintKey", "toggleSprint")
        addDependency("keybindToggleSprintKey", "keybindToggleSprint")

        addDependency("keybindToggleSneak", "toggleSneak")
        addDependency("keybindToggleSneakKey", "toggleSneak")
        addDependency("keybindToggleSneakKey", "keybindToggleSneak")

        addDependency("flyBoostAmount", "toggleFlyBoost")

        addCallback("toggleSprint") { syncToggleSprintToVanilla() }
        addCallback("toggleSneak") { syncToggleSneakToVanilla() }

        addCallback("keybindToggleSprintKey") { refreshSprintKeybind() }
        addCallback("keybindToggleSneakKey") { refreshSneakKeybind() }
        // The disk value has already been loaded by the addDependency calls above, so refresh once now to register
        // the saved keys; the callbacks keep them in sync on subsequent rebinds.
        refreshSprintKeybind()
        refreshSneakKeybind()
    }

    private fun refreshSprintKeybind() {
        registeredSprintKeybind = replaceRegistered(registeredSprintKeybind, keybindToggleSprintKey, sprintToggleAction)
    }

    private fun refreshSneakKeybind() {
        registeredSneakKeybind = replaceRegistered(registeredSneakKeybind, keybindToggleSneakKey, sneakToggleAction)
    }

    /**
     * Unregisters [old] and, if [src] is bound, registers a fresh keybind built from [src]'s keys and [action],
     * returning the newly registered keybind (or `null` if unbound). The action is supplied here rather than read
     * from [src] because a keybind's action is transient and is null once [src] has been loaded from disk.
     */
    private fun replaceRegistered(
        old: OneConfigKeybind?,
        src: OneConfigKeybind,
        action: (Boolean) -> Boolean
    ): OneConfigKeybind? {
        old?.let { KeybindManager.unregister(it) }
        if (!src.isBound) return null
        return BindNotInScreen(src.keyCodes, src.mouseBtns, src.mods, src.durationNanos, action)
            .also { KeybindManager.register(it) }
    }

    fun syncTogglesFromVanilla(persist: Boolean = false) {
        syncToggleSprintFromVanilla(persist)
        syncToggleSneakFromVanilla(persist)
    }

    fun syncToggleSprintFromVanilla(persist: Boolean = false) {
        val options = Minecraft.getInstance().options ?: return
        val vanillaToggleSprint = options.toggleSprint().get()
        val changed = toggleSprint != vanillaToggleSprint

        toggleSprint = vanillaToggleSprint

        if (persist && changed) {
            save()
        }
    }

    fun syncToggleSneakFromVanilla(persist: Boolean = false) {
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
        options.toggleSprint().set(toggleSprint)
        options.save()
    }

    private fun syncToggleSneakToVanilla() {
        val options = Minecraft.getInstance().options ?: return
        options.toggleCrouch().set(toggleSneak)
        options.save()
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
