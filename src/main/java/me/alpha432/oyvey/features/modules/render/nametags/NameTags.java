package me.alpha432.oyvey.features.modules.render.nametags;

import me.alpha432.oyvey.features.modules.render.nametags.util.EntityUtil;
import me.alpha432.oyvey.features.modules.render.nametags.util.Render2DUtil;
import me.alpha432.oyvey.features.modules.render.nametags.util.TextUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4d;

import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class NameTags extends Module {
    private static NameTags INSTANCE = new NameTags();

    public Setting<Float> scale = this.register(new Setting<Float>("Scale", 0.68f, 0.1f, 2f));
    public Setting<Float> minScale = this.register(new Setting<Float>("MinScale", 0.2f, 0.1f, 1f));
    public Setting<Integer> scaled = this.register(new Setting<Integer>("Scaled", 1, 0, 2));
    public Setting<Float> offset = this.register(new Setting<Float>("Offset", 0.315f, 0.001f, 1f));
    public Setting<Integer> height = this.register(new Setting<Integer>("Height", 0, -3, 3));
    public Setting<Integer> ntD = this.register(new Setting<Integer>("DistanciaNT", 100, 0, 255));

    private final Setting<Boolean> gamemode = this.register(new Setting<Boolean>("Gamemode", false));
    private final Setting<Boolean> ping = this.register(new Setting<Boolean>("Ping", false));
    private final Setting<Boolean> health = this.register(new Setting<Boolean>("Health", true));
    private final Setting<Boolean> distance = this.register(new Setting<Boolean>("Distance", true));
    private final Setting<Boolean> pops = this.register(new Setting<Boolean>("TotemPops", true));
    private final Setting<Boolean> enchants = this.register(new Setting<Boolean>("Enchants", true));

    private final Setting<Boolean> outlineBol = this.register(new Setting<Boolean>("Outline", true));
    private final Setting<Boolean> rectBol = this.register(new Setting<Boolean>("Rect", true));

    private final Setting<Font> font = this.register(new Setting<Font>("Enchants", Font.Fast));
    private final Setting<Float> armorHeight = this.register(new Setting<Float>("ArmorHeight", 0.3f, (-10.0f), 10f));
    private final Setting<Float> armorScale = this.register(new Setting<Float>("ArmorScale", 0.9f, 0.1f, 2f));
    private final Setting<Armor> armorMode = this.register(new Setting<Armor>("ArmorMode", Armor.Full));

    private final Setting<Integer> outliner  = this.register(new Setting<Integer>("OutlineRed", 5, 0, 255));
    private final Setting<Integer> outlineg  = this.register(new Setting<Integer>("OutlineGreen", 5, 0, 255));
    private final Setting<Integer> outlineb  = this.register(new Setting<Integer>("OutlineBlue", 5, 0, 255));
    private final Setting<Integer> outlinea  = this.register(new Setting<Integer>("OutlineAlpha", 5, 0, 255));

    private final Setting<Integer> rectr  = this.register(new Setting<Integer>("RectRed", 5, 0, 255));
    private final Setting<Integer> rectg  = this.register(new Setting<Integer>("RectGreen", 5, 0, 255));
    private final Setting<Integer> rectb  = this.register(new Setting<Integer>("RectBlue", 5, 0, 255));
    private final Setting<Integer> recta  = this.register(new Setting<Integer>("RectAlpha", 5, 0, 255));

    private final Setting<Integer> friendColorr  = this.register(new Setting<Integer>("FriendColorRed", 5, 0, 255));
    private final Setting<Integer> friendColorg  = this.register(new Setting<Integer>("FriendColorGreen", 5, 0, 255));
    private final Setting<Integer> friendColorb  = this.register(new Setting<Integer>("FriendColorBlue", 5, 0, 255));
    private final Setting<Integer> friendColora  = this.register(new Setting<Integer>("FriendColorAlpha", 5, 0, 255));

    private final Setting<Integer> colorr  = this.register(new Setting<Integer>("ColorRed", 5, 0, 255));
    private final Setting<Integer> colorg  = this.register(new Setting<Integer>("ColorGreen", 5, 0, 255));
    private final Setting<Integer> colorb  = this.register(new Setting<Integer>("ColorBlue", 5, 0, 255));
    private final Setting<Integer> colora  = this.register(new Setting<Integer>("ColorAlpha", 5, 0, 255));


    public NameTags() {
        super("NameTags", "NameTags", Module.Category.RENDER, true, false, false);
        this.setInstance();
    }

    public static NameTags getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NameTags();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onRender2D(DrawContext context, float tickDelta) {
        Color outline = new Color(outliner.getValue(),outlineg.getValue(),outlineb.getValue(),outlinea.getValue());
        Color rect = new Color(rectr.getValue(),rectg.getValue(),rectb.getValue(),recta.getValue());
        Color friendColor = new Color(friendColorr.getValue(),friendColorg.getValue(),friendColorb.getValue(),friendColora.getValue());
        Color color = new Color(colorr.getValue(),colorg.getValue(),colorb.getValue(),colora.getValue());

        for (PlayerEntity ent : mc.world.getPlayers()) {
            if (ent == mc.player && mc.options.getPerspective().isFirstPerson() /*&& FreeCam.INSTANCE.isOff()*/) continue;
            double x = ent.prevX + (ent.getX() - ent.prevX) * mc.getTickDelta();
            double y = ent.prevY + (ent.getY() - ent.prevY) * mc.getTickDelta();
            double z = ent.prevZ + (ent.getZ() - ent.prevZ) * mc.getTickDelta();
            Vec3d vector = new Vec3d(x, y + height.getValue() + ent.getBoundingBox().getLengthY() + 0.3, z);
            Vec3d preVec = vector;
            vector = TextUtil.worldSpaceToScreenSpace(new Vec3d(vector.x, vector.y, vector.z));
            if (vector.z > 0 && vector.z < 1) {
                Vector4d position = new Vector4d(vector.x, vector.y, vector.z, 0);
                position.x = Math.min(vector.x, position.x);
                position.y = Math.min(vector.y, position.y);
                position.z = Math.max(vector.x, position.z);

                String final_string = "";

                if (ping.getValue()) {
                    final_string += getEntityPing(ent) + "ms ";
                }
                if (gamemode.getValue()) {
                    final_string += translateGamemode(getEntityGamemode(ent)) + " ";
                }
                final_string += Formatting.RESET + ent.getName().getString();
                if (health.getValue()) {
                    final_string += " " + getHealthColor(ent) + round2(ent.getAbsorptionAmount() + ent.getHealth());
                }
                if (distance.getValue()) {
                    final_string += " " + Formatting.RESET + String.format("%.1f", mc.player.distanceTo(ent)) + "m";
                }
                if (pops.getValue() && CrackedClient.popManager.getPop(ent.getName().getString()) != 0) {
                    final_string += " §bPop" + " " + Formatting.LIGHT_PURPLE + CrackedClient.popManager.getPop(ent.getName().getString());
                }

                double posX = position.x;
                double posY = position.y;
                double endPosX = position.z;

                float diff = (float) (endPosX - posX) / 2;
                float textWidth;

                if (font.getValue() == Font.Fancy) {
                    textWidth = mc.textRenderer.getWidth(final_string);//(FontRenderers.Arial.getWidth(final_string) * 1);
                } else {
                    textWidth = mc.textRenderer.getWidth(final_string);
                }

                float tagX = (float) ((posX + diff - textWidth / 2) * 1);

                ArrayList<ItemStack> stacks = new ArrayList<>();

                stacks.add(ent.getMainHandStack());
                stacks.add(ent.getInventory().armor.get(3));
                stacks.add(ent.getInventory().armor.get(2));
                stacks.add(ent.getInventory().armor.get(1));
                stacks.add(ent.getInventory().armor.get(0));
                stacks.add(ent.getOffHandStack());

                context.getMatrices().push();
                context.getMatrices().translate(tagX - 2 + (textWidth + 4) / 2f, (float) (posY - 13f) + 6.5f, 0);
                float size = (float) Math.max(1 - MathHelper.sqrt((float) mc.cameraEntity.squaredDistanceTo(preVec)) * 0.01 * scaled.getValue(), 0);
                context.getMatrices().scale(Math.max(scale.getValue() * size, minScale.getValue()), Math.max(scale.getValue() * size, minScale.getValue()), 1f);
                context.getMatrices().translate(0, offset.getValue() * MathHelper.sqrt((float) EntityUtil.getEyesPos().squaredDistanceTo(preVec)), 0);
                context.getMatrices().translate(-(tagX - 2 + (textWidth + 4) / 2f), -(float) ((posY - 13f) + 6.5f), 0);

                float item_offset = 0;
                if (armorMode.getValue() != Armor.None) {
                    int count = 0;
                    for (ItemStack armorComponent : stacks) {
                        count++;
                        if (!armorComponent.isEmpty()) {
                            context.getMatrices().push();
                            context.getMatrices().translate(tagX - 2 + (textWidth + 4) / 2f, (float) (posY - 13f) + 6.5f, 0);
                            context.getMatrices().scale(armorScale.getValue(), armorScale.getValue(), 1f);
                            context.getMatrices().translate(-(tagX - 2 + (textWidth + 4) / 2f), -(float) ((posY - 13f) + 6.5f), 0);
                            context.getMatrices().translate(posX - 52.5 + item_offset, (float) (posY - 29f) + armorHeight.getValue(), 0);
                            float durability = armorComponent.getMaxDamage() - armorComponent.getDamage();
                            int percent = (int) ((durability / (float) armorComponent.getMaxDamage()) * 100F);
                            Color colorHealt;// = new Color(0);
                            if (percent <= 33) {
                                colorHealt = Color.RED;
                            } else if (percent <= 66) {
                                colorHealt = Color.ORANGE;
                            } else {
                                colorHealt = Color.GREEN;
                            }
                            switch (armorMode.getValue()) {
                                case OnlyArmor -> {
                                    if (count > 1 && count < 6) {
                                        DiffuseLighting.disableGuiDepthLighting();
                                        context.drawItem(armorComponent, 0, 0);
                                        context.drawItemInSlot(mc.textRenderer, armorComponent, 0, 0);
                                    }
                                }
                                case Item -> {
                                    DiffuseLighting.disableGuiDepthLighting();
                                    context.drawItem(armorComponent, 0, 0);
                                    context.drawItemInSlot(mc.textRenderer, armorComponent, 0, 0);
                                }
                                case Full -> {
                                    DiffuseLighting.disableGuiDepthLighting();
                                    context.drawItem(armorComponent, 0, 0);
                                    context.drawItemInSlot(mc.textRenderer, armorComponent, 0, 0);
                                    if (armorComponent.getMaxDamage() > 0) {
                                        if (font.getValue() == Font.Fancy) {
                                            context.drawText(mc.textRenderer, String.valueOf(percent), 9 - mc.textRenderer.getWidth(String.valueOf(percent)) / 2, -mc.textRenderer.fontHeight + 1, colorHealt.getRGB(), true);//FontRenderers.Arial.drawString(context.getMatrices(), String.valueOf(percent), 9 - FontRenderers.Arial.getWidth(String.valueOf(percent)) / 2, -FontRenderers.Arial.getFontHeight() + 3, color.getRGB());
                                        } else {
                                            context.drawText(mc.textRenderer, String.valueOf(percent), 9 - mc.textRenderer.getWidth(String.valueOf(percent)) / 2, -mc.textRenderer.fontHeight + 1, colorHealt.getRGB(), true);
                                        }
                                    }
                                }
                                case Durability -> {
                                    context.drawItemInSlot(mc.textRenderer, armorComponent, 0, 0);
                                    if (armorComponent.getMaxDamage() > 0) {
                                        if (!armorComponent.isItemBarVisible()) {
                                            int i = armorComponent.getItemBarStep();
                                            int j = armorComponent.getItemBarColor();
                                            int k = 2;
                                            int l = 13;
                                            context.fill(RenderLayer.getGuiOverlay(), k, l, k + 13, l + 2, -16777216);
                                            context.fill(RenderLayer.getGuiOverlay(), k, l, k + i, l + 1, j | -16777216);
                                        }
                                        if (font.getValue() == Font.Fancy) {
                                            //FontRenderers.Arial.drawString(context.getMatrices(), String.valueOf(percent), 9 - FontRenderers.Arial.getWidth(String.valueOf(percent)) / 2, 7, color.getRGB());//FontRenderers.Arial.drawString(context.getMatrices(), String.valueOf(percent), 9 - FontRenderers.Arial.getWidth(String.valueOf(percent)) / 2, 7, color.getRGB());
                                            context.drawText(mc.textRenderer, String.valueOf(percent), 9 - mc.textRenderer.getWidth(String.valueOf(percent)) / 2, 5, color.getRGB(), true);
                                        } else {
                                            context.drawText(mc.textRenderer, String.valueOf(percent), 9 - mc.textRenderer.getWidth(String.valueOf(percent)) / 2, 5, color.getRGB(), true);
                                        }
                                    }
                                }
                            }
                            context.getMatrices().pop();

                            if (this.enchants.getValue()) {
                                float enchantmentY = 0;
                                NbtList enchants = armorComponent.getEnchantments();
                                for (int index = 0; index < enchants.size(); ++index) {
                                    String id = enchants.getCompound(index).getString("id");
                                    short level = enchants.getCompound(index).getShort("lvl");
                                    String encName;
                                    switch (id) {
                                        case "minecraft:blast_protection" -> encName = "B" + level;
                                        case "minecraft:protection" -> encName = "P" + level;
                                        case "minecraft:thorns" -> encName = "T" + level;
                                        case "minecraft:sharpness" -> encName = "S" + level;
                                        case "minecraft:efficiency" -> encName = "E" + level;
                                        case "minecraft:unbreaking" -> encName = "U" + level;
                                        case "minecraft:power" -> encName = "PO" + level;
                                        default -> {
                                            continue;
                                        }
                                    }

                                    if (font.getValue() == Font.Fancy) {
                                        //FontRenderers.Arial.drawString(context.getMatrices(), encName, posX - 50 + item_offset, (float) posY - 45 + enchantmentY, -1);
                                        context.getMatrices().push();
                                        context.getMatrices().translate((posX - 50f + item_offset), (posY - 45f + enchantmentY), 0);
                                        context.drawText(mc.textRenderer, encName, 0, 0, -1, true);
                                        context.getMatrices().pop();
                                    } else {
                                        context.getMatrices().push();
                                        context.getMatrices().translate((posX - 50f + item_offset), (posY - 45f + enchantmentY), 0);
                                        context.drawText(mc.textRenderer, encName, 0, 0, -1, true);
                                        context.getMatrices().pop();
                                    }
                                    enchantmentY -= 8;
                                }
                            }
                        }
                        item_offset += 18f;
                    }
                }
                if (rectBol.getValue()) {
                    Render2DUtil.drawRect(context.getMatrices(), tagX - 2, (float) (posY - 13f), textWidth + 4, 11, rect);
                }
                if (outlineBol.getValue()) {
                    Render2DUtil.drawRect(context.getMatrices(), tagX - 3, (float) (posY - 14f), textWidth + 6, 1, outline);
                    Render2DUtil.drawRect(context.getMatrices(), tagX - 3, (float) (posY - 2f), textWidth + 6, 1, outline);
                    Render2DUtil.drawRect(context.getMatrices(), tagX - 3, (float) (posY - 14f), 1, 12, outline);
                    Render2DUtil.drawRect(context.getMatrices(), tagX + textWidth + 2, (float) (posY - 14f), 1, 12, outline);
                }
                if (font.getValue() == Font.Fancy) {
                    //FontRenderers.Arial.drawString(context.getMatrices(), final_string, tagX, (float) posY - 10, OyVey.friendManager.isFriend(ent) ? friendColor.getValue().getRGB() : this.color.getValue().getRGB());
                    context.getMatrices().push();
                    context.getMatrices().translate(tagX, ((float) posY - 11), 0);
                    context.drawText(mc.textRenderer, final_string, 0, 0, CrackedClient.friendManager.isFriend(ent) ? friendColor.getRGB() : color.getRGB(), true);
                    context.getMatrices().pop();
                } else {
                    context.getMatrices().push();
                    context.getMatrices().translate(tagX, ((float) posY - 11), 0);
                    context.drawText(mc.textRenderer, final_string, 0, 0, CrackedClient.friendManager.isFriend(ent) ? friendColor.getRGB() : color.getRGB(), true);
                    context.getMatrices().pop();
                }
                context.getMatrices().pop();
            }
        }
    }

    public static String getEntityPing(PlayerEntity entity) {
        if (mc.getNetworkHandler() == null) return "-1";
        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(entity.getUuid());
        if (playerListEntry == null) return "-1";
        int ping = playerListEntry.getLatency();
        Formatting color = Formatting.GREEN;
        if (ping >= 100) {
            color = Formatting.YELLOW;
        }
        if (ping >= 250) {
            color = Formatting.RED;
        }
        return color.toString() + ping;
    }

    public static GameMode getEntityGamemode(PlayerEntity entity) {
        if (entity == null) return null;
        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(entity.getUuid());
        return playerListEntry == null ? null : playerListEntry.getGameMode();
    }

    private String translateGamemode(GameMode gamemode) {
        if (gamemode == null) return "§7[BOT]";
        return switch (gamemode) {
            case SURVIVAL -> "§b[S]";
            case CREATIVE -> "§c[C]";
            case SPECTATOR -> "§7[SP]";
            case ADVENTURE -> "§e[A]";
        };
    }

    private Formatting getHealthColor(@NotNull PlayerEntity entity) {
        int health = (int) ((int) entity.getHealth() + entity.getAbsorptionAmount());
        if (health >= 30) {
            return Formatting.DARK_GREEN;
        }
        if (health >= 24) {
            return Formatting.GREEN;
        }
        if (health >= 18) {
            return Formatting.YELLOW;
        }
        if (health >= 12) {
            return Formatting.GOLD;
        }
        if (health >= 6) {
            return Formatting.RED;
        }
        return Formatting.DARK_RED;
    }

    public static float round2(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    public enum Font {
        Fancy, Fast
    }

    public enum Armor {
        None, Full, Durability, Item, OnlyArmor
    }
}
    /*
    public NameTags() {
        super("NameTags", "NameTags", Module.Category.RENDER, true, false, false);
        INSTANCE = this;
    }

    @Override
    public void onRender2D(DrawContext context, float tickDelta) {
    //public void onPreRender2D(DrawContext context) {
        Color outline = new Color(outliner.getValue(),outlineg.getValue(),outlineb.getValue(),outlinea.getValue());
        Color rect = new Color(rectr.getValue(),rectg.getValue(),rectb.getValue(),recta.getValue());
        Color friendColor = new Color(friendColorr.getValue(),friendColorg.getValue(),friendColorb.getValue(),friendColora.getValue());
        Color color = new Color(colorr.getValue(),colorg.getValue(),colorb.getValue(),colora.getValue());
        for (PlayerEntity ent : mc.world.getPlayers()) {

            Vec3d[] vectors = getPoints(ent);

            Vector4d position = null;
            for (Vec3d vector : vectors) {
                vector = RenderUtils.worldSpaceToScreenSpace(new Vec3d(vector.x, vector.y, vector.z));
                if (vector.z > 0 && vector.z < ntD.getValue()) {
                    if (position == null)
                        position = new Vector4d(vector.x, vector.y, vector.z, 0);
                    position.x = Math.min(vector.x, position.x);
                    position.y = Math.min(vector.y, position.y);
                    position.z = Math.max(vector.x, position.z);
                    position.w = Math.max(vector.y, position.w);
                }
            }

            if (ent == mc.player && mc.options.getPerspective().isFirstPerson() /*&& FreeCam.INSTANCE.isOff()*) continue;
            double x = ent.prevX + (ent.getX() - ent.prevX) * mc.getTickDelta();
            double y = ent.prevY + (ent.getY() - ent.prevY) * mc.getTickDelta();
            double z = ent.prevZ + (ent.getZ() - ent.prevZ) * mc.getTickDelta();
            Vec3d vector = new Vec3d(x, y + height.getValue() + ent.getBoundingBox().getLengthY() + 0.3, z);
            //Vec3d vector = new Vec3d(x, y + ent.getEyeHeight(ent.getPose()) + ent.getBoundingBox().getLengthY() + 0.3, z);
            Vec3d preVec = vector;
            vector = TextUtil.worldSpaceToScreenSpace(new Vec3d(vector.x, vector.y, vector.z));
            if (position != nullvector.z > 0 && vector.z < ntD.getValue()) {
                //Vector4d position = new Vector4d(vector.x, vector.y, vector.z, 0);
                //position.x = Math.min(vector.x, position.x);
                //position.y = Math.min(vector.y, position.y);
                //position.z = Math.max(vector.x, position.z);

                if (position == null) continue;//test
                if (mc.getNetworkHandler().getPlayerListEntry(ent.getUuid()) == null) continue;

                String final_string = "";

                if (ping.getValue()) {
                    final_string += getEntityPing(ent) + "ms ";
                }
                if (gamemode.getValue()) {
                    final_string += translateGamemode(getEntityGamemode(ent)) + " ";
                }
                final_string += Formatting.RESET + ent.getName().getString();
                if (health.getValue()) {
                    final_string += " " + getHealthColor(ent) + round2(ent.getAbsorptionAmount() + ent.getHealth());
                }
                if (distance.getValue()) {
                    final_string += " " + Formatting.RESET + String.format("%.1f", mc.player.distanceTo(ent)) + "m";
                }
                if (pops.getValue() && OyVey.popManager.getPop(ent.getName().getString()) != 0) {
                    final_string += " §bPop" + " " + Formatting.LIGHT_PURPLE + OyVey.popManager.getPop(ent.getName().getString());
                }

                float posX = (float) position.x;
                float posY = (float) position.y;
                float endPosX = (float) position.z;
                /*
                double posX = position.x;
                double posY = position.y;
                double endPosX = position.z;
                 *

                float diff = (float) (endPosX - posX) / 2;
                float textWidth;

                if (font.getValue() == Font.Fancy) {
                    //textWidth = (FontRenderers.Arial.getWidth(final_string) * 1);
                    textWidth = mc.textRenderer.getWidth(final_string);
                } else {
                    textWidth = mc.textRenderer.getWidth(final_string);
                }

                float tagX = (float) ((posX + diff - textWidth / 2) * 1);

                ArrayList<ItemStack> stacks = new ArrayList<>();

                stacks.add(ent.getMainHandStack());
                stacks.add(ent.getInventory().armor.get(3));
                stacks.add(ent.getInventory().armor.get(2));
                stacks.add(ent.getInventory().armor.get(1));
                stacks.add(ent.getInventory().armor.get(0));
                stacks.add(ent.getOffHandStack());

                context.getMatrices().push();
                context.getMatrices().translate(tagX - 2 + (textWidth + 4) / 2f, (float) (posY - 13f) + 6.5f, 0);
                float size = (float) Math.max(1 - MathHelper.sqrt((float) mc.cameraEntity.squaredDistanceTo(preVec)) * 0.01 * scaled.getValue(), 0);
                context.getMatrices().scale(Math.max(scale.getValue() * size, minScale.getValue()), Math.max(scale.getValue() * size, minScale.getValue()), 1f);
                context.getMatrices().translate(0, offset.getValue() * MathHelper.sqrt((float) getEyesPos().squaredDistanceTo(preVec)), 0);
                context.getMatrices().translate(-(tagX - 2 + (textWidth + 4) / 2f), -(float) ((posY - 13f) + 6.5f), 0);

                float item_offset = 0;
                if (armorMode.getValue() != Armor.None) {
                    int count = 0;
                    for (ItemStack armorComponent : stacks) {
                        count++;
                        if (!armorComponent.isEmpty()) {
                            context.getMatrices().push();
                            context.getMatrices().translate(tagX - 2 + (textWidth + 4) / 2f, (float) (posY - 13f) + 6.5f, 0);
                            context.getMatrices().scale(armorScale.getValue(), armorScale.getValue(), 1f);
                            context.getMatrices().translate(-(tagX - 2 + (textWidth + 4) / 2f), -(float) ((posY - 13f) + 6.5f), 0);
                            context.getMatrices().translate(posX - 52.5 + item_offset, (float) (posY - 29f) + armorHeight.getValue(), 0);
                            float durability = armorComponent.getMaxDamage() - armorComponent.getDamage();
                            int percent = (int) ((durability / (float) armorComponent.getMaxDamage()) * 100F);
                            //Color color;
                            if (percent <= 33) {
                                color = Color.RED;
                            } else if (percent <= 66) {
                                color = Color.ORANGE;
                            } else {
                                color = Color.GREEN;
                            }
                            switch (armorMode.getValue()) {
                                case OnlyArmor -> {
                                    if (count > 1 && count < 6) {
                                        DiffuseLighting.disableGuiDepthLighting();
                                        context.drawItem(armorComponent, 0, 0);
                                        context.drawItemInSlot(mc.textRenderer, armorComponent, 0, 0);
                                    }
                                }
                                case Item -> {
                                    DiffuseLighting.disableGuiDepthLighting();
                                    context.drawItem(armorComponent, 0, 0);
                                    context.drawItemInSlot(mc.textRenderer, armorComponent, 0, 0);
                                }
                                case Full -> {
                                    DiffuseLighting.disableGuiDepthLighting();
                                    context.drawItem(armorComponent, 0, 0);
                                    context.drawItemInSlot(mc.textRenderer, armorComponent, 0, 0);
                                    if (armorComponent.getMaxDamage() > 0) {
                                        if (font.getValue() == Font.Fancy) {
                                            //FontRenderers.Arial.drawString(context.getMatrices(), String.valueOf(percent), 9 - FontRenderers.Arial.getWidth(String.valueOf(percent)) / 2, -FontRenderers.Arial.getFontHeight() + 3, color.getRGB());
                                            context.drawText(mc.textRenderer, String.valueOf(percent), 9 - mc.textRenderer.getWidth(String.valueOf(percent)) / 2, -mc.textRenderer.fontHeight + 1, color.getRGB(), true);
                                        } else {
                                            context.drawText(mc.textRenderer, String.valueOf(percent), 9 - mc.textRenderer.getWidth(String.valueOf(percent)) / 2, -mc.textRenderer.fontHeight + 1, color.getRGB(), true);
                                        }
                                    }
                                }
                                case Durability -> {
                                    context.drawItemInSlot(mc.textRenderer, armorComponent, 0, 0);
                                    if (armorComponent.getMaxDamage() > 0) {
                                        if (!armorComponent.isItemBarVisible()) {
                                            int i = armorComponent.getItemBarStep();
                                            int j = armorComponent.getItemBarColor();
                                            int k = 2;
                                            int l = 13;
                                            context.fill(RenderLayer.getGuiOverlay(), k, l, k + 13, l + 2, -16777216);
                                            context.fill(RenderLayer.getGuiOverlay(), k, l, k + i, l + 1, j | -16777216);
                                        }
                                        if (font.getValue() == Font.Fancy) {
                                            //FontRenderers.Arial.drawString(context.getMatrices(), String.valueOf(percent), 9 - FontRenderers.Arial.getWidth(String.valueOf(percent)) / 2, 7, color.getRGB());
                                            context.drawText(mc.textRenderer, String.valueOf(percent), 9 - mc.textRenderer.getWidth(String.valueOf(percent)) / 2, 5, color.getRGB(), true);
                                        } else {
                                            context.drawText(mc.textRenderer, String.valueOf(percent), 9 - mc.textRenderer.getWidth(String.valueOf(percent)) / 2, 5, color.getRGB(), true);
                                        }
                                    }
                                }
                            }
                            context.getMatrices().pop();

                            if (this.enchants.getValue()) {
                                float enchantmentY = 0;
                                NbtList enchants = armorComponent.getEnchantments();
                                for (int index = 0; index < enchants.size(); ++index) {
                                    String id = enchants.getCompound(index).getString("id");
                                    short level = enchants.getCompound(index).getShort("lvl");
                                    String encName;
                                    switch (id) {
                                        case "minecraft:blast_protection" -> encName = "B" + level;
                                        case "minecraft:protection" -> encName = "P" + level;
                                        case "minecraft:thorns" -> encName = "T" + level;
                                        case "minecraft:sharpness" -> encName = "S" + level;
                                        case "minecraft:efficiency" -> encName = "E" + level;
                                        case "minecraft:unbreaking" -> encName = "U" + level;
                                        case "minecraft:power" -> encName = "PO" + level;
                                        default -> {
                                            continue;
                                        }
                                    }

                                    if (font.getValue() == Font.Fancy) {
                                        //FontRenderers.Arial.drawString(context.getMatrices(), encName, posX - 50 + item_offset, (float) posY - 45 + enchantmentY, -1);
                                        context.getMatrices().push();
                                        context.getMatrices().translate((posX - 50f + item_offset), (posY - 45f + enchantmentY), 0);
                                        context.drawText(mc.textRenderer, encName, 0, 0, -1, true);
                                        context.getMatrices().pop();
                                    } else {
                                        context.getMatrices().push();
                                        context.getMatrices().translate((posX - 50f + item_offset), (posY - 45f + enchantmentY), 0);
                                        context.drawText(mc.textRenderer, encName, 0, 0, -1, true);
                                        context.getMatrices().pop();
                                    }
                                    enchantmentY -= 8;
                                }
                            }
                        }
                        item_offset += 18f;
                    }
                }
                if (rectBol.getValue()) {
                    Render2DUtil.drawRect(context.getMatrices(), tagX - 2, (float) (posY - 13f), textWidth + 4, 11, rect);
                }
                if (outlineBol.getValue()) {
                    Render2DUtil.drawRect(context.getMatrices(), tagX - 3, (float) (posY - 14f), textWidth + 6, 1, outline);
                    Render2DUtil.drawRect(context.getMatrices(), tagX - 3, (float) (posY - 2f), textWidth + 6, 1, outline);
                    Render2DUtil.drawRect(context.getMatrices(), tagX - 3, (float) (posY - 14f), 1, 12, outline);
                    Render2DUtil.drawRect(context.getMatrices(), tagX + textWidth + 2, (float) (posY - 14f), 1, 12, outline);
                }
                if (font.getValue() == Font.Fancy) {
                    //FontRenderers.Arial.drawString(context.getMatrices(), final_string, tagX, (float) posY - 10, OyVey.friendManager.isFriend(ent) ? friendColor.getValue().getRGB() : this.color.getValue().getRGB());
                    context.getMatrices().push();
                    context.getMatrices().translate(tagX, ((float) posY - 11), 0);
                    context.drawText(mc.textRenderer, final_string, 0, 0, OyVey.friendManager.isFriend(ent) ? friendColor.getRGB() : color.getRGB(), true);
                    context.getMatrices().pop();
                } else {
                    context.getMatrices().push();
                    context.getMatrices().translate(tagX, ((float) posY - 11), 0);
                    context.drawText(mc.textRenderer, final_string, 0, 0, OyVey.friendManager.isFriend(ent) ? friendColor.getRGB() : color.getRGB(), true);
                    context.getMatrices().pop();
                }
                context.getMatrices().pop();
            }
        }
    }

    public static String getEntityPing(PlayerEntity entity) {
        if (mc.getNetworkHandler() == null) return "-1";
        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(entity.getUuid());
        if (playerListEntry == null) return "-1";
        int ping = playerListEntry.getLatency();
        Formatting color = Formatting.GREEN;
        if (ping >= 100) {
            color = Formatting.YELLOW;
        }
        if (ping >= 250) {
            color = Formatting.RED;
        }
        return color.toString() + ping;
    }

    public static GameMode getEntityGamemode(PlayerEntity entity) {
        if (entity == null) return null;
        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(entity.getUuid());
        return playerListEntry == null ? null : playerListEntry.getGameMode();
    }

    private String translateGamemode(GameMode gamemode) {
        if (gamemode == null) return "§7[BOT]";
        return switch (gamemode) {
            case SURVIVAL -> "§b[S]";
            case CREATIVE -> "§c[C]";
            case SPECTATOR -> "§7[SP]";
            case ADVENTURE -> "§e[A]";
        };
    }

    private Formatting getHealthColor(@NotNull PlayerEntity entity) {
        int health = (int) ((int) entity.getHealth() + entity.getAbsorptionAmount());
        if (health >= 30) {
            return Formatting.DARK_GREEN;
        }
        if (health >= 24) {
            return Formatting.GREEN;
        }
        if (health >= 18) {
            return Formatting.YELLOW;
        }
        if (health >= 12) {
            return Formatting.GOLD;
        }
        if (health >= 6) {
            return Formatting.RED;
        }
        return Formatting.DARK_RED;
    }

    public static Vec3d getEyesPos() {
        return mc.player.getEyePos();
    }

        @NotNull
    private static Vec3d[] getPoints(Entity ent) {
        double x = ent.prevX + (ent.getX() - ent.prevX) * mc.getTickDelta();
        double y = ent.prevY + (ent.getY() - ent.prevY) * mc.getTickDelta();
        double z = ent.prevZ + (ent.getZ() - ent.prevZ) * mc.getTickDelta();
        Box axisAlignedBB2 = ent.getBoundingBox();
        Box axisAlignedBB = new Box(axisAlignedBB2.minX - ent.getX() + x - 0.05, axisAlignedBB2.minY - ent.getY() + y, axisAlignedBB2.minZ - ent.getZ() + z - 0.05, axisAlignedBB2.maxX - ent.getX() + x + 0.05, axisAlignedBB2.maxY - ent.getY() + y + 0.15, axisAlignedBB2.maxZ - ent.getZ() + z + 0.05);
        Vec3d[] vectors = new Vec3d[]{new Vec3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ)};
        return vectors;
    }

    public static float round2(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    public enum Font {
        Fancy, Fast
    }

    public enum Armor {
        None, Full, Durability, Item, OnlyArmor
    }
}*/