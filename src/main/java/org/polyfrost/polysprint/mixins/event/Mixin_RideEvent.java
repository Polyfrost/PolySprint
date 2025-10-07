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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.polyfrost.oneconfig.api.event.v1.events.Event;
import org.polyfrost.polysprint.client.SprintStateEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLivingBase.class)
public abstract class Mixin_RideEvent {
    @Inject(method = "mountEntity", at = @At("HEAD"))
    private void onMount(Entity entityIn, CallbackInfo ci) {
        //noinspection ConstantConditions
        if ((Object) this == OmniClient.getPlayer()) {
            Event event;
            if (entityIn != null) {
                event = new SprintStateEvent.Start(SprintStateEvent.Type.RIDE);
            } else {
                event = new SprintStateEvent.End(SprintStateEvent.Type.RIDE);
            }

            EventManager.INSTANCE.post(event);
        }
    }
}
