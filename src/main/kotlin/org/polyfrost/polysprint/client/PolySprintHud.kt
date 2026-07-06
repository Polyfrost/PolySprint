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

import org.polyfrost.oneconfig.api.config.v1.annotations.Button
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.config.v1.annotations.Text
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import org.polyfrost.polyui.unit.fix

class PolySprintHud : TextHud(
    id = "togglesprint.json",
    title = "PolySprint State",
    category = Category.PLAYER,
    prefix = ""
) {
    private var isSneaking = false
    private var isFlying = false
    private var isSprinting = false
    private var isRiding = false

    @Switch(title = "Brackets")
    private var brackets = true

    @Button(
        title = "Reset Text on ALL HUDs",
        text = "Reset"
    )
    fun resetText() {
        descendingHeld = "Descending (key held)"
        descendingToggled = "Descending (toggled)"
        descending = "Descending (vanilla)"
        flying = "Flying"
        flyBoostText = "x boost"
        riding = "Riding"
        sneakHeld = "Sneaking (key held)"
        sneakToggle = "Sneaking (toggled)"
        sneak = "Sneaking (vanilla)"
        sprintHeld = "Sprinting (key held)"
        sprintToggle = "Sprinting (toggled)"
        sprint = "Sprinting (vanilla)"
    }

    @Text(
        title = "Descending Held Text",
        category = "Display",
        subcategory = "Text"
    )
    var descendingHeld = "Descending (key held)"

    @Text(
        title = "Descending Toggled Text",
        category = "Display",
        subcategory = "Text"
    )
    var descendingToggled = "Descending (toggled)"

    @Text(
        title = "Descending Text",
        category = "Display",
        subcategory = "Text"
    )
    var descending = "Descending (vanilla)"

    @Text(
        title = "Flying Text",
        category = "Display",
        subcategory = "Text"
    )
    var flying = "Flying"

    @Text(
        title = "Fly Boost Text",
        category = "Display",
        subcategory = "Text"
    )
    var flyBoostText = "x boost"

    @Text(
        title = "Riding Text",
        category = "Display",
        subcategory = "Text"
    )
    var riding = "Riding"

    @Text(
        title = "Sneak Held Text",
        category = "Display",
        subcategory = "Text"
    )
    var sneakHeld = "Sneaking (key held)"

    @Text(
        title = "Sneak Toggle Text",
        category = "Display",
        subcategory = "Text"
    )
    var sneakToggle = "Sneaking (toggled)"

    @Text(
        title = "Sneaking Text",
        category = "Display",
        subcategory = "Text"
    )
    var sneak = "Sneaking (vanilla)"

    @Text(
        title = "Sprint Held Text",
        category = "Display",
        subcategory = "Text"
    )
    var sprintHeld = "Sprinting (key held)"

    @Text(
        title = "Sprint Toggle Text",
        category = "Display",
        subcategory = "Text"
    )
    var sprintToggle = "Sprinting (toggled)"

    @Text(
        title = "Sprinting Text",
        category = "Display",
        subcategory = "Text"
    )
    var sprint = "Sprinting (vanilla)"

    override fun updateFrequency(): Long = 100_000_000L

    override fun setup() {
        super.setup()

        eventHandler { event: SprintStateEvent.Start ->
            when (event.type) {
                SprintStateEvent.Type.SNEAK -> isSneaking = true
                SprintStateEvent.Type.FLY -> isFlying = true
                SprintStateEvent.Type.RIDE -> isRiding = true
                SprintStateEvent.Type.SPRINT -> isSprinting = true
            }

            updateAndRecalculate()
        }.register()

        eventHandler { event: SprintStateEvent.End ->
            when (event.type) {
                SprintStateEvent.Type.SNEAK -> isSneaking = false
                SprintStateEvent.Type.FLY -> isFlying = false
                SprintStateEvent.Type.RIDE -> isRiding = false
                SprintStateEvent.Type.SPRINT -> isSprinting = false
            }

            updateAndRecalculate()
        }.register()
    }

    override fun getText(): String? {
        val sb = StringBuilder()

        if (brackets) {
            sb.append('[')
        }

        val config = PolySprintConfig
        if (isFlying) {
            if (isSneaking) {
                if (PolySprintClient.isSneakHeld) {
                    sb.append(descendingHeld)
                } else if (config.isEnabled && isToggleSneakEnabled && config.toggleSneakState) {
                    sb.append(descendingToggled)
                } else {
                    sb.append(descending)
                }
            } else {
                sb.append(flying)
                if (isFlyBoosting()) {
                    sb.append(' ').append(config.flyBoostAmount.fix(2)).append(flyBoostText)
                }
            }
        } else if (isRiding) {
            sb.append(riding)
        } else if (isSneaking || (config.isEnabled && isToggleSneakEnabled && config.toggleSneakState)) {
            if (PolySprintClient.isSneakHeld) {
                sb.append(sneakHeld)
            } else if (config.isEnabled && isToggleSneakEnabled && config.toggleSneakState) {
                sb.append(sneakToggle)
            } else {
                sb.append(sneak)
            }
        } else if (isSprinting || (config.isEnabled && isToggleSprintEnabled && config.toggleSprintState)) {
            if (PolySprintClient.isSprintHeld) {
                sb.append(sprintHeld)
            } else if (config.isEnabled && isToggleSprintEnabled && config.toggleSprintState) {
                sb.append(sprintToggle)
            } else {
                sb.append(sprint)
            }
        }

        if (brackets) {
            sb.append(']')
        }

        val isEmpty = sb.isEmpty() || (brackets && sb.length == 2)
        if (isEmpty && HudManager.isEditing) {
            sb.insert(if (brackets) 1 else 0, sprintToggle)
        }

        hidden = isEmpty && !HudManager.isEditing

        return sb.toString()
    }
}
