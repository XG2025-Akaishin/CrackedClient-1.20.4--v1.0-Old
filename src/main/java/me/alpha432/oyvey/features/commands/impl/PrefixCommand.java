package me.alpha432.oyvey.features.commands.impl;

import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.features.commands.Command;
import net.minecraft.util.Formatting;

public class PrefixCommand
        extends Command {
    public PrefixCommand() {
        super("prefix", new String[]{"<char>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            Command.sendMessage(Formatting.GREEN + "Current prefix is " + CrackedClient.commandManager.getPrefix());
            return;
        }
        CrackedClient.commandManager.setPrefix(commands[0]);
        Command.sendMessage("Prefix changed to " + Formatting.GRAY + commands[0]);
    }
}