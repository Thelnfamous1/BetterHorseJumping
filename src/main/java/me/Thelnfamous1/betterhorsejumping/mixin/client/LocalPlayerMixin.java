package me.Thelnfamous1.betterhorsejumping.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import me.Thelnfamous1.betterhorsejumping.BetterHorseJumping;
import me.Thelnfamous1.betterhorsejumping.common.AnimatableJump;
import me.Thelnfamous1.betterhorsejumping.common.network.ServerboundVehicleAnimatableJumpPacket;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V", ordinal = 2, shift = At.Shift.AFTER))
    private void post_sendServerboundMoveVehiclePacket_tick(CallbackInfo ci, @Local(ordinal = 0) Entity rootVehicle){
        if(rootVehicle instanceof AnimatableJump animatableJump){
            BetterHorseJumping.SYNC_CHANNEL.sendToServer(new ServerboundVehicleAnimatableJumpPacket(rootVehicle.getId(), animatableJump.betterhorsejumping$getJumpPitch()));
        }
    }
}
