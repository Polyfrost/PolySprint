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

import dev.deftu.omnicore.api.client.input.OmniKeys
import org.polyfrost.oneconfig.api.config.v1.Config
import org.polyfrost.oneconfig.api.config.v1.annotations.Include
import org.polyfrost.oneconfig.api.config.v1.annotations.Keybind
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.ui.v1.keybind.KeybindManager
import org.polyfrost.polyui.input.KeybindHelper

//#if MC >= 1.16.5
//$$ import dev.deftu.omnicore.api.client.client
//#endif

object PolySprintConfig : Config(
    //VigilanceMigrator(File("./config/simpletogglesprint.toml").absolutePath),
    "polysprint.json",
    "/assets/polysprint/polysprint_dark.svg",
    "PolySprint",
    Category.COMBAT
) {
    @JvmStatic @Switch(title = "Enabled")
    var isEnabled = true

    //#if MC <= 1.12.2
    @Switch(title = "Toggle Sprint")
    var toggleSprint = true

    @Switch(title = "Toggle Sneak")
    var toggleSneak = false
    //#endif

    @JvmStatic @Switch(title = "Disable W-Tap Sprint")
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
    var keybindToggleSprintKey = KeybindHelper.builder().keys(OmniKeys.KEY_NONE.code).does {
        if (keybindToggleSprint) {
            if (isEnabled && isToggleSprintEnabled && !PolySprintClient.isSprintHeld) {
                invertToggleSprintState()
            }

            PolySprintClient.invertSprintHeld()
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
    var keybindToggleSneakKey = KeybindHelper.builder().keys(OmniKeys.KEY_NONE.code).does {
        if (keybindToggleSneak) {
            if (isEnabled && isToggleSneakEnabled && !PolySprintClient.isSneakHeld) {
                invertToggleSneakState()
            }

            PolySprintClient.invertSneakHeld()
        }
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
        max = 10.0F
    )
    var flyBoostAmount = 4.0F

    init {
        //#if MC >= 1.16.5
        //$$ addDependency("keybindToggleSprint", null) { if (isToggleSprintEnabled) Property.Display.SHOWN else Property.Display.DISABLED }
        //$$ addDependency("keybindToggleSneak", null) { if (isToggleSneakEnabled) Property.Display.SHOWN else Property.Display.DISABLED }
        //#else
        addDependency("keybindToggleSprint", "toggleSprint")
        addDependency("keybindToggleSneak", "toggleSneak")
        //#endif
        addDependency("flyBoostAmount", "toggleFlyBoost")
        addDependency("keybindToggleSprintKey", "keybindToggleSprint")
        addDependency("keybindToggleSneakKey", "keybindToggleSneak")

        KeybindManager.registerKeybind(keybindToggleSprintKey)
        KeybindManager.registerKeybind(keybindToggleSneakKey)
    }

    fun invertToggleSprintState() {
        toggleSprintState = !toggleSprintState
        //#if MC >= 1.16.5
        //$$ (client.options.keySprint as StickyKeyBindingSetter).toggle(PolySprintConfig.toggleSprintState)
        //#endif
        save()
    }

    fun invertToggleSneakState() {
        toggleSneakState = !toggleSneakState
        //#if MC >= 1.16.5
        //$$ (client.options.keyShift as StickyKeyBindingSetter).toggle(PolySprintConfig.toggleSneakState)
        //#endif
        save()
    }
}