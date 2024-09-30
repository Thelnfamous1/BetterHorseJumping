package me.Thelnfamous1.betterhorsejumping;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class HorseJumpUtil {
    public static final float THIRD_PI = Mth.PI / 3F;
    public static final float FOURTH_PI = Mth.PI / 4F;
    public static final float SIXTH_PI = Mth.PI / 6F;
    public static final float TWELFTH_PI = Mth.PI / 12F;

    public static float getXRotFromMovement(Entity horse, float rotationDelta, float maxXRot){
        Vec3 deltaMovement = horse.getDeltaMovement();
        if (deltaMovement.y * deltaMovement.y < (double)0.03F && horse.getXRot() != 0.0F) {
            return Mth.rotLerp(rotationDelta, horse.getXRot(), 0.0F);
        } else if (deltaMovement.length() > (double)1.0E-5F) {
            double horizontalDistance = deltaMovement.horizontalDistance();
            return Mth.clamp((float) (Math.atan2(-deltaMovement.y, horizontalDistance) * (double)Mth.RAD_TO_DEG), -maxXRot, maxXRot);
        } else{
            return 0.0F;
        }
    }
}
