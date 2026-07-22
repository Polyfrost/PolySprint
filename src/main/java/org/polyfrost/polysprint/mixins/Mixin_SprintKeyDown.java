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

package org.polyfrost.polysprint.mixins;

//? if <1.21.5 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.KeyMapping;
import org.polyfrost.polysprint.client.SprintState;
import org.spongepowered.asm.mixin.injection.At;
*///?}
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LocalPlayer.class)
public class Mixin_SprintKeyDown {
    //? if <1.21.5 {
    /*// Before 1.21.5, LocalPlayer#aiStep reads keySprint.isDown() directly to drive sprinting, so it never
    // passes through the KeyboardInput toggle wrap. When vanilla sprint is set to Toggle and a separate toggle
    // keybind is used, PolySprint cancels the sticky toggle on keySprint, leaving isDown() stuck false and
    // breaking momentary sprint. Route these reads through the same state resolution as newer versions.
    @WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;isDown()Z"))
    private boolean polysprint$sprintKeyDown(KeyMapping instance, Operation<Boolean> original) {
        return SprintState.isSprintingToggled(instance, original.call(instance));
    }
    *///?}
}
