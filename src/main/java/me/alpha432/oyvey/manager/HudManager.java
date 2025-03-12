package me.alpha432.oyvey.manager;

import java.util.Arrays;

import org.apache.commons.lang3.time.DurationFormatUtils;

import net.minecraft.client.network.PlayerListEntry;
import me.alpha432.oyvey.features.modules.client.hud.utils.Timer;

import java.text.DecimalFormat;
import static me.alpha432.oyvey.util.traits.Util.mc;

public class HudManager {
    private final float[] tpsCounts = new float[10];
    private final DecimalFormat format = new DecimalFormat("##.00#");
    private final Timer timer = new Timer();
    private float TPS = 20.0f;
    private long lastUpdate = -1L;
    private String serverBrand = "\u00A7b\u00A7l[\u00A7a\u00A7lCracked\u00A7b\u00A7l]\u00A7r \u00A7a\u00A7l-> \u00A7b\u00A7l[\u00A7a\u00A7lServer\u00A7b\u00A7l]\u00A7r ";//"\u00A7b\u00A7l[\u00A7a\u00A7lCracked\u00A7b\u00A7l]\u00A7r ";

    public void onPacketReceived() {
        this.timer.reset();
    }
    
    public void update() {
        float tps;
        long currentTime = System.currentTimeMillis();
        if (this.lastUpdate == -1L) {
            this.lastUpdate = currentTime;
            return;
        }
        long timeDiff = currentTime - this.lastUpdate;
        float tickTime = (float) timeDiff / 20.0f;
        if (tickTime == 0.0f) {
            tickTime = 50.0f;
        }
        if ((tps = 1000.0f / tickTime) > 20.0f) {
            tps = 20.0f;
        }
        System.arraycopy(this.tpsCounts, 0, this.tpsCounts, 1, this.tpsCounts.length - 1);
        this.tpsCounts[0] = tps;
        double total = 0.0;
        for (float f : this.tpsCounts) {
            total += f;
        }
        if ((total /= this.tpsCounts.length) > 20.0) {
            total = 20.0;
        }
        this.TPS = Float.parseFloat(this.format.format(total));
        this.lastUpdate = currentTime;
    }

    //@Override
    public void reset() {
        Arrays.fill(this.tpsCounts, 20.0f);
        this.TPS = 20.0f;
    }

    public float getTpsFactor() {
        return 20.0f / this.TPS;
    }

    public float getTPS() {
        return this.TPS;
    }
    public float getTPS2() {
        return this.TPS;
    }


    public String getServerBrand() {
        return this.serverBrand;
    }

    public void setServerBrand(String brand) {
        this.serverBrand = brand;
    }


    public static int getPing() {
        if (mc.getNetworkHandler() == null || mc.player == null) return 0;
        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        if (playerListEntry == null) return 0;
        return playerListEntry.getLatency();
    }

    //count Time
    //public static long initTime
    //initTime = System.currentTimeMillis();

    public static String onlineTime(long initTime) {
        return DurationFormatUtils.formatDuration(System.currentTimeMillis() - initTime, "HH:mm", true);
    }
}
