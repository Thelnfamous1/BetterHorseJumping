package me.Thelnfamous1.betterhorsejumping.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.Thelnfamous1.betterhorsejumping.common.AnimatableJump;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin extends Animal implements AnimatableJump {

    @Shadow public abstract boolean isJumping();

    @Shadow private float standAnimO;
    @Shadow @Final private static Predicate<LivingEntity> PARENT_HORSE_SELECTOR;
    @Unique
    private float betterhorsejumping$jumpPitch0;
    @Unique
    private float betterhorsejumping$jumpPitch;

    protected AbstractHorseMixin(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    // Prevent the vanilla stand animation from playing during a jump
    @WrapOperation(method = "onPlayerJump", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;standIfPossible()V"))
    private void wrap_standIfPossible_onPlayerJump(AbstractHorse instance, Operation<Void> original){
        // NO_OP
    }

    // Prevent the vanilla stand animation from playing during a jump
    @WrapOperation(method = "handleStartJump", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;standIfPossible()V"))
    private void wrap_standIfPossible_handleStartJump(AbstractHorse instance, Operation<Void> original){
        // NO_OP
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void post_tick(CallbackInfo ci){
        this.betterhorsejumping$jumpPitch0 = this.betterhorsejumping$jumpPitch;
        if(this.isControlledByLocalInstance()){
            if(this.isJumping()){
                this.betterhorsejumping$increaseJumpPitch(this.getDeltaMovement());
            } else {
                this.betterhorsejumping$decreaseJumpPitch();
            }
        }
    }

    @Inject(method = "positionRider", at = @At("TAIL"))
    private void post_positionRider(Entity pPassenger, MoveFunction pCallback, CallbackInfo ci){
        float jumpAnim = this.getJumpAnim(0.0F);
        if (jumpAnim != 0.0F && this.standAnimO <= 0.0F) {
            float xScale = Mth.sin(this.yBodyRot * Mth.DEG_TO_RAD);
            float zScale = Mth.cos(this.yBodyRot * Mth.DEG_TO_RAD);
            float xzOffset = 0.7F * Mth.abs(jumpAnim);
            float additionalYOffset = 0.15F * jumpAnim;
            pCallback.accept(pPassenger,
                    this.getX() + (double)(xzOffset * xScale),
                    this.getY() + this.getPassengersRidingOffset() + pPassenger.getMyRidingOffset() + (double)additionalYOffset,
                    this.getZ() - (double)(xzOffset * zScale));
            if (pPassenger instanceof LivingEntity) {
                ((LivingEntity)pPassenger).yBodyRot = this.yBodyRot;
            }
        }
    }

    @Override
    public float betterhorsejumping$getMaxJumpPitch() {
        return 30.0F;
    }

    @Override
    public float betterhorsejumping$getJumpPitch() {
        return this.betterhorsejumping$jumpPitch;
    }

    @Override
    public void betterhorsejumping$setJumpPitch(float jumpPitch, boolean absolute) {
        this.betterhorsejumping$jumpPitch = jumpPitch;
        if(absolute){
            this.betterhorsejumping$jumpPitch0 = jumpPitch;
        }
    }

    @Override
    public float betterhorsejumping$getLerpJumpPitch(float pPartialTick) {
        return Mth.lerp(pPartialTick, this.betterhorsejumping$jumpPitch0, this.betterhorsejumping$jumpPitch);
    }
}
