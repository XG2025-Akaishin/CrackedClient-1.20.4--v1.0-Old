package me.alpha432.oyvey.features.modules.render.itemesphobos;

import java.awt.Color;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import me.alpha432.oyvey.event.impl.Render3DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.render.storagesp.utils.ColorUtil;
import me.alpha432.oyvey.features.modules.render.storagesp.utils.UtilRenderESP;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.util.futurepro.RenderUtils;

public class PhobosItemEsp extends Module {

	public final Setting<Boolean> outline = this.register(new Setting<>("Outline", true));
    public final Setting<Boolean> fill = this.register(new Setting<>("Fill", true));
	private final Setting<Integer> r  = this.register(new Setting<Integer>("Red", 5, 0, 255));
	private final Setting<Integer> g  = this.register(new Setting<Integer>("Green", 5, 0, 255));
	private final Setting<Integer> b  = this.register(new Setting<Integer>("Blue", 5, 0, 255));
	private final Setting<Integer> a  = this.register(new Setting<Integer>("Alpha", 5, 0, 255));

	public PhobosItemEsp() {
		super("ItemESPPhobos", "ItemESP", Category.RENDER , true, false,true);
	}

    @Override
	public void onPreRender3D(MatrixStack stack) {
		Color color = new Color(r.getValue() ,g.getValue() ,b.getValue() ,a.getValue());
		for (Entity entity : mc.world.getEntities()) {
			if(entity instanceof ItemEntity) {
				//getRenderUtils().draw3DBox(event.getMatrix(), entity.getBoundingBox(), new Color(255, 0, 0), 0.2f);
				if (fill.getValue()) {
                UtilRenderESP.drawFilledBox(stack, entity.getBoundingBox(), color);
				}

                if (outline.getValue()) {
                    UtilRenderESP.drawBoxOutline(entity.getBoundingBox(), ColorUtil.injectAlpha(color, 255), 1f);
				}
			}
		}
	}

}