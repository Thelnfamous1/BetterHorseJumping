package me.Thelnfamous1.betterhorsejumping.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import me.Thelnfamous1.betterhorsejumping.common.AnimatableJump;
import me.Thelnfamous1.betterhorsejumping.client.JumpAnimMath;
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
    private void post_prepareMobModel(T pEntity, float walkAnimPos, float walkAnimSpeed, float pPartialTick, CallbackInfo ci, @Local(ordinal = 7) float xRotRadians){
        float jumpAnim = ((AnimatableJump) pEntity).getJumpAnim(pPartialTick);
        if(jumpAnim != 0.0F && pEntity.getStandAnim(pPartialTick) == 0.0F){
            float verticalDirection = Mth.sign(jumpAnim);
            float jumpAnimAbs = Mth.abs(jumpAnim);
            float jumpAnimRemaining = 1.0F - jumpAnimAbs;

            // Rotate Head
            float baseHeadXRot = jumpAnimRemaining * ((JumpAnimMath.SIXTH_PI));
            float jumpingHeadXRot = jumpAnimAbs * ((JumpAnimMath.TWELFTH_PI * (verticalDirection >= 0.0F ? 1 : 3)));
            this.headParts.xRot = (jumpingHeadXRot + baseHeadXRot);

            // Shift Head
            float jumpingHeadYShiftMax = verticalDirection >= 0.0F ? -4.0F : 12.0F;
            float jumpingHeadZShiftMax = verticalDirection >= 0.0F ? -4.0F : -20.0F;
            this.headParts.y = (jumpAnimAbs * jumpingHeadYShiftMax + jumpAnimRemaining * 4.0F);
            this.headParts.z = (jumpAnimAbs * jumpingHeadZShiftMax + jumpAnimRemaining * -12.0F);

            // Rotate Body
            this.body.xRot = jumpAnimAbs * (-JumpAnimMath.FOURTH_PI) * verticalDirection;

            // Shift Front Legs
            float jumpingFrontLegYMax = verticalDirection >= 0.0F ? 2.0F : 26.0F;
            float jumpingFrontLegY = jumpingFrontLegYMax * jumpAnimAbs;
            this.leftFrontLeg.y = (jumpingFrontLegY + 14.0F * jumpAnimRemaining);
            float jumpingFrontLegZMax = verticalDirection >= 0.0F ? -6.0F : -14.0F;
            float jumpingFrontLegZ = jumpingFrontLegZMax * jumpAnimAbs;
            this.leftFrontLeg.z = (jumpingFrontLegZ - 10.0F * jumpAnimRemaining);
            this.rightFrontLeg.y = this.leftFrontLeg.y;
            this.rightFrontLeg.z = this.leftFrontLeg.z;

            // Rotate Back Legs
            float jumpingBackLegXRot = (JumpAnimMath.TWELFTH_PI * verticalDirection) * jumpAnimAbs;
            float limbSpeed = pEntity.isInWater() ? 0.2F : 1.0F;
            float limbProgress = Mth.cos(limbSpeed * walkAnimPos * 0.6662F + Mth.PI);
            float baseBackLegXRot = limbProgress * 0.5F * walkAnimSpeed * jumpAnimRemaining;
            this.leftHindLeg.xRot = (jumpingBackLegXRot - baseBackLegXRot);
            this.rightHindLeg.xRot = (jumpingBackLegXRot + baseBackLegXRot);

            // Rotate Front Legs
            float jumpingFrontLegXRotMax = -JumpAnimMath.THIRD_PI * verticalDirection;
            float tickCount = (float)pEntity.tickCount + pPartialTick;
            float jumpingSwingAdditional = Mth.cos(tickCount * 0.6F + Mth.PI);
            float baseFrontLegSwing = limbProgress * 0.8F * walkAnimSpeed;
            float baseFrontLegXRot = baseFrontLegSwing * jumpAnimRemaining;
            this.leftFrontLeg.xRot = (jumpingFrontLegXRotMax + jumpingSwingAdditional) * jumpAnimAbs + baseFrontLegXRot;
            this.rightFrontLeg.xRot = (jumpingFrontLegXRotMax - jumpingSwingAdditional) * jumpAnimAbs - baseFrontLegXRot;
        }
    }
}
