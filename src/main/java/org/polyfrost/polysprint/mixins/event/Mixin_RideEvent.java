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

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.polyfrost.polysprint.client.SprintStateEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class Mixin_RideEvent {
    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"))
    private void onMount(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        //noinspection ConstantConditions
        if ((Object) this == Minecraft.getInstance().player) {
            EventManager.INSTANCE.post(new SprintStateEvent.Start(SprintStateEvent.Type.RIDE));
        }
    }

    @Inject(method = "removeVehicle", at = @At("HEAD"))
    private void onDismount(CallbackInfo ci) {
        //noinspection ConstantConditions
        if ((Object) this == Minecraft.getInstance().player) {
            EventManager.INSTANCE.post(new SprintStateEvent.End(SprintStateEvent.Type.RIDE));
        }
    }
}
