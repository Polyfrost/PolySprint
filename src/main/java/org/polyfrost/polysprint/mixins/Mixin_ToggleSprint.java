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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.KeyboardInput;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.polyfrost.polysprint.client.SprintState;
import org.polyfrost.polysprint.client.SprintStateEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(KeyboardInput.class)
public class Mixin_ToggleSprint {
    @Unique
    private boolean polysprint$isToggleActive = false;

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;isDown()Z", ordinal = 6))
    private boolean setSprintState(KeyMapping instance, Operation<Boolean> original) {
        boolean vanilla = original.call(instance);
        if (Minecraft.getInstance().player == null) return vanilla;

        if (SprintState.isSprintToggleActive() != polysprint$isToggleActive) {
            polysprint$isToggleActive = SprintState.isSprintToggleActive();
            if (SprintState.isSprintToggleActive() || !Minecraft.getInstance().player.isSprinting()) {
                SprintStateEvent.Type type = SprintStateEvent.Type.SPRINT;
                EventManager.INSTANCE.post(SprintState.isSprintToggleActive() ? new SprintStateEvent.Start(type) : new SprintStateEvent.End(type));
            }
        }

        return SprintState.isSprintingToggled(instance, vanilla);
    }
}
