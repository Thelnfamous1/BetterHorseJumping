package me.Thelnfamous1.betterhorsejumping.common;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public interface AnimatableJump {
    double MIN_Y_DELTA_FOR_JUMPING = 0.03F;
    double MIN_MOVEMENT_FOR_JUMPING = 1.0E-5F;
    float JUMP_PITCH_DECREMENT_DELTA = 0.2F;

    float betterhorsejumping$getMaxJumpPitch();

    float betterhorsejumping$getJumpPitch();

    void betterhorsejumping$setJumpPitch(float jumpPitch, boolean absolute);

    default void betterhorsejumping$increaseJumpPitch(Vec3 deltaMovement){
        if (deltaMovement.y * deltaMovement.y < MIN_Y_DELTA_FOR_JUMPING) {
            this.betterhorsejumping$decreaseJumpPitch();
        } else if (deltaMovement.length() > MIN_MOVEMENT_FOR_JUMPING) {
            double horizontalDistance = deltaMovement.horizontalDistance();
            float maxJumpPitch = this.betterhorsejumping$getMaxJumpPitch();
            this.betterhorsejumping$setJumpPitch(Mth.clamp((float) (Math.atan2(-deltaMovement.y, horizontalDistance) * (double)Mth.RAD_TO_DEG), -maxJumpPitch, maxJumpPitch), false);
        }
    }

    default void betterhorsejumping$decreaseJumpPitch(){
        float jumpPitch = this.betterhorsejumping$getJumpPitch();
        if(jumpPitch != 0.0F){
            this.betterhorsejumping$setJumpPitch(Mth.rotLerp(JUMP_PITCH_DECREMENT_DELTA, jumpPitch, 0.0F), false);
        }
    }

    default float getJumpAnim(float pPartialTick){
        return Mth.clamp((-betterhorsejumping$getLerpJumpPitch(pPartialTick) / this.betterhorsejumping$getMaxJumpPitch()), -1.0F, 1.0F);
    }

    float betterhorsejumping$getLerpJumpPitch(float pPartialTick);
}
