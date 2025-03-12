package me.alpha432.oyvey.features.modules.misc.titlebrack;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.render.storagesp.utils.ColorUtil;
import me.alpha432.oyvey.features.modules.render.storagesp.utils.UtilRenderESP;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.awt.Color;

public class TileBreaker extends Module {
	private ArrayList<Block> blocks = new ArrayList<>();

    private final Setting<Integer> radius  = this.register(new Setting<Integer>("Radius", 5, 0, 15));
	public final Setting<Boolean> outline = this.register(new Setting<>("Outline", true));
    public final Setting<Boolean> fill = this.register(new Setting<>("Fill", true));
	private final Setting<Integer> r  = this.register(new Setting<Integer>("Red", 5, 0, 255));
	private final Setting<Integer> g  = this.register(new Setting<Integer>("Green", 5, 0, 255));
	private final Setting<Integer> b  = this.register(new Setting<Integer>("Blue", 5, 0, 255));
	private final Setting<Integer> a  = this.register(new Setting<Integer>("Alpha", 5, 0, 255));
	
	public TileBreaker() {
		super("TileBreaker", "TileBreaker ROMPER BLOQUES ALREDEDOR", Category.MISC, true, false, false);
		this.loadTileBreakerBlocks();
	}

	public void setRadius(int radius) {
		this.radius.setValue(radius);
	}

	@Override
	public void onUpdate() {
		int rad = this.radius.getValue();
		for (int x = -rad; x < rad; x++) {
			for (int y = rad; y > -rad; y--) {
				for (int z = -rad; z < rad; z++) {
					BlockPos blockpos = new BlockPos( mc.player.getBlockX() + x,
							 mc.player.getBlockY() + y,
							 mc.player.getBlockZ() + z);
					Block block = mc.world.getBlockState(blockpos).getBlock();
					if (this.isTileBreakerBlock(block)) {
						mc.player.networkHandler.sendPacket(
								new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, blockpos, Direction.NORTH));
						mc.player.networkHandler.sendPacket(
								new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, blockpos, Direction.NORTH));
					}
				}
			}
		}
    }

	@Override
	public void onPreRender3D(MatrixStack stack) {//test onPreRender3D
		int rad = this.radius.getValue();
		for (int x = -rad; x < rad; x++) {
			for (int y = rad; y > -rad; y--) {
				for (int z = -rad; z < rad; z++) {
					BlockPos blockpos = new BlockPos((int) mc.player.getBlockX() + x, mc.player.getBlockY() + y,
							 mc.player.getBlockZ() + z);
					Block block = mc.world.getBlockState(blockpos).getBlock();

					if (this.isTileBreakerBlock(block)) {
						Color color = new Color(r.getValue() ,g.getValue() ,b.getValue() ,a.getValue());
						if (fill.getValue()) {
							UtilRenderESP.drawFilledBox(stack, new Box(blockpos), color);
						}
						if (outline.getValue()) {
							UtilRenderESP.drawBoxOutline(new Box(blockpos), ColorUtil.injectAlpha(color, 255), 1f);
						}
					
					}
				}
			}
		}
	}
	
	private void loadTileBreakerBlocks() {
		this.blocks.add(Blocks.TORCH);
		this.blocks.add(Blocks.WALL_TORCH);
		this.blocks.add(Blocks.REDSTONE_TORCH);
		this.blocks.add(Blocks.REDSTONE_WALL_TORCH);
		this.blocks.add(Blocks.FERN);
		this.blocks.add(Blocks.LARGE_FERN);
		this.blocks.add(Blocks.FLOWER_POT);
		this.blocks.add(Blocks.POTATOES);
		this.blocks.add(Blocks.CARROTS);
		this.blocks.add(Blocks.WHEAT);
		this.blocks.add(Blocks.BEETROOTS);
		this.blocks.add(Blocks.SUGAR_CANE);
		this.blocks.add(Blocks.SHORT_GRASS);
		this.blocks.add(Blocks.TALL_GRASS);
		this.blocks.add(Blocks.SEAGRASS);
		this.blocks.add(Blocks.TALL_SEAGRASS);
		this.blocks.add(Blocks.DEAD_BUSH);
		this.blocks.add(Blocks.DANDELION);
		this.blocks.add(Blocks.ROSE_BUSH);
		this.blocks.add(Blocks.POPPY);
		this.blocks.add(Blocks.BLUE_ORCHID);
		this.blocks.add(Blocks.ALLIUM);
		this.blocks.add(Blocks.AZURE_BLUET);
		this.blocks.add(Blocks.RED_TULIP);
		this.blocks.add(Blocks.ORANGE_TULIP);
		this.blocks.add(Blocks.WHITE_TULIP);
		this.blocks.add(Blocks.PINK_TULIP);
		this.blocks.add(Blocks.OXEYE_DAISY);
		this.blocks.add(Blocks.CORNFLOWER);
		this.blocks.add(Blocks.WITHER_ROSE);
		this.blocks.add(Blocks.LILY_OF_THE_VALLEY);
		this.blocks.add(Blocks.BROWN_MUSHROOM);
		this.blocks.add(Blocks.RED_MUSHROOM);
		this.blocks.add(Blocks.SUNFLOWER);
		this.blocks.add(Blocks.LILAC);
		this.blocks.add(Blocks.PEONY);
	}
	
	public boolean isTileBreakerBlock(Block b) {
		return this.blocks.contains(b);
	}
}