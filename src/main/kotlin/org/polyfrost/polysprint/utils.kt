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

package org.polyfrost.polysprint

import dev.deftu.omnicore.client.*
import net.minecraft.client.settings.KeyBinding
import org.polyfrost.oneconfig.api.hypixel.v1.HypixelUtils

fun shouldSetSprint(keyBinding: KeyBinding): Boolean {
    return keyBinding.isKeyDown || !OmniScreen.isInScreen && PolySprintConfig.enabled && PolySprintConfig.toggleSprint && PolySprintConfig.toggleSprintState
}

fun shouldSetSneak(keyBinding: KeyBinding): Boolean {
    return keyBinding.isKeyDown || !OmniScreen.isInScreen && PolySprintConfig.enabled && PolySprintConfig.toggleSneak && PolySprintConfig.toggleSneakState
}

fun shouldFlyBoost(): Boolean {
    val player = OmniClientPlayer.getInstance() ?: return false
    return OmniClient.getInstance().gameSettings.keyBindSprint.isKeyDown && PolySprintConfig.enabled && PolySprintConfig.toggleFlyBoost && player.capabilities.isFlying && player.capabilities.isCreativeMode && !HypixelUtils.isHypixel()
}

fun checkKeyCode(keyCode: Int): Boolean {
    return if (keyCode > 0) {
        OmniKeyboard.isPressed(keyCode)
    } else {
        OmniMouse.isPressed(keyCode + 100)
    }
}
