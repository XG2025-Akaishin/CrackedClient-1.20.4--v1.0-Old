package me.alpha432.oyvey.features.modules.render.tooltips;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class Tooltips extends Module {

    private static Tooltips INSTANCE = new Tooltips();
    public Setting<Boolean> middleClickOpen = this.register(new Setting<>("MiddleClickOpen", true));
    public Setting<Boolean> storage =  this.register(new Setting<>("Storage", true));
    public Setting<Boolean> maps =  this.register(new Setting<>("Maps", true));
    public final Setting<Boolean> shulkerRegear = this.register(new Setting<>("ShulkerRegear", true));
    
    public Tooltips() {
        super("ToolTips", "ToolTips", Module.Category.RENDER, true, false, false);
        this.setInstance();
    }

    public static Tooltips getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Tooltips();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static boolean hasItems(ItemStack itemStack) {
        NbtCompound compoundTag = itemStack.getSubNbt("BlockEntityTag");
        return compoundTag != null && compoundTag.contains("Items", 9);
    }
}
