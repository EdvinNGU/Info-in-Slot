package com.drillslotinfo;

import net.fabricmc.api.ModInitializer;
import com.drillslotinfo.config.DrillConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DrillSlotInfo implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("InfoInSlot");

    @Override
    public void onInitialize() {
        LOGGER.info("Info in Slot initialized");
        DrillConfig.init();
    }
}
