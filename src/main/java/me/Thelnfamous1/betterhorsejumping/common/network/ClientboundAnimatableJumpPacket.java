package me.Thelnfamous1.betterhorsejumping.common.network;

import me.Thelnfamous1.betterhorsejumping.client.network.ClientNetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundAnimatableJumpPacket {

    private final int entityId;
    private final float jumpPitch;

    public ClientboundAnimatableJumpPacket(int entityId, float jumpPitch){
        this.entityId = entityId;
        this.jumpPitch = jumpPitch;
    }

    public ClientboundAnimatableJumpPacket(FriendlyByteBuf buf){
        this.entityId = buf.readVarInt();
        this.jumpPitch = buf.readFloat();
    }

    public void write(FriendlyByteBuf buf){
        buf.writeVarInt(this.entityId);
        buf.writeFloat(this.jumpPitch);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            ClientNetworkHandler.handleSyncAnimatableJump(this.entityId, this.jumpPitch);
        });
        ctx.get().setPacketHandled(true);
    }
}
