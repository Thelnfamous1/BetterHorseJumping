package me.Thelnfamous1.betterhorsejumping.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import me.Thelnfamous1.betterhorsejumping.client.ContainedHierarchicalModel;
import me.Thelnfamous1.betterhorsejumping.client.HorseAnimations;
import me.Thelnfamous1.betterhorsejumping.common.AnimatableJump;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.joml.Vector3f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HorseModel.class)
public abstract class HorseModelMixin<T extends AbstractHorse> {
    @Unique
    private static final Vector3f betterhorsejumping$ANIMATION_VECTOR_CACHE = new Vector3f();
    @Unique
    private ContainedHierarchicalModel<T> betterhorsejumping$containedHierarchicalModel;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void post_init(ModelPart pRoot, CallbackInfo ci){
        this.betterhorsejumping$containedHierarchicalModel = new ContainedHierarchicalModel<>(pRoot);
    }

    @Inject(method = "prepareMobModel(Lnet/minecraft/world/entity/animal/horse/AbstractHorse;FFF)V", at = @At("HEAD"))
    private void pre_prepareMobModel(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, CallbackInfo ci){
        this.betterhorsejumping$containedHierarchicalModel.root().getAllParts().forEach(ModelPart::resetPose);
    }

    @Inject(method = "prepareMobModel(Lnet/minecraft/world/entity/animal/horse/AbstractHorse;FFF)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/HorseModel;rightHindBabyLeg:Lnet/minecraft/client/model/geom/ModelPart;", opcode = Opcodes.GETFIELD, ordinal = 0))
    private void post_prepareMobModel(T pEntity, float walkAnimPos, float walkAnimSpeed, float pPartialTick, CallbackInfo ci, @Local(ordinal = 7) float xRotRadians){
        if(((AnimatableJump)pEntity).betterhorsejumping$getJumpPhase() != AnimatableJump.JumpPhase.LANDED && pEntity.getStandAnim(pPartialTick) == 0.0F){
            float jumpAnim = ((AnimatableJump) pEntity).betterhorsejumping$getJumpAnim(pPartialTick);
            KeyframeAnimations.animate(this.betterhorsejumping$containedHierarchicalModel, HorseAnimations.JUMP, AnimatableJump.calculatedAccumulatedTime(jumpAnim, HorseAnimations.JUMP.lengthInSeconds()), 1.0F, betterhorsejumping$ANIMATION_VECTOR_CACHE);
        }
    }
}
