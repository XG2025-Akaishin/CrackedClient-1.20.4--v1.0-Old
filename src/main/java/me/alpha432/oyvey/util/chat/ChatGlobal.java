package me.alpha432.oyvey.util.chat;

import net.minecraft.text.Text;
import me.alpha432.oyvey.features.modules.Module;
import static me.alpha432.oyvey.util.traits.Util.mc;

public class ChatGlobal {
	//Use Example sendChatMessageWidthId("mesaage", -1);
	public static void sendChatMessageWidthId(String message, int id) {
		if (Module.nullCheck()) return;
		((IChatHud) mc.inGameHud.getChatHud()).n_nextgen_master$add(Text.of(message), id);
	}
}
