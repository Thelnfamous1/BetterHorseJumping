package me.Thelnfamous1.betterhorsejumping.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.Thelnfamous1.betterhorsejumping.BetterHorseJumping;
import me.Thelnfamous1.betterhorsejumping.common.AnimatableJump;
import me.Thelnfamous1.betterhorsejumping.common.DebugFlags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin extends Animal implements AnimatableJump {

    @Unique
    private JumpPhase betterhorsejumping$jumpPhase = JumpPhase.LANDED;

    @Shadow public abstract boolean isJumping();
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
                this.betterhorsejumping$pitchJumpTowardsMovement(this.getDeltaMovement());
            } else {
                this.betterhorsejumping$resetJumpPitch();
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
        float maxJumpPitch = this.betterhorsejumping$getMaxJumpPitch();
        float prevJumpPitch = this.betterhorsejumping$jumpPitch;
        this.betterhorsejumping$jumpPitch = Mth.clamp(jumpPitch, -maxJumpPitch, maxJumpPitch);
        JumpPhase prevPhase = this.betterhorsejumping$jumpPhase;
        this.betterhorsejumping$jumpPhase = this.betterhorsejumping$jumpPhase.updatePhase(prevJumpPitch, this.betterhorsejumping$jumpPitch);
        if(prevJumpPitch != this.betterhorsejumping$jumpPitch){
            if(DebugFlags.DEBUG_HORSE_JUMP)
                BetterHorseJumping.LOGGER.info("Updated jumpPitch for {} from {} to {}", this, prevJumpPitch, this.betterhorsejumping$jumpPitch);
        }
        if(prevPhase != this.betterhorsejumping$jumpPhase){
            if(DebugFlags.DEBUG_HORSE_JUMP)
                BetterHorseJumping.LOGGER.info("Updated JumpPhase for {} from {} to {}", this, prevPhase, this.betterhorsejumping$jumpPhase);
        }
        if(absolute){
            this.betterhorsejumping$jumpPitch0 = this.betterhorsejumping$jumpPitch;
        }
    }

    @Override
    public float betterhorsejumping$getLerpJumpPitch(float pPartialTick) {
        return Mth.lerp(pPartialTick, this.betterhorsejumping$jumpPitch0, this.betterhorsejumping$jumpPitch);
    }

    @Override
    public JumpPhase betterhorsejumping$getJumpPhase() {
        return this.betterhorsejumping$jumpPhase;
    }
}
