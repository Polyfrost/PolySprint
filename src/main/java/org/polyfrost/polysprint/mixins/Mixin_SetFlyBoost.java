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

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
//? if =1.21.1
/*import net.minecraft.client.player.Input;*/
//? if >1.21.1
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import org.polyfrost.polysprint.client.PolySprintConfig;
import org.polyfrost.polysprint.client.SprintState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class Mixin_SetFlyBoost extends AbstractClientPlayer {
    //? if =1.21.1
    /*@Shadow public Input input;*/
    //? if >1.21.1
    @Shadow public ClientInput input;

    public Mixin_SetFlyBoost(ClientLevel level, GameProfile profile) {
        super(level, profile);
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void polysprint$modifyFlightSpeed(CallbackInfo ci) {
        if (!SprintState.isFlyBoostEnabled()) {
            return;
        }

        final float base = 0.05f;

        if (!SprintState.isFlyBoosting()) {
            this.getAbilities().setFlyingSpeed(base);
            return;
        }

        float boost = PolySprintConfig.getFlyBoostAmount();
        this.getAbilities().setFlyingSpeed(base * boost);

        if (this.getAbilities().flying) {
            double yDelta = 0.0;
            //? if =1.21.1 {
            /*if (this.input.shiftKeyDown) {
            *///?} else {
            if (this.input.keyPresses.shift()) {
            //?}
                yDelta -= 0.15 * boost;
            }

            //? if =1.21.1 {
            /*if (this.input.jumping) {
            *///?} else {
            if (this.input.keyPresses.jump()) {
            //?}
                yDelta += 0.15 * boost;
            }

            if (yDelta != 0.0) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, yDelta, 0.0));
            }
        }
    }
}
