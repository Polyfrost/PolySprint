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

fun isSprintingToggled(keyBinding: KeyMapping? = null): Boolean {
    return optionallyStateful(keyBinding) {
        isToggleSprintEnabled && PolySprintConfig.toggleSprintState
    }
}

fun isSneakingToggled(keyBinding: KeyMapping): Boolean {
    return optionallyStateful(keyBinding) {
        isToggleSneakEnabled && PolySprintConfig.toggleSneakState
    }
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

private fun optionallyStateful(keyBinding: KeyMapping?, consumer: () -> Boolean): Boolean {
    if (keyBinding != null && keyBinding.isDown) {
        return true
    }

    return !HudManager.isGuiScreenOpen && PolySprintConfig.isEnabled && consumer()
}
