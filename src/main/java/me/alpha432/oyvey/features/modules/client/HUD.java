package me.alpha432.oyvey.features.modules.client;

import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.event.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.hud.TimeDay;
import me.alpha432.oyvey.features.modules.client.hud.utils.FrameRateCounter;
import me.alpha432.oyvey.features.modules.client.hud.utils.MathUtility;
import me.alpha432.oyvey.features.modules.client.hud.TimerRadar;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.manager.HudManager;
import me.alpha432.oyvey.util.ColorUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class HUD extends Module {

    private static final ItemStack totem = new ItemStack(Items.TOTEM_OF_UNDYING);
        public Setting<Integer> red = this.register(new Setting<>("Red", 0, 0, 255));
        public Setting<Integer> green = this.register(new Setting<>("Green", 0, 0, 255));
        public Setting<Integer> blue = this.register(new Setting<>("Blue", 255, 0, 255));
        public Setting<Integer> alpha = this.register(new Setting<>("Alpha", 240, 0, 255));
        public Setting<Integer> updatedelay = this.register(new Setting<>("UpdateDelay", 5, 0, 1000));
        private final Setting<Boolean> health = this.register(new Setting<>("Health", false));
        private final Setting<Boolean> renderingUp = this.register(new Setting<>("RenderingUp", false));
        private final Setting<Boolean> arrayList = this.register(new Setting<>("ActiveModules", false));
        private final Setting<Boolean> coords = this.register(new Setting<>("Coords", false));
        private final Setting<Boolean> armorhud = this.register(new Setting<>("Armor", false));
        private final Setting<Boolean> totemshud = this.register(new Setting<>("Totems", false));
        private final Setting<Boolean> direction = this.register(new Setting<>("Direction", false));
        private final Setting<Boolean> serverbrandset = this.register(new Setting<>("ServerBrand", false));
        private final Setting<Boolean> memory = this.register(new Setting<>("Memory", false));
        private final Setting<Boolean> playersOnline = this.register(new Setting<>("Players", false));
        private final Setting<Boolean> speed = this.register(new Setting<>("Speed", false));
        private final Setting<Boolean> ping = this.register(new Setting<>("Ping", false));
        private final Setting<Boolean> fps = this.register(new Setting<>("FPS", false));
        private final Setting<Boolean> tps = this.register(new Setting<>("TPS", false));
        private final Setting<Boolean> extraTps = this.register(new Setting<>("ExtraTPS", true, v-> tps.getValue()));
        public Setting<Boolean> time = this.register(new Setting<>("Time", false));
        private final Setting<Boolean> greeter = this.register(new Setting<Boolean>("Greeter", false));
        private final Setting<Greeter> greetermode = this.register(new Setting<Greeter>("GreeterMode", Greeter.NONE, v -> this.greeter.getValue()));
        private final Setting<String> customGreeter = this.register(new Setting<String>("GreeterCustom", "Cracked v2.0 - 1.20.4", v -> this.greetermode.getValue() == Greeter.CUSTOM));
        private Map<String, Integer> players = new HashMap<>();
        private final TimerRadar timer = new TimerRadar();
        private String GV = mc.getGameVersion();
        public static String text;
        public static boolean percent;
        public int color;

  
    public HUD() {
        super("HUD", "hud", Category.CLIENT, true, false, false);
    }

    @Override 
    public void onUpdate() {
        if(timer.passed(updatedelay.getValue())){
            players = getTextRadarMap();
            timer.reset();
        }
    }

    @Override 
    public void onRender2D(Render2DEvent event) {

        int width = mc.getWindow().getScaledWidth();
        int height = mc.getWindow().getScaledHeight();

        color = ColorUtil.toARGB(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue());

        event.getContext().drawTextWithShadow( mc.textRenderer, CrackedClient.NAME + " " + CrackedClient.VERSION, 2,/* x */ 2/* y */, ColorUtil.toARGB(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue()) );
        event.getContext().drawTextWithShadow( mc.textRenderer, mc.player.getDisplayName().getString() , 2, 12, ColorUtil.toARGB(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue()) );
        event.getContext().drawTextWithShadow( mc.textRenderer, GV , 2, 22, ColorUtil.toARGB(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue()) );
        //event.getContext().drawTextWithShadow( mc.textRenderer, HudManager.onlineTime(OyVey.initTime) , 2, 32, ColorUtil.toARGB(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue()) );
        

        //Modules Enable Disable ArrayList Active Modules
        int j = (mc.currentScreen instanceof ChatScreen && !renderingUp.getValue()) ? 14 : 0;
        if (arrayList.getValue())
            if (renderingUp.getValue()) {
                for (Module module : CrackedClient.moduleManager.getEnabledModules().stream().filter(Module::isDrawn).sorted(Comparator.comparing(module -> mc.textRenderer.getWidth(module.getFullArrayString()) * -1)).collect(Collectors.toList())) {
                    if (!module.isDrawn()) {
                        continue;
                    }
                    String str = module.getDisplayName() + Formatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + Formatting.WHITE + module.getDisplayInfo() + Formatting.GRAY + "]") : "");
                    event.getContext().drawTextWithShadow(mc.textRenderer, Text.of(str), (width - 2 - getStringWidth(str)), (2 + j * 10), color);
                    j++;
                }
            } else {
                for (Module module : CrackedClient.moduleManager.getEnabledModules().stream().filter(Module::isDrawn).sorted(Comparator.comparing(module -> mc.textRenderer.getWidth(module.getFullArrayString()) * -1)).collect(Collectors.toList())) {
                    if (!module.isDrawn()) {
                        continue;
                    }
                    String str = module.getDisplayName() + Formatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + Formatting.WHITE + module.getDisplayInfo() + Formatting.GRAY + "]") : "");
                    j += 10;
                    event.getContext().drawTextWithShadow(mc.textRenderer, Text.of(str), (width - 2 - getStringWidth(str)), (height - j), color);
                }
            }

        //info bar chat Ping Brand Time Etc
        int i = (mc.currentScreen instanceof ChatScreen && renderingUp.getValue()) ? 13 : (renderingUp.getValue() ? -2 : 0);
        if (renderingUp.getValue()) {
            if (serverbrandset.getValue()) {
                String str = "ServerBrand " + Formatting.WHITE + CrackedClient.hudManager.getServerBrand();
                i += 10;
                event.getContext().drawTextWithShadow(mc.textRenderer, Text.of(str), (width - getStringWidth(str) - 2), (height - 2 - i), color);
            }
            if (memory.getValue()) {
                long m = Runtime.getRuntime().maxMemory();
                long t = Runtime.getRuntime().totalMemory();
                long f = Runtime.getRuntime().freeMemory();
                long o = t - f;
                String str = "Memory: " + Formatting.WHITE + toMiB(o) + "/" + toMiB(m) + "MB [" + (o * 100L / m) + "%]";
                i += 10;
                event.getContext().drawTextWithShadow(mc.textRenderer, Text.of(str), (width - getStringWidth(str) - 2), (height - 2 - i), color);
            }
            if (speed.getValue()) {
                String str = "Speed " + Formatting.WHITE + CrackedClient.speedManager.getSpeedKpH() + " km/h";
                i += 10;
                event.getContext().drawTextWithShadow(mc.textRenderer, Text.of(str), (width - getStringWidth(str) - 2), (height - 2 - i), color);
            }
            if (tps.getValue()) {
                String str = "TPS " + Formatting.WHITE + CrackedClient.hudManager.getTPS() + (extraTps.getValue() ? " [" + CrackedClient.hudManager.getTPS2() + "]" : "");
                i += 10;
                event.getContext().drawTextWithShadow(mc.textRenderer, Text.of(str), (width - getStringWidth(str) - 2), (height - 2 - i), color);
            }
            if (time.getValue()) {
                String str = "Time " + Formatting.WHITE + (new SimpleDateFormat("h:mm a")).format(new Date());
                i += 10;
                event.getContext().drawTextWithShadow(mc.textRenderer, Text.of(str), (width - getStringWidth(str) - 2), (height - 2 - i), color);
            }
            if (playersOnline.getValue()) {
                String str = "Game: " + Formatting.WHITE + HudManager.onlineTime(CrackedClient.initTime) + "h";
                i += 10;
                event.getContext().drawTextWithShadow(mc.textRenderer, Text.of(str), (width - getStringWidth(str) - 2), (height - 2 - i), color);
            }
            if (playersOnline.getValue()) {
                String str = "Players: " + Formatting.WHITE + (mc.getNetworkHandler().getPlayerList() == null ? 0 : mc.getNetworkHandler().getPlayerList().size());
                i += 10;
                event.getContext().drawTextWithShadow(mc.textRenderer, Text.of(str), (width - getStringWidth(str) - 2), (height - 2 - i), color);
            }
            String fpsText = "FPS " + Formatting.WHITE + FrameRateCounter.INSTANCE.getFps();
            String str1 = "Ping " + Formatting.WHITE + CrackedClient.hudManager.getPing();
            if (getStringWidth(str1) > getStringWidth(fpsText)) {
                if (ping.getValue()) {
                    i += 10;
                    event.getContext().drawTextWithShadow(mc.textRenderer, Text.of(str1), (width - getStringWidth(str1) - 2), (height - 2 - i), color);
                }
                if (fps.getValue()) {
                    i += 10;
                    event.getContext().drawTextWithShadow(mc.textRenderer, Text.of(fpsText), (width - getStringWidth(fpsText) - 2), (height - 2 - i), color);
                }
            } else {
                if (fps.getValue()) {
                    i += 10;
                    event.getContext().drawTextWithShadow(mc.textRenderer, Text.of(fpsText), (width - getStringWidth(fpsText) - 2), (height - 2 - i), color);
                }
                if (ping.getValue()) {
                    i += 10;
                    event.getContext().drawTextWithShadow(mc.textRenderer, Text.of(str1), (width - getStringWidth(str1) - 2), (height - 2 - i), color);
                }
            }
        } else {
            if (serverbrandset.getValue()) {
                String str = "ServerBrand " + Formatting.WHITE + CrackedClient.hudManager.getServerBrand();
                event.getContext().drawTextWithShadow(mc.textRenderer, Text.of(str), (width - getStringWidth(str) - 2), (2 + i++ * 10), color);
            }
            if (memory.getValue()) {
                long m = Runtime.getRuntime().maxMemory();
                long t = Runtime.getRuntime().totalMemory();
                long f = Runtime.getRuntime().freeMemory();
                long o = t - f;
                String str = "Memory: " + Formatting.WHITE + toMiB(o) + "/" + toMiB(m) + "MB [" + (o * 100L / m) + "%]";
                event.getContext().drawTextWithShadow(mc.textRenderer, Text.of(str), (width - getStringWidth(str) - 2), (2 + i++ * 10), color);
            }
            if (speed.getValue()) {
                String str = "Speed " + Formatting.WHITE + CrackedClient.speedManager.getSpeedKpH() + " km/h";
                event.getContext().drawTextWithShadow(mc.textRenderer, Text.of(str), (width - getStringWidth(str) - 2), (2 + i++ * 10), color);
            }
            if (tps.getValue()) {
                String str = "TPS " + Formatting.WHITE + CrackedClient.hudManager.getTPS() + (extraTps.getValue() ? " [" + CrackedClient.hudManager.getTPS2() + "]" : "");
                event.getContext().drawTextWithShadow(mc.textRenderer, Text.of(str), (width - getStringWidth(str) - 2), (2 + i++ * 10), color);
            }
            if (time.getValue()) {
                String str = "Time " + Formatting.WHITE + (new SimpleDateFormat("h:mm a")).format(new Date());
                event.getContext().drawTextWithShadow(mc.textRenderer, Text.of(str), (width - getStringWidth(str) - 2), (2 + i++ * 10), color);
            }
            if (playersOnline.getValue()) {
                String str = "Game: " + Formatting.WHITE + HudManager.onlineTime(CrackedClient.initTime) + "h";
                event.getContext().drawTextWithShadow(mc.textRenderer, Text.of(str), (width - getStringWidth(str) - 2), (2 + i++ * 10), color);
            }
            if (playersOnline.getValue()) {
                String str = "Players: " + Formatting.WHITE + (mc.getNetworkHandler().getPlayerList() == null ? 0 : mc.getNetworkHandler().getPlayerList().size());
                event.getContext().drawTextWithShadow(mc.textRenderer, Text.of(str), (width - getStringWidth(str) - 2), (2 + i++ * 10), color);
            }
            String fpsText = "FPS " + Formatting.WHITE + FrameRateCounter.INSTANCE.getFps();
            String str1 = "Ping " + Formatting.WHITE + CrackedClient.hudManager.getPing();
            if (getStringWidth(str1) > getStringWidth(fpsText)) {
                if (ping.getValue()) {
                    event.getContext().drawTextWithShadow(mc.textRenderer, Text.of(str1), (width - getStringWidth(str1) - 2), (2 + i++ * 10), color);
                }
                if (fps.getValue()) {
                    event.getContext().drawTextWithShadow(mc.textRenderer, Text.of(fpsText), (width - getStringWidth(fpsText) - 2), (2 + i++ * 10), color);
                }
            } else {
                if (fps.getValue()) {
                    event.getContext().drawTextWithShadow(mc.textRenderer, Text.of(fpsText), (width - getStringWidth(fpsText) - 2), (2 + i++ * 10), color);
                }
                if (ping.getValue()) {
                    event.getContext().drawTextWithShadow(mc.textRenderer, Text.of(str1), (width - getStringWidth(str1) - 2), (2 + i++ * 10), color);
                }
            }
        }

        //Direccion Bar Text  X Y Z Cords
        boolean inHell = Objects.equals(mc.world.getRegistryKey().getValue().getPath(), "the_nether");
        int posX = (int) mc.player.getX();
        int posY = (int) mc.player.getY();
        int posZ = (int) mc.player.getZ();
        float nether = !inHell ? 0.125F : 8.0F;
        int hposX = (int) (mc.player.getX() * nether);
        int hposZ = (int) (mc.player.getZ() * nether);
        i = (mc.currentScreen instanceof ChatScreen) ? 14 : 0;
        String coordinates = Formatting.WHITE + "XYZ " + Formatting.RESET + (inHell ? (posX + ", " + posY + ", " + posZ + Formatting.WHITE + " [" + Formatting.RESET + hposX + ", " + hposZ + Formatting.WHITE + "]" + Formatting.RESET) : (posX + ", " + posY + ", " + posZ + Formatting.WHITE + " [" + Formatting.RESET + hposX + ", " + hposZ + Formatting.WHITE + "]"));
        String direction1 = "";

        if(direction.getValue()){
            switch (mc.player.getHorizontalFacing()){
            case EAST -> direction1 = "East" + Formatting.WHITE + " [+X]";
                case WEST -> direction1 = "West" + Formatting.WHITE + " [-X]";
                case NORTH -> direction1 = "North" + Formatting.WHITE + " [-Z]";
                case SOUTH -> direction1 = "South" + Formatting.WHITE + " [+Z]";
            }
        }

        String coords1 = coords.getValue() ? coordinates : "";
        i += 10;

        event.getContext().drawTextWithShadow(mc.textRenderer, Text.of(direction1), 2, (height - i - 11), color);
        event.getContext().drawTextWithShadow(mc.textRenderer, Text.of(coords1), 2, (height - i), color);

        //Greeter 
        if (greeter.getValue()) {
        int widthGreeter = mc.getWindow().getScaledWidth();

            switch (this.greetermode.getValue()) {
                case TIME: {
                    text = TimeDay.getTimeOfDay() + " " + HUD.mc.player.getDisplayName().getString() + "";
                    break;
                }
            }
            switch (this.greetermode.getValue()) {
                case CHRISTMAS: {
                    text = "Cracked Anarchy " + "time remove" + HUD.mc.player.getDisplayName().getString() + " :^)";
                    break;
                }
            }
            switch (this.greetermode.getValue()) {
                case LONG: {
                    text = "Welcome to Cracked " + HUD.mc.player.getDisplayName().getString() + " :^)";
                    break;
                }
            }
            switch (this.greetermode.getValue()) {
                case CUSTOM: {
                    text += this.customGreeter.getValue();
                    break;
                }
            }
            switch (this.greetermode.getValue()) {
                case NAME: {
                    text = "Welcome " + HUD.mc.player.getDisplayName().getString();
                    break;
                }
            }
            switch (this.greetermode.getValue()) {
                case NONE: {
                    text = "";
                    break;
                }
            }

          event.getContext().drawTextWithShadow(mc.textRenderer, Text.of(text), (int) (widthGreeter / 2.0F - getStringWidth(text) / 2.0F + 2.0F), (int) 2.0F, color);
        }

        //Render Totem Inventory Bar Count
        if (totemshud.getValue()) {
        int widthTotem = mc.getWindow().getScaledWidth();
        int heightTotem = mc.getWindow().getScaledHeight();
        int totems = mc.player.getInventory().main.stream().filter(itemStack -> (itemStack.getItem() == Items.TOTEM_OF_UNDYING)).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING)
            totems += mc.player.getOffHandStack().getCount();
        if (totems > 0) {
            int iTotem = widthTotem / 2;
            int yTotem = heightTotem - 55 - ((mc.player.isSubmergedInWater()) ? 10 : 0);
            int xTotem = iTotem - 189 + 180 + 2;
            event.getContext().drawItem(totem, xTotem, yTotem);
            event.getContext().drawItemInSlot(mc.textRenderer,totem, xTotem, yTotem);
            event.getContext().drawCenteredTextWithShadow(mc.textRenderer,totems + "", xTotem + 8, (yTotem - 7), 16777215);
            }
        }

        //RenderArmorHud Bar Inventory Visualizer
        //boolean percent;
        if (armorhud.getValue()) {
        int widthArmor = mc.getWindow().getScaledWidth();
        int heightArmor = mc.getWindow().getScaledHeight();
        int iArmor = widthArmor / 2;
        int iteration = 0;
        int yArmor = heightArmor - 55 - ((mc.player.isSubmergedInWater()) ? 10 : 0);
        for (ItemStack is : mc.player.getInventory().armor) {
            iteration++;
            if (is.isEmpty())
                continue;
            int xArmor = iArmor - 90 + (9 - iteration) * 20 + 2;

            event.getContext().drawItem(is, xArmor, yArmor);
            event.getContext().drawItemInSlot(mc.textRenderer,is, xArmor, yArmor);
            String sArmor = (is.getCount() > 1) ? (is.getCount() + "") : "";
            event.getContext().drawText(mc.textRenderer,Text.of(sArmor), (xArmor + 19 - 2 - getStringWidth(sArmor)), (yArmor + 9), 16777215, true);
            if (percent) {
                float green = (float) (is.getMaxDamage() - is.getDamage()) / (float) is.getMaxDamage();
                float red = 1.0F - green;
                int dmg = 100 - (int) (red * 100.0F);

                event.getContext().drawText(mc.textRenderer,Text.of(dmg + ""), (xArmor + 8 - getStringWidth(dmg + "") / 2), (yArmor - 11), new Color((int) MathUtility.clamp((red * 255.0F), 0, 255f), (int) MathUtility.clamp((green * 255.0F), 0, 255f), 0).getRGB(), true);
            }
          }
        }
    }

    private Map<String, Integer> getTextRadarMap() {
        Map<String, Integer> retval = new HashMap<>();

        DecimalFormat dfDistance = new DecimalFormat("#.#");
        dfDistance.setRoundingMode(RoundingMode.CEILING);
        StringBuilder distanceSB = new StringBuilder();

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player.isInvisible() || player.getName().equals(mc.player.getName())) continue;

            int distanceInt = (int) mc.player.distanceTo(player);
            String distance = dfDistance.format(distanceInt);

            if (distanceInt >= 25) {
                distanceSB.append(Formatting.GREEN);

            } else if (distanceInt > 10) {
                distanceSB.append(Formatting.YELLOW);

            } else {
                distanceSB.append(Formatting.RED);
            }
            distanceSB.append(distance);

            retval.put((health.getValue() ? (getHealthColor(player) + String.valueOf(round2(player.getAbsorptionAmount() + player.getHealth())) + " ") : "") + (CrackedClient.friendManager.isFriend(player) ? Formatting.AQUA : Formatting.RESET) + player.getName().getString() + " " + Formatting.WHITE + "[" + Formatting.RESET + distanceSB + "m" + Formatting.WHITE + "] " + Formatting.GREEN, (int) mc.player.distanceTo(player));

            distanceSB.setLength(0);
        }

        if (!retval.isEmpty()) {
            retval = sortByValue(retval, false);
        }

        return retval;
    }

    public static float round2(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    private Formatting getHealthColor(@NotNull PlayerEntity entity) {
        int health = (int) ((int) entity.getHealth() + entity.getAbsorptionAmount());
        if (health <= 15 && health > 7) return Formatting.YELLOW;
        if (health > 15) return Formatting.GREEN;
        return Formatting.RED;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, boolean descending) {
        LinkedList<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        if (descending) {
            list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        } else {
            list.sort(Map.Entry.comparingByValue());
        }
        LinkedHashMap result = new LinkedHashMap();
        for (Map.Entry entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }


    private int getStringWidth(String str) {
        return mc.textRenderer.getWidth(str);
    }

    private long toMiB(long bytes) {
        return bytes / 1024L / 1024L;
    }

    public enum Greeter {
        NAME,
        TIME,
        CHRISTMAS,
        LONG,
        CUSTOM,
        NONE
    }


}
