package me.alpha432.oyvey.features.modules.movement;

import java.util.ArrayList;
import java.util.List;
import me.alpha432.oyvey.features.modules.render.storagesp.utils.ColorUtil;
import me.alpha432.oyvey.features.modules.render.storagesp.utils.UtilRenderESP;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.world.chunk.WorldChunk;
import java.awt.Color;

public class StorageSrch extends me.alpha432.oyvey.features.modules.Module {

    public StorageSrch() {
        super("StorageSrch", "StorageSrch", Category.RENDER, true, false, false);
    }
    
    public final Setting<Boolean> outline = this.register(new Setting<>("Outline", true));
    public final Setting<Boolean> fill = this.register(new Setting<>("Fill", true));
    private final Setting<Integer> r = this.register(new Setting<>("Red", 160, 0, 255));
    private final Setting<Integer> g = this.register(new Setting<>("Green", 160, 0, 255));
    private final Setting<Integer> b = this.register(new Setting<>("Blue", 160, 0, 255));
    private final Setting<Integer> a = this.register(new Setting<>("Alpha", 160, 0, 255));
    private final Setting<Boolean> legit = this.register(new Setting<>("Legit", false));


    
	@Override
	public void onPreRender3D(MatrixStack stack) {
        Color color = new Color(r.getValue() ,g.getValue() ,b.getValue() ,a.getValue());
		for(BlockEntity blockEntity : getBlockEntities()) {
			if(blockEntity instanceof ChestBlockEntity || blockEntity instanceof TrappedChestBlockEntity) {
				Box box = new Box(blockEntity.getPos());
				//RenderOthersColl.draw3DBox(event.getMatrix(), box, new Color(r.getValue(),g.getValue(),b.getValue(),a.getValue()));
                	if (fill.getValue()) {
						UtilRenderESP.drawFilledBox(stack, box, color);
					}
					if (outline.getValue()) {
						UtilRenderESP.drawBoxOutline(box, ColorUtil.injectAlpha(color, 255), 1f);
					}
			}
		}
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
                WorldChunk chunk = mc.world.getChunkManager().getWorldChunk((int) mc.player.getX() /*/ 16 */ + x, (int) mc.player.getZ()/*/ 16 */ + z);

                if (chunk != null) chunks.add(chunk);
            }
        }
        return chunks;
    }
}
