package me.alpha432.oyvey.features.commands.impl;

import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.features.commands.Command;
import net.minecraft.util.Formatting;

public class HelpCommand
        extends Command {
    public HelpCommand() {
        super("help");
    }

    @Override
    public void execute(String[] commands) {
        HelpCommand.sendMessage("Commands: ");
        for (Command command : CrackedClient.commandManager.getCommands()) {
            StringBuilder builder = new StringBuilder(Formatting.GRAY.toString());
            builder.append(CrackedClient.commandManager.getPrefix());
            builder.append(command.getName());
            builder.append(" ");
            for (String cmd : command.getCommands()) {
                builder.append(cmd);
                builder.append(" ");
            }
            HelpCommand.sendMessage(builder.toString());
        }
    }
}