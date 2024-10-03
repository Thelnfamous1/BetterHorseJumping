package me.Thelnfamous1.betterhorsejumping.common;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public interface AnimatableJump {
    double MIN_Y_DELTA_SQR_FOR_JUMPING = 0.03F;
    double MIN_MOVEMENT_FOR_JUMPING = 1.0E-5F;
    float JUMP_PITCH_DECREMENT_DELTA = 0.2F;
    int LANDING_DURATION = Mth.floor(0.5 * 20);

    static long calculatedAccumulatedTime(float animation, float animationDuration){
        return Mth.lfloor((animation * animationDuration) * 1000.0F);
    }

    float betterhorsejumping$getJumpPitch();

    void betterhorsejumping$setJumpPitch(float jumpPitch, boolean absolute);

    default void betterhorsejumping$pitchJumpTowardsMovement(Vec3 deltaMovement){
        float jumpPitch = this.betterhorsejumping$getJumpPitch();
        if (Mth.square(deltaMovement.y) < MIN_Y_DELTA_SQR_FOR_JUMPING && jumpPitch != 0.0F) {
            this.betterhorsejumping$setJumpPitch(Mth.rotLerp(JUMP_PITCH_DECREMENT_DELTA, jumpPitch, 0.0F), false);
        } else if (deltaMovement.length() > MIN_MOVEMENT_FOR_JUMPING) {
            double horizontalDistance = deltaMovement.horizontalDistance();
            this.betterhorsejumping$setJumpPitch((float) (Math.atan2(-deltaMovement.y, horizontalDistance) * (double)Mth.RAD_TO_DEG), false);
        }
    }

    default void betterhorsejumping$resetJumpPitch(){
        float jumpPitch = this.betterhorsejumping$getJumpPitch();
        float startJumpPitch = Mth.abs(this.betterhorsejumping$getStartJumpPitch());
        if(jumpPitch != 0.0F){
            float pitchAddition = startJumpPitch / LANDING_DURATION;
            if(jumpPitch < 0.0F){
                this.betterhorsejumping$setJumpPitch(Math.min(jumpPitch + pitchAddition, 0), false);
            } else{
                this.betterhorsejumping$setJumpPitch(Math.max(jumpPitch - pitchAddition, 0), false);
            }
        }
    }

    default float betterhorsejumping$getJumpAnim(float pPartialTick){
        float jumpPitchAbs = Mth.abs(this.betterhorsejumping$getLerpJumpPitch(pPartialTick));
        float startJumpPitchAbs = Mth.abs(this.betterhorsejumping$getStartJumpPitch());
        if(startJumpPitchAbs == 0.0F){
            return 0.0F;
        }
        switch (this.betterhorsejumping$getJumpPhase()){
            case LAUNCHING -> {
                return Mth.clamp((jumpPitchAbs / startJumpPitchAbs), 0, 1) * 0.25F;
            }
            case ASCENDING -> {
                return Mth.clamp((1 - (jumpPitchAbs / startJumpPitchAbs)), 0, 1) * 0.25F + 0.25F;
            }
            case DESCENDING -> {
                return Mth.clamp((jumpPitchAbs / startJumpPitchAbs), 0, 1) * 0.25F + 0.5F;
            }
            case LANDING -> {
                return Mth.clamp((1 - (jumpPitchAbs / startJumpPitchAbs)), 0, 1) * 0.25F + 0.75F;
            }
            default -> {
                return 0.0F;
            }
        }
    }

    float betterhorsejumping$getStartJumpPitch();

    float betterhorsejumping$getLerpJumpPitch(float pPartialTick);

    JumpPhase betterhorsejumping$getJumpPhase();

    enum JumpPhase{
        LANDED,
        LAUNCHING,
        ASCENDING,
        DESCENDING,
        LANDING;

        public JumpPhase updatePhase(float prevJumpPitch, float newJumpPitch){
            if(prevJumpPitch == newJumpPitch){
                return this;
            }
            switch (this){
                case LANDED -> {
                    if(Mth.abs(newJumpPitch) > 0.0F){
                        return LAUNCHING;
                    }
                }
                case LAUNCHING -> {
                    if(Mth.abs(prevJumpPitch) > Mth.abs(newJumpPitch)){
                        return ASCENDING;
                    }
                }
                case ASCENDING -> {
                    if(Mth.abs(prevJumpPitch) < Mth.abs(newJumpPitch)){
                        return DESCENDING;
                    }
                }
                case DESCENDING -> {
                    if(Mth.abs(prevJumpPitch) > Mth.abs(newJumpPitch)){
                        return LANDING;
                    }
                }
                case LANDING -> {
                    if(newJumpPitch == 0.0F){
                        return LANDED;
                    }
                }
            }
            return this;
        }
    }
}
