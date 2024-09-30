package me.Thelnfamous1.betterhorsejumping.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.Thelnfamous1.betterhorsejumping.AnimatableJump;
import me.Thelnfamous1.betterhorsejumping.BetterHorseJumping;
import me.Thelnfamous1.betterhorsejumping.DebugFlags;
import me.Thelnfamous1.betterhorsejumping.HorseJumpUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin extends Animal implements AnimatableJump {
    @Unique
    private boolean betterhorsejumping$isJumping;

    protected AbstractHorseMixin(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @WrapOperation(method = "onPlayerJump", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;standIfPossible()V"))
    private void wrap_standIfPossible_onPlayerJump(AbstractHorse instance, Operation<Void> original){
        this.betterhorsejumping$isJumping = true;
        if(DebugFlags.DEBUG_IS_JUMPING)
            BetterHorseJumping.LOGGER.info("Setting betterhorsejumping$isJumping for {}", this);
    }

    @WrapOperation(method = "handleStartJump", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;standIfPossible()V"))
    private void wrap_standIfPossible_handleStartJump(AbstractHorse instance, Operation<Void> original){
        this.betterhorsejumping$isJumping = true;
        if(DebugFlags.DEBUG_IS_JUMPING)
            BetterHorseJumping.LOGGER.info("Setting betterhorsejumping$isJumping for {}", this);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void post_tick(CallbackInfo ci){
        if(this.betterhorsejumping$isJumping && this.onGround()){
            this.betterhorsejumping$isJumping = false;
            if(DebugFlags.DEBUG_IS_JUMPING)
                BetterHorseJumping.LOGGER.info("Resetting betterhorsejumping$isJumping for {}", this);
        }
    }

    @Inject(method = "getRiddenRotation", at = @At("HEAD"), cancellable = true)
    private void pre_getRiddenRotation(LivingEntity pEntity, CallbackInfoReturnable<Vec2> cir){
        if(this.betterhorsejumping$isJumping()){
            cir.setReturnValue(new Vec2(HorseJumpUtil.getXRotFromMovement(this, 0.2F, this.betterhorsejumping$getMaxXRotForJump()), pEntity.getYRot()));
        }
    }

    @Override
    public boolean betterhorsejumping$isJumping() {
        return this.betterhorsejumping$isJumping;
    }

    @Override
    public float betterhorsejumping$getMaxXRotForJump() {
        return 30.0F;
    }
}
