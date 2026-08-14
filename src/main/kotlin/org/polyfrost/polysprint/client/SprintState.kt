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
    if (isSprintToggleActive()) {
        return true
    }

    // Vanilla sticky toggleSprint stays on so KeyMapping#isDown reports the toggle and not the press
    // Poll the physical key instead
    if (PolySprintConfig.isEnabled && PolySprintConfig.keybindToggleSprint && isToggleSprintEnabled) {
        return !isScreenOpen() && PolySprintClient.isKeyPhysicallyDown(keyBinding)
    }

    return original
}

fun isSprintToggleActive(): Boolean {
    return !HudManager.isGuiScreenOpen && PolySprintConfig.isEnabled &&
            isToggleSprintEnabled && PolySprintConfig.toggleSprintState
}

fun isSneakingToggled(keyBinding: KeyMapping): Boolean {
    // Vanilla sticky toggleCrouch stays on so KeyMapping#isDown reports the toggle and not the press
    // Poll the physical key to keep the vanilla sneak key hold-to-sneak
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
    val client = Minecraft.getInstance()
    if (client.options == null) {
        return false
    }

    return PolySprintConfig.isEnabled &&
        PolySprintConfig.toggleFlyBoost &&
        !client.isMultiplayerServer()
}

fun isFlyBoostUsingSprintKey(): Boolean {
    val player = Minecraft.getInstance().player ?: return false
    return isFlyBoostEnabled() && player.abilities.flying && player.abilities.instabuild
}

fun isFlyBoosting(): Boolean {
    if (!isFlyBoostUsingSprintKey()) return false
    return PolySprintClient.isKeyPhysicallyDown(Minecraft.getInstance().options.keySprint)
}

private fun isScreenOpen(): Boolean {
    //? if >=26.2 {
    return Minecraft.getInstance().gui.screen() != null
    //?} else {
    /*return Minecraft.getInstance().screen != null
    *///?}
}
