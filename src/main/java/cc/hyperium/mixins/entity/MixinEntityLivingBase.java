/*
 *     Copyright (C) 2018  Hyperium <https://hyperium.cc/>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cc.hyperium.mixins.entity;

import cc.hyperium.event.EventBus;
import cc.hyperium.event.entity.LivingDeathEvent;
import cc.hyperium.event.entity.LivingEntityUpdateEvent;
import cc.hyperium.event.entity.PlayerResetHurtTimeEvent;
import cc.hyperium.mixinsimp.entity.HyperiumEntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends MixinEntity {
    private HyperiumEntityLivingBase hyperiumEntityLivingBase = new HyperiumEntityLivingBase((EntityLivingBase) (Object) this);

    @Inject(method = "getLook", at = @At("HEAD"), cancellable = true)
    private void getLook(float partialTicks, CallbackInfoReturnable<Vec3> ci) {
        hyperiumEntityLivingBase.getLook(partialTicks, ci, super.getLook(partialTicks));
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(DamageSource source, CallbackInfo ci) {
        EventBus.INSTANCE.post(new LivingDeathEvent((EntityLivingBase) (Object) this, source));
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void onUpdate(CallbackInfo ci) {
        EventBus.INSTANCE.post(new LivingEntityUpdateEvent((EntityLivingBase) (Object) this));
    }
    
    @Inject(method = "handleStatusUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;getHurtSound()Ljava/lang/String;", shift = At.Shift.BEFORE))
    public void resetHurtTimer(byte id, CallbackInfo ci) {
        EventBus.INSTANCE.post(new PlayerResetHurtTimeEvent((Entity) (Object) this));
    }
}
