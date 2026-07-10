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

import net.minecraft.client.KeyMapping;
import net.minecraft.client.ToggleKeyMapping;
import org.polyfrost.polysprint.client.PolySprintConfig;
import org.polyfrost.polysprint.client.StickyKeyBindingSetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ToggleKeyMapping.class)
public abstract class Mixin_StickyKeyBindingSetter extends KeyMapping implements StickyKeyBindingSetter {
    //? if <1.21.10 {
    /*public Mixin_StickyKeyBindingSetter(String string, int i, String category) {
        super(string, i, category);
    }
    *///?} else {
    public Mixin_StickyKeyBindingSetter(String string, int i, KeyMapping.Category category) {
        super(string, i, category);
    }
    //?}

    @Override
    public void polySprint$toggle(boolean value) {
        super.setDown(value);
    }

    @Inject(method = "setDown", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;setDown(Z)V", ordinal = 0), cancellable = true)
    private void onSetDown(boolean bl, CallbackInfo ci) {
        if (!PolySprintConfig.isEnabled()) {
            return;
        }

        ci.cancel();
    }
}
