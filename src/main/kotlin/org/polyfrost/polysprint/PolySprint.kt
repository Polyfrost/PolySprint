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

//#if FORGE
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
//#else
//$$ import net.fabricmc.api.ClientModInitializer
//$$ import net.legacyfabric.fabric.api.client.event.lifecycle.v1.ClientTickEvents
//#endif

import dev.deftu.omnicore.client.OmniClient
import org.polyfrost.oneconfig.api.commands.v1.CommandManager
import org.polyfrost.oneconfig.api.hud.v1.HudManager

//#if FORGE
@Mod(modid = PolySprint.ID, version = PolySprint.VERSION, name = PolySprint.NAME, modLanguageAdapter = "org.polyfrost.oneconfig.utils.v1.forge.KotlinLanguageAdapter")
//#endif
object PolySprint
    //#if FABRIC
    //$$ : ClientModInitializer
    //#endif
{

    const val ID = "@MOD_ID@"
    const val NAME = "@MOD_NAME@"
    const val VERSION = "@MOD_VERSION@"

    var sprintHeld = false
    var sneakHeld = false

    private fun initialize() {
        PolySprintConfig
    }

    private fun postInitialize() {
        HudManager.register(PolySprintHud())
        CommandManager.register(PolySprintCommand())
    }

    private fun handleInput() {
        if (!PolySprintConfig.enabled) {
            return
        }

        val options = OmniClient.getInstance().gameSettings
        val sprint = options.keyBindSprint.keyCode
        val sneak = options.keyBindSneak.keyCode

        if (!PolySprintConfig.keybindToggleSprint && checkKeyCode(sprint)) {
            if (PolySprintConfig.enabled && PolySprintConfig.toggleSprint && !sprintHeld) {
                PolySprintConfig.toggleSprintState = !PolySprintConfig.toggleSprintState
                PolySprintConfig.save()
            }
            sprintHeld = true
        } else {
            sprintHeld = false
        }

        if (!PolySprintConfig.keybindToggleSneak && checkKeyCode(sneak)) {
            if (PolySprintConfig.enabled && PolySprintConfig.toggleSneak && !sneakHeld) {
                PolySprintConfig.toggleSneakState = !PolySprintConfig.toggleSneakState
                PolySprintConfig.save()
            }
            sneakHeld = true
        } else {
            sneakHeld = false
        }
    }

    //#if FORGE
    @Mod.EventHandler
    fun fmlInitialize(event: FMLInitializationEvent) {
        initialize()
        MinecraftForge.EVENT_BUS.register(this)
    }

    @Mod.EventHandler
    fun fmlPostInitialize(event: FMLPostInitializationEvent) {
        postInitialize()
    }

    @SubscribeEvent
    fun onInput(event: InputEvent) {
        handleInput()
    }
    //#else
    //$$ override fun onInitializeClient() {
    //$$     initialize()
    //$$     postInitialize()
    //$$
    //$$     ClientTickEvents.START_CLIENT_TICK.register { client ->
    //$$         handleInput()
    //$$     }
    //$$ }
    //#endif

}