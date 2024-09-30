package me.Thelnfamous1.betterhorsejumping.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import me.Thelnfamous1.betterhorsejumping.AnimatableJump;
import me.Thelnfamous1.betterhorsejumping.HorseJumpUtil;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HorseModel.class)
public abstract class HorseModelMixin<T extends AbstractHorse> {

    @Shadow protected abstract Iterable<ModelPart> bodyParts();

    @Shadow @Final protected ModelPart headParts;

    @Shadow @Final protected ModelPart body;

    @Shadow @Final private ModelPart leftFrontLeg;

    @Shadow @Final private ModelPart rightFrontLeg;

    @Shadow @Final private ModelPart leftHindLeg;

    @Shadow @Final private ModelPart rightHindLeg;

    @Shadow @Final private ModelPart tail;

    @Inject(method = "prepareMobModel(Lnet/minecraft/world/entity/animal/horse/AbstractHorse;FFF)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/HorseModel;rightHindBabyLeg:Lnet/minecraft/client/model/geom/ModelPart;", opcode = Opcodes.GETFIELD, ordinal = 0))
    private void post_prepareMobModel(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, CallbackInfo ci, @Local(ordinal = 7) float xRotRadians){
        if(((AnimatableJump)pEntity).betterhorsejumping$isJumping() && pEntity.getStandAnim(pPartialTick) == 0.0F){
            float jumpAnim = Mth.clamp((-pEntity.getXRot() / ((AnimatableJump) pEntity).betterhorsejumping$getMaxXRotForJump()), -1.0F, 1.0F);
            float verticalDirection = Mth.sign(jumpAnim);
            float jumpAnimAbs = Mth.abs(jumpAnim);
            float jumpAnimRemaining = 1.0F - jumpAnimAbs;
            float f13 = jumpAnimRemaining * ((HorseJumpUtil.SIXTH_PI * verticalDirection) + xRotRadians);
            // TODO: Fix head positioning during descent
            this.headParts.xRot = (jumpAnimAbs * ((HorseJumpUtil.TWELFTH_PI * verticalDirection) + xRotRadians) + f13);
            this.headParts.y = (jumpAnimAbs * -4.0F + jumpAnimRemaining * 4.0F) * verticalDirection;
            this.headParts.z = (jumpAnimAbs * -4.0F + jumpAnimRemaining * -12.0F) * verticalDirection;

            this.body.xRot = jumpAnimAbs * (-HorseJumpUtil.FOURTH_PI) * verticalDirection;

            // TODO: Fix front legs positioning during descent
            this.leftFrontLeg.y = (2.0F * jumpAnimAbs + 14.0F * jumpAnimRemaining) * verticalDirection;
            this.leftFrontLeg.z = (-6.0F * jumpAnimAbs - 10.0F * jumpAnimRemaining) * verticalDirection;
            this.rightFrontLeg.y = this.leftFrontLeg.y;
            this.rightFrontLeg.z = this.leftFrontLeg.z;

            float f14 = HorseJumpUtil.TWELFTH_PI * jumpAnimAbs;
            float f10 = pEntity.isInWater() ? 0.2F : 1.0F;
            float f11 = Mth.cos(f10 * pLimbSwing * 0.6662F + Mth.PI);
            float tickCount = (float)pEntity.tickCount + pPartialTick;
            float f15 = Mth.cos(tickCount * 0.6F + Mth.PI);
            this.leftHindLeg.xRot = (f14 - f11 * 0.5F * pLimbSwingAmount * jumpAnimRemaining);
            this.rightHindLeg.xRot = (f14 + f11 * 0.5F * pLimbSwingAmount * jumpAnimRemaining);
            float f12 = f11 * 0.8F * pLimbSwingAmount;
            this.leftFrontLeg.xRot = ((-HorseJumpUtil.THIRD_PI) + f15) * jumpAnimAbs + f12 * jumpAnimRemaining;
            this.rightFrontLeg.xRot = ((-HorseJumpUtil.THIRD_PI) - f15) * jumpAnimAbs - f12 * jumpAnimRemaining;
        }
    }
}
