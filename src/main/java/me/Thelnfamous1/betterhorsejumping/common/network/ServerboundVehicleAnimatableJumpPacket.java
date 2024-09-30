package me.Thelnfamous1.betterhorsejumping.common.network;

import me.Thelnfamous1.betterhorsejumping.BetterHorseJumping;
import me.Thelnfamous1.betterhorsejumping.common.AnimatableJump;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class ServerboundVehicleAnimatableJumpPacket {

    private final int vehicleId;
    private final float jumpPitch;

    public ServerboundVehicleAnimatableJumpPacket(int vehicleId, float jumpPitch){
        this.vehicleId = vehicleId;
        this.jumpPitch = jumpPitch;
    }

    public ServerboundVehicleAnimatableJumpPacket(FriendlyByteBuf buf){
        this.vehicleId = buf.readVarInt();
        this.jumpPitch = buf.readFloat();
    }

    public void write(FriendlyByteBuf buf){
        buf.writeVarInt(this.vehicleId);
        buf.writeFloat(this.jumpPitch);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            Entity vehicle = sender.level().getEntity(this.vehicleId);
            if(vehicle instanceof AnimatableJump animatableJump && vehicle == sender.getRootVehicle() && vehicle.getControllingPassenger() == sender){
                animatableJump.betterhorsejumping$setJumpPitch(this.jumpPitch, true);
                BetterHorseJumping.SYNC_CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> sender), new ClientboundAnimatableJumpPacket(this.vehicleId, this.jumpPitch));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
