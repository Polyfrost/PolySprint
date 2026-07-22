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

@file:JvmName("SprintState")

package org.polyfrost.polysprint.client

import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import org.polyfrost.oneconfig.api.hud.v1.HudManager

val isToggleSprintEnabled: Boolean
    get() {
        val client = Minecraft.getInstance()
        if (client.options == null) {
            return false
        }

        PolySprintConfig.syncToggleSprintFromVanilla()
        return PolySprintConfig.toggleSprint
    }

val isToggleSneakEnabled: Boolean
    get() {
        val client = Minecraft.getInstance()
        if (client.options == null) {
            return false
        }

        PolySprintConfig.syncToggleSneakFromVanilla()
        return PolySprintConfig.toggleSneak
    }

fun isSprintingToggled(keyBinding: KeyMapping, original: Boolean): Boolean {
    // Toggle sprint is currently on, so force sprinting regardless of the key's own state.
    if (isSprintToggleActive()) {
        return true
    }

    // Only take over the vanilla sprint key when a separate toggle keybind is in use. Toggle sprint keeps
    // vanilla's sticky toggleSprint enabled, so KeyMapping#isDown reports the sticky toggle rather than the
    // physical press; poll the physical key state to avoid inheriting that toggle.
    if (PolySprintConfig.isEnabled && PolySprintConfig.keybindToggleSprint && isToggleSprintEnabled) {
        return !isScreenOpen() && PolySprintClient.isKeyPhysicallyDown(keyBinding)
    }

    // PolySprint isn't driving this key; defer to the original behavior to stay compatible with other mods.
    return original
}

fun isSprintToggleActive(): Boolean {
    return !HudManager.isGuiScreenOpen && PolySprintConfig.isEnabled &&
            isToggleSprintEnabled && PolySprintConfig.toggleSprintState
}

fun isSneakingToggled(keyBinding: KeyMapping): Boolean {
    // With a separate toggle keybind, the vanilla sneak key must stay hold-to-sneak. Toggle sneak
    // keeps vanilla's sticky toggleCrouch enabled, so KeyMapping#isDown reports the sticky toggle
    // rather than the physical press; poll the physical key state to avoid inheriting that toggle.
    val held = if (PolySprintConfig.keybindToggleSneak) {
        !isScreenOpen() && PolySprintClient.isKeyPhysicallyDown(keyBinding)
    } else {
        keyBinding.isDown
    }
    if (held) {
        return true
    }

    return !HudManager.isGuiScreenOpen && PolySprintConfig.isEnabled &&
        isToggleSneakEnabled && PolySprintConfig.toggleSneakState
}


fun isFlyBoostEnabled(): Boolean {
    if (Minecraft.getInstance().options == null) {
        return false
    }

    return PolySprintConfig.isEnabled && PolySprintConfig.toggleFlyBoost
}

fun isFlyBoosting(): Boolean {
    val client = Minecraft.getInstance()
    val player = client.player ?: return false
    if (!PolySprintConfig.isEnabled || !PolySprintConfig.toggleFlyBoost) return false
    if (!player.abilities.flying || !player.abilities.instabuild) return false
    return PolySprintClient.isKeyPhysicallyDown(client.options.keySprint)
}

private fun isScreenOpen(): Boolean {
    //? if >=26.2 {
    /*return Minecraft.getInstance().gui.screen() != null
    *///?} else {
    return Minecraft.getInstance().screen != null
    //?}
}
