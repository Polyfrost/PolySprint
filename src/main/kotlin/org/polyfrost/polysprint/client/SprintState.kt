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

import dev.deftu.omnicore.api.client.input.keybindings.OmniKeyBindings
import dev.deftu.omnicore.api.client.player
import dev.deftu.omnicore.api.client.screen.isInScreen
import dev.deftu.omnicore.api.client.client
import dev.deftu.omnicore.api.client.options.OmniKeyboardSettings
import net.minecraft.client.KeyMapping

val isToggleSprintEnabled: Boolean
    get() {
        if (client.options == null) {
            return false
        }

        return OmniKeyboardSettings.isToggleSprintEnabled
    }

val isToggleSneakEnabled: Boolean
    get() {
        if (client.options == null) {
            return false
        }

        return OmniKeyboardSettings.isToggleSneakEnabled
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


// TODO: this isnt always correct
fun isFlyBoosting(): Boolean {
    val player = player ?: return false
    val sprintKey = OmniKeyBindings.sprint
    return sprintKey.isPressed && PolySprintConfig.isEnabled && PolySprintConfig.toggleFlyBoost && player.abilities.flying && player.abilities.instabuild
}

private fun optionallyStateful(keyBinding: KeyMapping?, consumer: () -> Boolean): Boolean {
    if (keyBinding != null && keyBinding.isDown) {
        return true
    }

    return !isInScreen && PolySprintConfig.isEnabled && consumer()
}
