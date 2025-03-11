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

import dev.deftu.omnicore.client.OmniKeyboard
import org.polyfrost.oneconfig.api.config.v1.Config
import org.polyfrost.oneconfig.api.config.v1.annotations.Include
import org.polyfrost.oneconfig.api.config.v1.annotations.Keybind
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.ui.v1.keybind.KeybindManager.registerKeybind
import org.polyfrost.polyui.input.KeybindHelper

object PolySprintConfig : Config(
    //VigilanceMigrator(File("./config/simpletogglesprint.toml").absolutePath),
    "polysprint.json",
    "/polysprint_dark.svg",
    "PolySprint",
    Category.COMBAT
) {

    @Switch(title = "Toggle Sprint")
    var toggleSprint = true

    @Switch(title = "Toggle Sneak")
    var toggleSneak = false

    @Switch(title = "Disable W-Tap Sprint")
    var disableWTapSprint = true

    @JvmField
    @Include
    var toggleSprintState = false

    @JvmField
    @Include
    var toggleSneakState = false

    @Switch(
        title = "Seperate Keybind for Toggle Sprint",
        subcategory = "Toggle Sprint",
        description = "Use a seperate keybind for Toggle Sprint."
    )
    var keybindToggleSprint = false

    @Keybind(
        title = "Toggle Sprint Keybind",
        subcategory = "Toggle Sprint"
    )
    var keybindToggleSprintKey = KeybindHelper.builder().keys(OmniKeyboard.KEY_NONE).does {
        if (keybindToggleSprint) {
            if (enabled && toggleSprint && !PolySprint.sprintHeld) {
                toggleSprintState = !toggleSprintState
                PolySprintConfig.save()
            }
            PolySprint.sprintHeld = !PolySprint.sprintHeld
        }
    }.build()

    @Switch(
        title = "Seperate Keybind for Toggle Sneak",
        subcategory = "Toggle Sneak",
        description = "Use a seperate keybind for Toggle Sneak."
    )
    var keybindToggleSneak = false

    @Keybind(
        title = "Toggle Sneak Keybind",
        subcategory = "Toggle Sneak"
    )
    var keybindToggleSneakKey = KeybindHelper.builder().keys(OmniKeyboard.KEY_NONE).does {
        if (keybindToggleSneak) {
            if (enabled && toggleSneak && !PolySprint.sneakHeld) {
                toggleSneakState = !toggleSneakState
                PolySprintConfig.save()
            }
            PolySprint.sneakHeld = !PolySprint.sneakHeld
        }
    }.build()

    @Switch(
        title = "Fly Boost",
        subcategory = "Fly Boost"
    )
    var toggleFlyBoost = false

    @Slider(
        title = "Fly Boost Amount",
        subcategory = "Fly Boost",
        min = 1.0F,
        max = 10.0F
    )
    var flyBoostAmount = 4.0F

    init {
        addDependency("keybindToggleSprint", "toggleSprint")
        addDependency("keybindToggleSneak", "toggleSneak")
        addDependency("flyBoostAmount", "toggleFlyBoost")
        addDependency("keybindToggleSprintKey", "keybindToggleSprint")
        addDependency("keybindToggleSneakKey", "keybindToggleSneak")

        registerKeybind(keybindToggleSprintKey)
        registerKeybind(keybindToggleSneakKey)
    }
}
