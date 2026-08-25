package dev.brodino.epicknightslancefix;

import dev.brodino.epicknightslancefix.network.LanceCollisionHandler;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EpicKnightsLanceFix implements ModInitializer {

    public static final String MOD_ID = "epicknightslancefix";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LanceCollisionHandler.initialize();
    }
}
