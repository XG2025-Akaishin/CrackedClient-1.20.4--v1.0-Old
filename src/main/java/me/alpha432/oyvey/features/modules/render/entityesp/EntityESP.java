package me.alpha432.oyvey.features.modules.render.entityesp;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import java.awt.Color;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.render.entityesp.utilsd.Utild;
import me.alpha432.oyvey.features.modules.render.storagesp.utils.ColorUtil;
import me.alpha432.oyvey.features.modules.render.storagesp.utils.UtilRenderESP;

public class EntityESP extends Module {
	private final Setting<Boolean> self  = this.register(new Setting<Boolean>("Self", true));
	public final Setting<Boolean> outline = this.register(new Setting<>("Outline", true));
    public final Setting<Boolean> fill = this.register(new Setting<>("Fill", true));

	private final Setting<Integer> r  = this.register(new Setting<Integer>("OtherRed", 5, 0, 255));
	private final Setting<Integer> g  = this.register(new Setting<Integer>("OtherGreen", 5, 0, 255));
	private final Setting<Integer> b  = this.register(new Setting<Integer>("OtherBlue", 5, 0, 255));
	private final Setting<Integer> a  = this.register(new Setting<Integer>("OtherAlpha", 5, 0, 255)); 

	private final Setting<Integer> pr  = this.register(new Setting<Integer>("PlayerRed", 5, 0, 255));
	private final Setting<Integer> pg  = this.register(new Setting<Integer>("PlayerGreen", 5, 0, 255));
	private final Setting<Integer> pb  = this.register(new Setting<Integer>("PlayerBlue", 5, 0, 255));
	private final Setting<Integer> pa  = this.register(new Setting<Integer>("PlayerAlpha", 5, 0, 255));

	private final Setting<Integer> rs  = this.register(new Setting<Integer>("SelfRed", 5, 0, 255));
	private final Setting<Integer> gs  = this.register(new Setting<Integer>("SelfGreen", 5, 0, 255));
	private final Setting<Integer> bs  = this.register(new Setting<Integer>("SelfBlue", 5, 0, 255));
	private final Setting<Integer> as  = this.register(new Setting<Integer>("SelfAlpha", 5, 0, 255));

	private final Setting<Integer> rd  = this.register(new Setting<Integer>("AnimalRed", 5, 0, 255));
	private final Setting<Integer> gd  = this.register(new Setting<Integer>("AnimalGreen", 5, 0, 255));
	private final Setting<Integer> bd  = this.register(new Setting<Integer>("AnimalBlue", 5, 0, 255));
	private final Setting<Integer> ad  = this.register(new Setting<Integer>("AnimalAlpha", 5, 0, 255));

	private final Setting<Integer> rr  = this.register(new Setting<Integer>("MonsterRed", 5, 0, 255));
	private final Setting<Integer> gg  = this.register(new Setting<Integer>("MonsterGreen", 5, 0, 255));
	private final Setting<Integer> bb  = this.register(new Setting<Integer>("MonsterBlue", 5, 0, 255));
	private final Setting<Integer> aa  = this.register(new Setting<Integer>("MonsterAlpha", 5, 0, 255));

	public EntityESP() {
		super("EntityESP", "EntityESP R", Category.RENDER, true, false, false);
	}

	@Override
	public void onPreRender3D(MatrixStack stack) {	
		Color colors = new Color(rs.getValue() ,gs.getValue() ,bs.getValue() ,as.getValue());
		Color colore = new Color(r.getValue() ,g.getValue() ,b.getValue() ,a.getValue());
		Color colord = new Color(rd.getValue() ,gd.getValue() ,bd.getValue() ,ad.getValue());
		Color colora = new Color(rr.getValue() ,gg.getValue() ,bb.getValue() ,aa.getValue());
		Color colorp = new Color(pr.getValue() ,pg.getValue() ,pb.getValue() ,pa.getValue());
		for (Entity entity : mc.world.getEntities()) {
			//if (entity instanceof LivingEntity && !(entity instanceof PlayerEntity)) {

				if (entity instanceof PlayerEntity) {
					//getRenderUtils().draw3DBox(event.getMatrix(), entity.getBoundingBox(), new Color(0, 255, 0), 0.2f);ç+
					if (fill.getValue()) {
						    UtilRenderESP.drawFilledBox(stack, entity.getBoundingBox(), colorp);
						}
	
					if (outline.getValue()) {
						UtilRenderESP.drawBoxOutline(entity.getBoundingBox(), ColorUtil.injectAlpha(colorp, 255), 1f);
					}
				} else if (entity instanceof AnimalEntity) {
					//getRenderUtils().draw3DBox(event.getMatrix(), entity.getBoundingBox(), new Color(0, 255, 0), 0.2f);ç+
					if (fill.getValue()) {
						    UtilRenderESP.drawFilledBox(stack, entity.getBoundingBox(), colord);
						}
	
					if (outline.getValue()) {
						UtilRenderESP.drawBoxOutline(entity.getBoundingBox(), ColorUtil.injectAlpha(colord, 255), 1f);
					}
				} else if (entity instanceof Monster) {
					//getRenderUtils().draw3DBox(event.getMatrix(), entity.getBoundingBox(), new Color(255, 0, 0), 0.2f);
				    if (fill.getValue()) {
				    	UtilRenderESP.drawFilledBox(stack, entity.getBoundingBox(), colora);
				    }

				    if (outline.getValue()) {
				    	UtilRenderESP.drawBoxOutline(entity.getBoundingBox(), ColorUtil.injectAlpha(colora, 255), 1f);
				    }
				} else {
					//getRenderUtils().draw3DBox(event.getMatrix(), entity.getBoundingBox(), new Color(0, 0, 255), 0.2f);
					if (fill.getValue()) {
						UtilRenderESP.drawFilledBox(stack, entity.getBoundingBox(), colore);
					}
	
					if (outline.getValue()) {
						UtilRenderESP.drawBoxOutline(entity.getBoundingBox(), ColorUtil.injectAlpha(colore, 255), 1f);
					}
				}
			//}
		}
		if (self.getValue()) {
			//getRenderUtils().draw3DBox(event.getMatrix(), mc.player.getBoundingBox().offset(Utild.getMotionVec(mc.player, 1, true)), new Color(0, 0, 255), 0.2f);
			if (fill.getValue()) {
				UtilRenderESP.drawFilledBox(stack, mc.player.getBoundingBox().offset(Utild.getMotionVec(mc.player, 1, true)), colors);
			}

			if (outline.getValue()) {
				UtilRenderESP.drawBoxOutline(mc.player.getBoundingBox().offset(Utild.getMotionVec(mc.player, 1, true)), ColorUtil.injectAlpha(colors, 255), 1f);
			}
		}
	}

}
