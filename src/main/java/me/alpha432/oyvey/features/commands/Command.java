package me.alpha432.oyvey.features.commands;

import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.util.chat.ChatGlobal;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;


public abstract class Command
        extends Feature {
    protected String name;
    protected String[] commands;

    public Command(String name) {
        super(name);
        this.name = name;
        this.commands = new String[]{""};
    }

    public Command(String name, String[] commands) {
        super(name);
        this.name = name;
        this.commands = commands;
    }

    public static void sendMessageId(String message, int id) {//message id replace
        ChatGlobal.sendChatMessageWidthId(message, id);
    }

    public static void sendMessage(String message) {//message normal
        Command.sendSilentMessage(CrackedClient.commandManager.getClientMessage() + " " + Formatting.GRAY + message);
    }

    public static void sendSilentMessage(String message) {
        if (Command.nullCheck()) {
            return;
        }
        // TODO add silent support ig
        mc.inGameHud.getChatHud().addMessage(Text.literal(message));
    }

    public static String getCommandPrefix() {
        return CrackedClient.commandManager.getPrefix();
    }

    public abstract void execute(String[] var1);

    @Override
    public String getName() {
        return this.name;
    }

    public String[] getCommands() {
        return this.commands;
    }
}