package me.Thelnfamous1.betterhorsejumping;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(BetterHorseJumping.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BetterHorseJumping {
    public static final String MODID = "betterhorsejumping";
    public static final Logger LOGGER = LogUtils.getLogger();

    public BetterHorseJumping() {}

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        // Do something when the setup is run on both client and server
        // LOGGER.info("HELLO from common setup!");
    }
}
