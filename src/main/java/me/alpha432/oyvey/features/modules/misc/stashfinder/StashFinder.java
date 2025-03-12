package me.alpha432.oyvey.features.modules.misc.stashfinder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.render.storagesp.StorageEsp;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.chunk.WorldChunk;

public class StashFinder extends Module {

    private final Setting<Boolean> sound = this.register(new Setting<>("Sound", true));
    //private final Setting<Boolean> saveToFile = new Setting<>("SaveToFile", true);
    private final Setting<Integer> minChests = this.register(new Setting<>("MinChests", 5, 0, 100));
    private final Setting<Integer> minShulkers = this.register(new Setting<>("MinShulkers", 0, 0, 100));

    private List<WorldChunk> savedChunks = new ArrayList<>();

    public StashFinder() {
        super("StashFinder", "StashFinder Logger", Category.MISC, true, false, false);
    }


    @Override
    public void onUpdate() {
        for (WorldChunk chunk : StorageEsp.getLoadedChunks()) {
            if (savedChunks.contains(chunk))
                continue;

            List<BlockEntity> storages = chunk.getBlockEntities().values().stream().toList();

            int chests = 0;
            int shulkers = 0;

            for (BlockEntity storage : storages) {
                if (storage instanceof ChestBlockEntity)
                    chests++;
                if (storage instanceof ShulkerBoxBlockEntity)
                    shulkers++;
            }

            if (chests >= minChests.getValue() && shulkers >= minShulkers.getValue()) {
                savedChunks.add(chunk);

                String str = "Stash pos: X:" + chunk.getPos().getCenterX() + " Z:" + chunk.getPos().getCenterZ() + " Chests: " + chests + " Shulkers: " + shulkers;
                sendMessage(str);

                if (sound.getValue())
                    mc.world.playSound(mc.player, mc.player.getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 1f, 1f);

                String serverIP = "unknown_server";
                if (mc.getNetworkHandler().getServerInfo() != null && mc.getNetworkHandler().getServerInfo().address != null)
                    serverIP = mc.getNetworkHandler().getServerInfo().address.replace(':', '_');
                //ip name server :)
            }
        }
    }
}