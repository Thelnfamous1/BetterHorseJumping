package me.Thelnfamous1.betterhorsejumping.client.network;

import me.Thelnfamous1.betterhorsejumping.common.AnimatableJump;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

public class ClientNetworkHandler {

    public static void handleSyncAnimatableJump(int entityId, float jumpPitch){
        Entity vehicle = Minecraft.getInstance().level.getEntity(entityId);
        if (vehicle instanceof AnimatableJump animatableJump) {
            animatableJump.betterhorsejumping$setJumpPitch(jumpPitch, true);
        }
    }
}
