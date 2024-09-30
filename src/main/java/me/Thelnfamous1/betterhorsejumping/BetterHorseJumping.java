package me.Thelnfamous1.betterhorsejumping;

import com.mojang.logging.LogUtils;
import me.Thelnfamous1.betterhorsejumping.common.network.ClientboundAnimatableJumpPacket;
import me.Thelnfamous1.betterhorsejumping.common.network.ServerboundVehicleAnimatableJumpPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;

import java.util.Optional;

@Mod(BetterHorseJumping.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BetterHorseJumping {
    public static final String MODID = "betterhorsejumping";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final ResourceLocation CHANNEL_NAME = new ResourceLocation(MODID, "sync_channel");
    private static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel SYNC_CHANNEL = NetworkRegistry.newSimpleChannel(
            CHANNEL_NAME, () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    private static int index;

    public BetterHorseJumping() {}

    private static void initNetwork(){
        SYNC_CHANNEL.registerMessage(index++, ServerboundVehicleAnimatableJumpPacket.class,
                ServerboundVehicleAnimatableJumpPacket::write, ServerboundVehicleAnimatableJumpPacket::new, ServerboundVehicleAnimatableJumpPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        SYNC_CHANNEL.registerMessage(index++, ClientboundAnimatableJumpPacket.class,
                ClientboundAnimatableJumpPacket::write, ClientboundAnimatableJumpPacket::new, ClientboundAnimatableJumpPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(BetterHorseJumping::initNetwork);
    }
}
