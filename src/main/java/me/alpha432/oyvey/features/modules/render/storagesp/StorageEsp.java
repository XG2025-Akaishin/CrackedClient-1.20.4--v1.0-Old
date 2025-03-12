package me.alpha432.oyvey.features.modules.render.storagesp;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.Box;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;


import me.alpha432.oyvey.features.modules.render.storagesp.utils.ColorUtil;
import me.alpha432.oyvey.features.modules.render.storagesp.utils.UtilRenderESP;
import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StorageEsp extends Module {
    public StorageEsp() {
        super("StorageESP", "StorageESP COOL", Module.Category.RENDER, true, false, false);
    }

    public final Setting<Boolean> outline = this.register(new Setting<>("Outline", true));
    public final Setting<Boolean> fill = this.register(new Setting<>("Fill", true));

    public final Setting<Boolean> chest = this.register(new Setting<>("Chest", true));
    public final Setting<Boolean> dispenser = this.register(new Setting<>("Dispenser", false));
    public final Setting<Boolean> shulker = this.register(new Setting<>("Shulker", true));
    public final Setting<Boolean> echest = this.register(new Setting<>("Ender Chest", true));
    public final Setting<Boolean> furnace = this.register(new Setting<>("Furnace", false));
    public final Setting<Boolean> hopper = this.register(new Setting<>("Hopper", false));
    public final Setting<Boolean> barrels = this.register(new Setting<>("Barrel", false));

    public final Setting<Boolean> cart = this.register(new Setting<>("Minecart", false));
    public final Setting<Boolean> frame = this.register(new Setting<>("ItemFrame", false));

    private final Setting<Integer> chestColorR = this.register(new Setting<>("ChestColorR", 255,0,255));
    private final Setting<Integer> chestColorG = this.register(new Setting<>("ChestColorG", 255,0,255));
    private final Setting<Integer> chestColorB = this.register(new Setting<>("ChestColorB", 255,0,255));
    private final Setting<Integer> chestColorA = this.register(new Setting<>("ChestColorA", 255,0,255));

    private final Setting<Integer> shulkColorR = this.register(new Setting<>("ShulkerColorR", 255,0,255));
    private final Setting<Integer> shulkColorG = this.register(new Setting<>("ShulkerColorG", 255,0,255));
    private final Setting<Integer> shulkColorB = this.register(new Setting<>("ShulkerColorB", 255,0,255));
    private final Setting<Integer> shulkColorA = this.register(new Setting<>("ShulkerColorA", 255,0,255));

    private final Setting<Integer> echestColorR = this.register(new Setting<>("EChestColorR", 255,0,255));
    private final Setting<Integer> echestColorG = this.register(new Setting<>("EChestColorG", 255,0,255));
    private final Setting<Integer> echestColorB = this.register(new Setting<>("EChestColorB", 255,0,255));
    private final Setting<Integer> echestColorA = this.register(new Setting<>("EChestColorA", 255,0,255));

    private final Setting<Integer> frameColorR = this.register(new Setting<>("FrameColorR", 255,0,255));
    private final Setting<Integer> frameColorG = this.register(new Setting<>("FrameColorG", 255,0,255));
    private final Setting<Integer> frameColorB = this.register(new Setting<>("FrameColorB", 255,0,255));
    private final Setting<Integer> frameColorA = this.register(new Setting<>("FrameColorA", 255,0,255));

    private final Setting<Integer> shulkerframeColorR = this.register(new Setting<>("ShulkFrameColorR", 255,0,255));
    private final Setting<Integer> shulkerframeColorG = this.register(new Setting<>("ShulkFrameColorG", 255,0,255));
    private final Setting<Integer> shulkerframeColorB = this.register(new Setting<>("ShulkFrameColorB", 255,0,255));
    private final Setting<Integer> shulkerframeColorA = this.register(new Setting<>("ShulkFrameColorA", 255,0,255));

    private final Setting<Integer> furnaceColorR = this.register(new Setting<>("FurnaceColorR", 255,0,255));
    private final Setting<Integer> furnaceColorG = this.register(new Setting<>("FurnaceColorG", 255,0,255));
    private final Setting<Integer> furnaceColorB = this.register(new Setting<>("FurnaceColorB", 255,0,255));
    private final Setting<Integer> furnaceColorA = this.register(new Setting<>("FurnaceColorA", 255,0,255));

    private final Setting<Integer> hopperColorR = this.register(new Setting<>("HopperColorR", 255,0,255));
    private final Setting<Integer> hopperColorG = this.register(new Setting<>("HopperColorG", 255,0,255));
    private final Setting<Integer> hopperColorB = this.register(new Setting<>("HopperColorB", 255,0,255));
    private final Setting<Integer> hopperColorA = this.register(new Setting<>("HopperColorA", 255,0,255));

    private final Setting<Integer> dispenserColorR = this.register(new Setting<>("DispenserColorR", 255,0,255));
    private final Setting<Integer> dispenserColorG = this.register(new Setting<>("DispenserColorG", 255,0,255));
    private final Setting<Integer> dispenserColorB = this.register(new Setting<>("DispenserColorB", 255,0,255));
    private final Setting<Integer> dispenserColorA = this.register(new Setting<>("DispenserColorA", 255,0,255));

    private final Setting<Integer> barrelColorR = this.register(new Setting<>("BarrelColorR", 255,0,255));
    private final Setting<Integer> barrelColorG = this.register(new Setting<>("BarrelColorG", 255,0,255));
    private final Setting<Integer> barrelColorB = this.register(new Setting<>("BarrelColorB", 255,0,255));
    private final Setting<Integer> barrelColorA = this.register(new Setting<>("BarrelColorA", 255,0,255));

    private final Setting<Integer> minecartColorR = this.register(new Setting<>("MinecartColorR", 255,0,255));
    private final Setting<Integer> minecartColorG = this.register(new Setting<>("MinecartColorG", 255,0,255));
    private final Setting<Integer> minecartColorB = this.register(new Setting<>("MinecartColorB", 255,0,255));
    private final Setting<Integer> minecartColorA = this.register(new Setting<>("MinecartColorA", 255,0,255));

    public void onPreRender3D(MatrixStack stack) {
        for (BlockEntity blockEntity : getBlockEntities()) {
            Color color = getColor(blockEntity);

            if (color == null) continue;

            Box chestbox = new Box(
                    blockEntity.getPos().getX() + 0.06,
                    blockEntity.getPos().getY(),
                    blockEntity.getPos().getZ() + 0.06,
                    (blockEntity.getPos().getX() + 0.94),
                    (blockEntity.getPos().getY() - 0.125 + 1),
                    (blockEntity.getPos().getZ() + 0.94)
            );

            if (fill.getValue()) {
                if (blockEntity instanceof ChestBlockEntity) {
                    UtilRenderESP.drawFilledBox(stack, chestbox, color);
                } else if (blockEntity instanceof EnderChestBlockEntity) {
                    UtilRenderESP.drawFilledBox(stack, chestbox, color);
                } else UtilRenderESP.drawFilledBox(stack, new Box(blockEntity.getPos()), color);
            }
            if (outline.getValue()) {
                if (blockEntity instanceof ChestBlockEntity) {
                    UtilRenderESP.drawBoxOutline(chestbox, ColorUtil.injectAlpha(color, 255), 1f);
                } else if (blockEntity instanceof EnderChestBlockEntity) {
                    UtilRenderESP.drawBoxOutline(chestbox, ColorUtil.injectAlpha(color, 255), 1f);
                } else
                    UtilRenderESP.drawBoxOutline(new Box(blockEntity.getPos()), ColorUtil.injectAlpha(color, 255), 1f);
            }
        }

        for (Entity ent : mc.world.getEntities()/*OyVey.asyncManager.getAsyncEntities()*/) {
            if (ent instanceof ItemFrameEntity iframe && frame.getValue()) {
                Color frameColor1 = new Color(frameColorR.getValue(),frameColorG.getValue(),frameColorB.getValue(),frameColorA.getValue());
                if (iframe.getHeldItemStack().getItem() instanceof BlockItem bitem && bitem.getBlock() instanceof ShulkerBoxBlock)
                    frameColor1 = new Color(shulkerframeColorR.getValue(),shulkerframeColorG.getValue(),shulkerframeColorB.getValue(),shulkerframeColorA.getValue());

                if (fill.getValue())
                    UtilRenderESP.drawFilledBox(stack, iframe.getBoundingBox(), frameColor1);

                if (outline.getValue())
                    UtilRenderESP.drawBoxOutline(iframe.getBoundingBox(), ColorUtil.injectAlpha(frameColor1, 255), 1f);
            }

            if (ent instanceof ChestMinecartEntity mcart && cart.getValue()) {
                if (fill.getValue())
                    UtilRenderESP.drawFilledBox(stack, mcart.getBoundingBox(), new Color(minecartColorR.getValue(),minecartColorG.getValue(),minecartColorB.getValue(),minecartColorA.getValue()));

                if (outline.getValue())
                    UtilRenderESP.drawBoxOutline(mcart.getBoundingBox(), ColorUtil.injectAlpha(new Color(minecartColorR.getValue(),minecartColorG.getValue(),minecartColorB.getValue(),minecartColorA.getValue()), 255), 1f);
            }
        }
    }

    @Nullable
    private Color getColor(BlockEntity bEnt) {
        Color color = null;

        if (bEnt instanceof TrappedChestBlockEntity && chest.getValue()) color = new Color(chestColorR.getValue() ,chestColorG.getValue() ,chestColorB.getValue() ,chestColorA.getValue());
        else if (bEnt instanceof ChestBlockEntity && chest.getValue())
            color = new Color(chestColorR.getValue() ,chestColorG.getValue() ,chestColorB.getValue() ,chestColorA.getValue());
        else if (bEnt instanceof EnderChestBlockEntity && echest.getValue())
            color = new Color(echestColorR.getValue(),echestColorG.getValue(),echestColorB.getValue(),echestColorA.getValue());
        else if (bEnt instanceof BarrelBlockEntity && barrels.getValue())
            color = new Color(barrelColorR.getValue(),barrelColorG.getValue(),barrelColorB.getValue(),barrelColorA.getValue());
        else if (bEnt instanceof ShulkerBoxBlockEntity && shulker.getValue())
            color = new Color(shulkColorR.getValue(),shulkColorG.getValue(),shulkColorB.getValue(),shulkColorA.getValue());
        else if (bEnt instanceof AbstractFurnaceBlockEntity && furnace.getValue())
            color = new Color(furnaceColorR.getValue(),furnaceColorG.getValue(),furnaceColorB.getValue(),furnaceColorA.getValue());
        else if (bEnt instanceof DispenserBlockEntity && dispenser.getValue())
            color = new Color(dispenserColorR.getValue(),dispenserColorG.getValue(),dispenserColorB.getValue(),dispenserColorA.getValue());
        else if (bEnt instanceof HopperBlockEntity && hopper.getValue())
            color = new Color(hopperColorR.getValue(),hopperColorG.getValue(),hopperColorB.getValue(),hopperColorA.getValue());

        return color;
    }

    public static List<BlockEntity> getBlockEntities() {
        List<BlockEntity> list = new ArrayList<>();
        for (WorldChunk chunk : getLoadedChunks())
            list.addAll(chunk.getBlockEntities().values());

        return list;
    }

    public static List<WorldChunk> getLoadedChunks() {
        List<WorldChunk> chunks = new ArrayList<>();
        int viewDist = mc.options.getViewDistance().getValue();
        for (int x = -viewDist; x <= viewDist; x++) {
            for (int z = -viewDist; z <= viewDist; z++) {
                WorldChunk chunk = mc.world.getChunkManager().getWorldChunk((int) mc.player.getX() / 16 + x, (int) mc.player.getZ() / 16 + z);

                if (chunk != null) chunks.add(chunk);
            }
        }
        return chunks;
    }
}
