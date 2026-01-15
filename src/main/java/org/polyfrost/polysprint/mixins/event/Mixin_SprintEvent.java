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

package org.polyfrost.polysprint.mixins.event;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.polyfrost.polysprint.client.SprintState;
import org.polyfrost.polysprint.client.SprintStateEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(KeyboardInput.class)
public abstract class Mixin_SprintEvent extends Input {
    @Unique private boolean polysprint$sneaking = false;

    /**
     * In 1.16.5, MovementInputFromOptions#tick() calls KeyBinding#isKeyDown()
     * six times in order: forward, back, left, right, jump, SNEAK (ordinal 5).
     * We redirect only the SNEAK read to apply our toggle and emit events.
     */
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;isDown()Z", ordinal = 5))
    private boolean polysprint$setSneakState(KeyMapping instance, Operation<Boolean> original) {
        boolean state = SprintState.isSneakingToggled(instance);
        if (state != polysprint$sneaking) {
            polysprint$sneaking = state;
            SprintStateEvent.Type type = SprintStateEvent.Type.SNEAK;
            EventManager.INSTANCE.post(state ? new SprintStateEvent.Start(type) : new SprintStateEvent.End(type));
        }

        return state;
    }
}
