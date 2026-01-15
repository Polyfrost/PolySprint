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

import dev.deftu.omnicore.api.client.OmniClient;
import net.minecraft.world.entity.LivingEntity;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.polyfrost.polysprint.client.SprintStateEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class Mixin_SprintEvent_2EletricBoogaloo {
    @Inject(method = "setSprinting", at = @At("HEAD"))
    private void onSetSprinting(boolean sprinting, CallbackInfo ci) {
        if ((Object) this != OmniClient.getPlayer()) {
            return;
        }

        SprintStateEvent.Type type = SprintStateEvent.Type.SPRINT;
        EventManager.INSTANCE.post(sprinting ? new SprintStateEvent.Start(type) : new SprintStateEvent.End(type));
    }
}
