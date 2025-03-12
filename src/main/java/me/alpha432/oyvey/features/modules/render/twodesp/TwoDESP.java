package me.alpha432.oyvey.features.modules.render.twodesp;

import com.mojang.blaze3d.systems.RenderSystem;
import org.jetbrains.annotations.NotNull;
import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.render.nametags.util.TextUtil;
import me.alpha432.oyvey.features.modules.render.twodesp.util.Render2DUtil;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.entity.*;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector4d;

import java.awt.*;
import java.util.ArrayList;


public class TwoDESP extends Module {
    public TwoDESP() {
        super("Esp2D", "Esp2D", Module.Category.RENDER, true, false, false);
    }

    public Setting<Page> page = this.register(new Setting<>("Settings", Page.Target));

    public Setting<Boolean> outline = this.register(new Setting<>("Outline",true, v -> page.getValue() == Page.Setting));
    public Setting<Boolean> renderHealth = this.register(new Setting<>("renderHealth",true, v -> page.getValue() == Page.Setting));
    public Setting<Boolean> renderArmor = this.register(new Setting<>("Armor Dura",true, v -> page.getValue() == Page.Setting));

    public Setting<Integer> durascale = this.register(new Setting<>("DuraScale", 1, 0, 2, v -> renderArmor.getValue()));

    public Setting<Boolean> drawItem = this.register(new Setting<>("draw Item Name",true, v -> page.getValue() == Page.Setting));
    public Setting<Boolean> drawItemC = this.register(new Setting<>("draw Item Count",true, v -> page.getValue() == Page.Setting  && drawItem.getValue() ));
    public Setting<Boolean> font = this.register(new Setting<>("CustomFont",true, v -> page.getValue() == Page.Setting));

    public Setting<Boolean> players = this.register(new Setting<>("Players",true, v -> page.getValue() == Page.Target));
    public Setting<Boolean> friends = this.register(new Setting<>("Friends",true, v -> page.getValue() == Page.Target));
    public Setting<Boolean> crystals = this.register(new Setting<>("Crystals",true, v -> page.getValue() == Page.Target));
    public Setting<Boolean> creatures = this.register(new Setting<>("Creatures",true, v -> page.getValue() == Page.Target));
    public Setting<Boolean> monsters = this.register(new Setting<>("Monsters",true, v -> page.getValue() == Page.Target));
    public Setting<Boolean> ambients = this.register(new Setting<>("Ambients",true, v -> page.getValue() == Page.Target));
    public Setting<Boolean> others = this.register(new Setting<>("Others",true, v -> page.getValue() == Page.Target));


    public Setting<Integer> colorR1 = this.register(new Setting<>("PlayerRed", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorG1 = this.register(new Setting<>("PlayerGreen", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorB1 = this.register(new Setting<>("PlayerBlue", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorA1 = this.register(new Setting<>("PlayerAlpha", 1, 0, 255, v -> page.getValue() == Page.Color));

    public Setting<Integer> colorR12 = this.register(new Setting<>("FriendsRed", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorG12 = this.register(new Setting<>("FriendsGreen", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorB12 = this.register(new Setting<>("FriendsBlue", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorA12 = this.register(new Setting<>("FriendsAlpha", 1, 0, 255, v -> page.getValue() == Page.Color));

    public Setting<Integer> colorR13 = this.register(new Setting<>("CrystalsRed", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorG13 = this.register(new Setting<>("CrystalsGreen", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorB13 = this.register(new Setting<>("CrystalsBlue", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorA13 = this.register(new Setting<>("CrystalsAlpha", 1, 0, 255, v -> page.getValue() == Page.Color));

    public Setting<Integer> colorR14 = this.register(new Setting<>("CreaturesRed", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorG14 = this.register(new Setting<>("CreaturesGreen", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorB14 = this.register(new Setting<>("CreaturesBlue", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorA14 = this.register(new Setting<>("CreaturesAlpha", 1, 0, 255, v -> page.getValue() == Page.Color));

    public Setting<Integer> colorR15 = this.register(new Setting<>("MonstersRed", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorG15 = this.register(new Setting<>("MonstersGreen", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorB15 = this.register(new Setting<>("MonstersBlue", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorA15 = this.register(new Setting<>("MonstersAlpha", 1, 0, 255, v -> page.getValue() == Page.Color));

    public Setting<Integer> colorR16 = this.register(new Setting<>("AmbientsRed", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorG16 = this.register(new Setting<>("AmbientsGreen", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorB16 = this.register(new Setting<>("AmbientsBlue", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorA16 = this.register(new Setting<>("AmbientsAlpha", 1, 0, 255, v -> page.getValue() == Page.Color));

    public Setting<Integer> colorR17 = this.register(new Setting<>("OthersRed", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorG17 = this.register(new Setting<>("OthersGreen", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorB17 = this.register(new Setting<>("OthersBlue", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorA17 = this.register(new Setting<>("OthersAlpha", 1, 0, 255, v -> page.getValue() == Page.Color));

    public Setting<Integer> colorR18 = this.register(new Setting<>("ArmorDuraRed", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorG18 = this.register(new Setting<>("ArmorDuraGreen", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorB18 = this.register(new Setting<>("ArmorDuraBlue", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorA18 = this.register(new Setting<>("ArmorDuraAlpha", 1, 0, 255, v -> page.getValue() == Page.Color));

    public Setting<Integer> colorR19 = this.register(new Setting<>("ItemNameRed", 1, 0, 255, v -> page.getValue() == Page.Color && drawItem.getValue()));
    public Setting<Integer> colorG19 = this.register(new Setting<>("ItemNameGreen", 1, 0, 255, v -> page.getValue() == Page.Color && drawItem.getValue()));
    public Setting<Integer> colorB19 = this.register(new Setting<>("ItemNameBlue", 1, 0, 255, v -> page.getValue() == Page.Color && drawItem.getValue()));
    public Setting<Integer> colorA19 = this.register(new Setting<>("ItemNameAlpha", 1, 0, 255, v -> page.getValue() == Page.Color && drawItem.getValue()));

    public Setting<Integer> colorR10 = this.register(new Setting<>("ItemCountRed", 1, 0, 255, v -> page.getValue() == Page.Color && drawItemC.getValue()));
    public Setting<Integer> colorG10 = this.register(new Setting<>("ItemCountGreen", 1, 0, 255, v -> page.getValue() == Page.Color && drawItemC.getValue()));
    public Setting<Integer> colorB10 = this.register(new Setting<>("ItemCountBlue", 1, 0, 255, v -> page.getValue() == Page.Color && drawItemC.getValue()));
    public Setting<Integer> colorA10 = this.register(new Setting<>("ItemCountAlpha", 1, 0, 255, v -> page.getValue() == Page.Color && drawItemC.getValue()));

    public Setting<Integer> colorR11 = this.register(new Setting<>("HighHealthRed", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorG11 = this.register(new Setting<>("HighHealthGreen", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorB11 = this.register(new Setting<>("HighHealthBlue", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorA11 = this.register(new Setting<>("HighHealthAlpha", 1, 0, 255, v -> page.getValue() == Page.Color));

    public Setting<Integer> colorR112 = this.register(new Setting<>("MidHealthRed", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorG112 = this.register(new Setting<>("PlayerGreen", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorB112 = this.register(new Setting<>("MidHealthBlue", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorA112 = this.register(new Setting<>("MidHealthAlpha", 1, 0, 255, v -> page.getValue() == Page.Color));

    public Setting<Integer> colorR113 = this.register(new Setting<>("LowHealthRed", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorG113 = this.register(new Setting<>("LowHealthGreen", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorB113 = this.register(new Setting<>("LowHealthBlue", 1, 0, 255, v -> page.getValue() == Page.Color));
    public Setting<Integer> colorA113 = this.register(new Setting<>("LowHealthAlpha", 1, 0, 255, v -> page.getValue() == Page.Color));

    /*
    public Color playersC = new Color(colorR1.getValue(),colorG1.getValue(),colorB1.getValue(),colorA1.getValue());
    public Color friendsC = new Color(colorR12.getValue(),colorG12.getValue(),colorB12.getValue(),colorA12.getValue());
    public Color crystalsC = new Color(colorR13.getValue(),colorG13.getValue(),colorB13.getValue(),colorA13.getValue());
    public Color creaturesC = new Color(colorR14.getValue(),colorG14.getValue(),colorB14.getValue(),colorA14.getValue());
    public Color monstersC = new Color(colorR15.getValue(),colorG15.getValue(),colorB15.getValue(),colorA15.getValue());
    public Color ambientsC = new Color(colorR16.getValue(),colorG16.getValue(),colorB16.getValue(),colorA16.getValue());
    public Color othersC = new Color(colorR17.getValue(),colorG17.getValue(),colorB17.getValue(),colorA17.getValue());
    public Color armorDuraColor = new Color(colorR18.getValue(),colorG18.getValue(),colorB18.getValue(),colorA18.getValue());
    public Color textcolor = new Color(colorR19.getValue(),colorG19.getValue(),colorB19.getValue(),colorA19.getValue());
    public Color countColor = new Color(colorR10.getValue(),colorG10.getValue(),colorB10.getValue(),colorA10.getValue());
    public Color hHealth = new Color(colorR11.getValue(),colorG11.getValue(),colorB11.getValue(),colorA11.getValue());
    public Color mHealth = new Color(colorR112.getValue(),colorG112.getValue(),colorB112.getValue(),colorA112.getValue());
    public Color lHealth = new Color(colorR113.getValue(),colorG113.getValue(),colorB113.getValue(),colorA113.getValue());
    */
    @Override
    public void onRender2D(DrawContext context, float tickDelta) {
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        Render2DUtil.setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        for (Entity ent : mc.world.getEntities()) {
            if (shouldRender(ent))
                drawBox(bufferBuilder, ent, matrix,context);
        }
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        Render2DUtil.endRender();

        for (Entity ent : mc.world.getEntities()) {
            if (shouldRender(ent))
                drawText(ent, context);
        }
    }

    public boolean shouldRender(Entity entity) {
        if (entity == null)
            return false;

        if (mc.player == null)
            return false;

        if (entity instanceof PlayerEntity) {
            if (entity == mc.player && mc.options.getPerspective().isFirstPerson()) {
                return false;
            }
            if (CrackedClient.friendManager.isFriend((PlayerEntity) entity))
                return friends.getValue();
            return players.getValue();
        }

        if (entity instanceof EndCrystalEntity)
            return crystals.getValue();

        return switch (entity.getType().getSpawnGroup()) {
            case CREATURE, WATER_CREATURE -> creatures.getValue();
            case MONSTER -> monsters.getValue();
            case AMBIENT, WATER_AMBIENT -> ambients.getValue();
            default -> others.getValue();
        };
    }

    public Color getEntityColor(Entity entity) {
    Color playersC = new Color(colorR1.getValue(),colorG1.getValue(),colorB1.getValue(),colorA1.getValue());
    Color friendsC = new Color(colorR12.getValue(),colorG12.getValue(),colorB12.getValue(),colorA12.getValue());
    Color creaturesC = new Color(colorR14.getValue(),colorG14.getValue(),colorB14.getValue(),colorA14.getValue());
    Color monstersC = new Color(colorR15.getValue(),colorG15.getValue(),colorB15.getValue(),colorA15.getValue());
    Color ambientsC = new Color(colorR16.getValue(),colorG16.getValue(),colorB16.getValue(),colorA16.getValue());
    Color othersC = new Color(colorR17.getValue(),colorG17.getValue(),colorB17.getValue(),colorA17.getValue());
    Color crystalsC = new Color(colorR13.getValue(),colorG13.getValue(),colorB13.getValue(),colorA13.getValue());

        if (entity == null)
            return new Color(-1);

        if (entity instanceof PlayerEntity) {
            if (CrackedClient.friendManager.isFriend((PlayerEntity) entity))
                return friendsC;
            return playersC;
        }

        if (entity instanceof EndCrystalEntity)
            return crystalsC;

        return switch (entity.getType().getSpawnGroup()) {
            case CREATURE, WATER_CREATURE -> creaturesC;
            case MONSTER -> monstersC;
            case AMBIENT, WATER_AMBIENT -> ambientsC;
            default -> othersC;
        };
    }

    public void drawBox(BufferBuilder bufferBuilder, @NotNull Entity ent, Matrix4f matrix, DrawContext context) {
        Color armorDuraColor = new Color(colorR18.getValue(),colorG18.getValue(),colorB18.getValue(),colorA18.getValue());
        double x = ent.prevX + (ent.getX() - ent.prevX) * mc.getTickDelta();
        double y = ent.prevY + (ent.getY() - ent.prevY) * mc.getTickDelta();
        double z = ent.prevZ + (ent.getZ() - ent.prevZ) * mc.getTickDelta();
        Box axisAlignedBB2 = ent.getBoundingBox();
        Box axisAlignedBB = new Box(axisAlignedBB2.minX - ent.getX() + x - 0.05, axisAlignedBB2.minY - ent.getY() + y, axisAlignedBB2.minZ - ent.getZ() + z - 0.05, axisAlignedBB2.maxX - ent.getX() + x + 0.05, axisAlignedBB2.maxY - ent.getY() + y + 0.15, axisAlignedBB2.maxZ - ent.getZ() + z + 0.05);
        Vec3d[] vectors = new Vec3d[]{new Vec3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ)};

        Color col = getEntityColor(ent);

        Vector4d position = null;
        for (Vec3d vector : vectors) {
            vector = TextUtil.worldSpaceToScreenSpace(new Vec3d(vector.x, vector.y, vector.z));
            if (vector.z > 0 && vector.z < 1) {
                if (position == null) position = new Vector4d(vector.x, vector.y, vector.z, 0);
                position.x = Math.min(vector.x, position.x);
                position.y = Math.min(vector.y, position.y);
                position.z = Math.max(vector.x, position.z);
                position.w = Math.max(vector.y, position.w);
            }
        }


        if (position != null) {
            double posX = position.x;
            double posY = position.y;
            double endPosX = position.z;
            double endPosY = position.w;

            if (outline.getValue()) {
                Render2DUtil.setRectPoints(bufferBuilder,matrix, (float) (posX - 1F), (float) posY, (float) (posX + 0.5), (float) (endPosY + 0.5), Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK);
                Render2DUtil.setRectPoints(bufferBuilder,matrix, (float) (posX - 1F), (float) (posY - 0.5), (float) (endPosX + 0.5), (float) (posY + 0.5 + 0.5), Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK);
                Render2DUtil.setRectPoints(bufferBuilder,matrix, (float) (endPosX - 0.5 - 0.5), (float) posY, (float) (endPosX + 0.5), (float) (endPosY + 0.5), Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK);
                Render2DUtil.setRectPoints(bufferBuilder,matrix, (float) (posX - 1), (float) (endPosY - 0.5 - 0.5), (float) (endPosX + 0.5), (float) (endPosY + 0.5), Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK);
                Render2DUtil.setRectPoints(bufferBuilder,matrix, (float) (posX - 0.5f), (float) posY, (float) (posX + 0.5 - 0.5), (float) endPosY, col, col, col, col);
                Render2DUtil.setRectPoints(bufferBuilder,matrix, (float) posX, (float) (endPosY - 0.5f), (float) endPosX, (float) endPosY, col, col, col, col);
                Render2DUtil.setRectPoints(bufferBuilder,matrix, (float) (posX - 0.5), (float) posY, (float) endPosX, (float) (posY + 0.5), col, col, col, col);
                Render2DUtil.setRectPoints(bufferBuilder,matrix, (float) (endPosX - 0.5), (float) posY, (float) endPosX, (float) endPosY, col, col, col, col);
            }


            if (ent instanceof LivingEntity lent && lent.getHealth() != 0 && renderHealth.getValue()) {
                Render2DUtil.setRectPoints(bufferBuilder,matrix, (float) (posX - 4), (float) posY, (float) posX - 3, (float) endPosY, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK);
                Color color=getcolor(lent.getHealth());
                Render2DUtil.setRectPoints(bufferBuilder,matrix, (float) (posX - 4), (float) (endPosY + (posY - endPosY) * lent.getHealth() / lent.getMaxHealth()), (float) posX - 3, (float) endPosY,color,color,color,color);
            }
            if (ent instanceof PlayerEntity player && renderArmor.getValue()) {
                double height = (endPosY - posY) / 4;
                ArrayList<ItemStack> stacks = new ArrayList<>();
                stacks.add(player.getInventory().armor.get(3));
                stacks.add(player.getInventory().armor.get(2));
                stacks.add(player.getInventory().armor.get(1));
                stacks.add(player.getInventory().armor.get(0));

                int i = -1;
                for (ItemStack armor : stacks) {
                    ++i;
                    if (!armor.isEmpty()) {

                        float durability = armor.getMaxDamage() - armor.getDamage();
                        int percent = (int) ((durability / (float) armor.getMaxDamage()) * 100F);
                        double finalH = height * (percent / 100);
                        Render2DUtil.setRectPoints(bufferBuilder,matrix, (float) (endPosX + 1.5), (float) ((float) posY + height * i + 1.2 * (i + 1)), (float) ((float) endPosX + 3),  (int)(posY + height * i + 1.2 * (i + 1) +finalH), armorDuraColor, armorDuraColor, armorDuraColor, armorDuraColor);
                    }
                }

            }


        }

    }

    public void drawText(Entity ent, DrawContext context) {
        Color textcolor = new Color(colorR19.getValue(),colorG19.getValue(),colorB19.getValue(),colorA19.getValue());
        Color countColor = new Color(colorR10.getValue(),colorG10.getValue(),colorB10.getValue(),colorA10.getValue());
        double x = ent.prevX + (ent.getX() - ent.prevX) * mc.getTickDelta();
        double y = ent.prevY + (ent.getY() - ent.prevY) * mc.getTickDelta();
        double z = ent.prevZ + (ent.getZ() - ent.prevZ) * mc.getTickDelta();
        Box axisAlignedBB2 = ent.getBoundingBox();
        Box axisAlignedBB = new Box(axisAlignedBB2.minX - ent.getX() + x - 0.05, axisAlignedBB2.minY - ent.getY() + y, axisAlignedBB2.minZ - ent.getZ() + z - 0.05, axisAlignedBB2.maxX - ent.getX() + x + 0.05, axisAlignedBB2.maxY - ent.getY() + y + 0.15, axisAlignedBB2.maxZ - ent.getZ() + z + 0.05);
        Vec3d[] vectors = new Vec3d[]{new Vec3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ)};

        Color col = getEntityColor(ent);

        Vector4d position = null;
        for (Vec3d vector : vectors) {
            vector = TextUtil.worldSpaceToScreenSpace(new Vec3d(vector.x, vector.y, vector.z));
            if (vector.z > 0 && vector.z < 1) {
                if (position == null) position = new Vector4d(vector.x, vector.y, vector.z, 0);
                position.x = Math.min(vector.x, position.x);
                position.y = Math.min(vector.y, position.y);
                position.z = Math.max(vector.x, position.z);
                position.w = Math.max(vector.y, position.w);
            }
        }


        if (position != null) {
            double posX = position.x;
            double posY = position.y;
            double endPosX = position.z;
            double endPosY = position.w;
            if (ent instanceof ItemEntity entity && drawItem.getValue()) {
                float diff = (float) ((endPosX - posX) / 2f);
                float textWidth = (mc.textRenderer.getWidth(entity.getDisplayName().getString()) * 1);//(FontRenderers.Arial.getWidth(entity.getDisplayName().getString()) * 1);
                float tagX = (float) ((posX + diff - textWidth / 2f) * 1);
                int count = entity.getStack().getCount();
                context.drawText(mc.textRenderer, entity.getDisplayName().getString(), (int) tagX, (int) (posY - 10), textcolor.getRGB(), false);
                
                if (drawItemC.getValue()) {
                    context.drawText(mc.textRenderer, "x" + count, (int) (tagX + mc.textRenderer.getWidth(entity.getDisplayName().getString() + " ")), (int) posY - 10, countColor.getRGB(), false);

                }

            }
            if (ent instanceof PlayerEntity player && renderArmor.getValue()) {
                double height = (endPosY - posY) / 4;
                ArrayList<ItemStack> stacks = new ArrayList<>();
                stacks.add(player.getInventory().armor.get(3));
                stacks.add(player.getInventory().armor.get(2));
                stacks.add(player.getInventory().armor.get(1));
                stacks.add(player.getInventory().armor.get(0));

                int i = -1;
                for (ItemStack armor : stacks) {
                    ++i;
                    if (!armor.isEmpty()) {

                        float durability = armor.getMaxDamage() - armor.getDamage();
                        int percent = (int) ((durability / (float) armor.getMaxDamage()) * 100F);
                        double finalH = height * (percent / 100);
                        context.drawItem(armor, (int) (endPosX + 4), (int) (posY + height * i + 1.2 * (i + 1) +finalH/2) );

                    }
                }

            }
        }

    }



    public static float getRotations(Vec2f vec) {
        if (mc.player == null) return 0;
        double x = vec.x - mc.player.getPos().x;
        double z = vec.y - mc.player.getPos().z;
        return (float) -(Math.atan2(x, z) * (180 / Math.PI));
    }
    public Color getcolor(float health){
        Color hHealth = new Color(colorR11.getValue(),colorG11.getValue(),colorB11.getValue(),colorA11.getValue());
        Color mHealth = new Color(colorR112.getValue(),colorG112.getValue(),colorB112.getValue(),colorA112.getValue());
        Color lHealth = new Color(colorR113.getValue(),colorG113.getValue(),colorB113.getValue(),colorA113.getValue());
        if(health>=20){
            return hHealth;
        }
        else if(20>health && health>10){
            return mHealth;
        }
        else{
            return lHealth;
        }
    }

    public enum Page{
        Setting,Target,Color
    }
}