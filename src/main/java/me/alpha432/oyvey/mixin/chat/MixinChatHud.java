package me.alpha432.oyvey.mixin.chat;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import me.alpha432.oyvey.util.chat.IChatHud;
import java.util.List;

@Mixin(ChatHud.class)
public abstract class MixinChatHud implements IChatHud {
    @Shadow
    @Final
    private List<ChatHudLine.Visible> visibleMessages;
    @Shadow
    @Final
    private List<ChatHudLine> messages;
    @Unique
    private int n_nextId = 0;
    @Shadow
    public abstract void addMessage(Text message);

    @Override
    public void n_nextgen_master$add(Text message, int id) {
        n_nextId = id;
        addMessage(message);
        n_nextId = 0;
    }
}
