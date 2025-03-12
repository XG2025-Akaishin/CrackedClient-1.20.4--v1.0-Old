package me.alpha432.oyvey.features.modules.movement.icespeed;

import me.alpha432.oyvey.mixin.icespeed.AccessorAbstractBlock;
import net.minecraft.block.Blocks;
import me.alpha432.oyvey.features.modules.Module;

public class IceSpeed extends Module {

    public IceSpeed() {
        super("IceSpeed", "IceSpeed 1.12.2 Super", Category.MOVEMENT, true, false, false);
        }

    @Override
    public void onEnable() {
        if (mc.world == null)
        {
            return;
        }
        ((AccessorAbstractBlock) Blocks.ICE).setSlipperiness(0.4f);
        ((AccessorAbstractBlock) Blocks.PACKED_ICE).setSlipperiness(0.4f);
        ((AccessorAbstractBlock) Blocks.BLUE_ICE).setSlipperiness(0.4f);
        ((AccessorAbstractBlock) Blocks.FROSTED_ICE).setSlipperiness(0.4f);
    }

    @Override
    public void onDisable() {
        if (mc.world == null)
        {
            return;
        }
        ((AccessorAbstractBlock) Blocks.ICE).setSlipperiness(0.98f);
        ((AccessorAbstractBlock) Blocks.PACKED_ICE).setSlipperiness(0.98f);
        ((AccessorAbstractBlock) Blocks.BLUE_ICE).setSlipperiness(0.98f);
        ((AccessorAbstractBlock) Blocks.FROSTED_ICE).setSlipperiness(0.98f);
    }
}
