package com.duyanhggg.areaminer;

import com.duyanhggg.areaminer.config.ConfigManager;
import com.duyanhggg.areaminer.mining.MiningController;
import com.duyanhggg.areaminer.network.NetworkHandler;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AreaMiner implements ModInitializer {
    public static final String MOD_ID = "area-miner";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Area Miner mod");
        
        // Initialize configuration
        ConfigManager.initialize();
        
        // Initialize network handlers
        NetworkHandler.registerServerPackets();
        
        // Initialize mining controller
        MiningController.initialize();
        
        LOGGER.info("Area Miner mod initialized successfully");
    }
}
