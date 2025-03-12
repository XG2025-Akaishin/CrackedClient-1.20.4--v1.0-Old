package me.alpha432.oyvey.features.commands.impl;

import com.google.common.eventbus.Subscribe;
import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.event.impl.KeyEvent;
import me.alpha432.oyvey.features.commands.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Bind;
import me.alpha432.oyvey.util.KeyboardUtil;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.security.Key;

public class BindCommand
        extends Command {

            public BindCommand() {
                super("bind", new String[]{"<module>", "<bind>"});
                EVENT_BUS.register(this);
            }
        
            @Override
            public void execute(String[] commands) {
                if (commands.length == 1) {
                    BindCommand.sendMessage("Please specify a module.");
                    return;
                }
                String rkey = commands[1];
                String moduleName = commands[0];
                Module module = CrackedClient.moduleManager.getModuleByName(moduleName);
                if (module == null) {
                    BindCommand.sendMessage("Unknown module '" + module + "'!");
                    return;
                }
                if (rkey == null) {
                    BindCommand.sendMessage(module.getName() + " is bound to " + Formatting.GRAY + module.getBind().toString());
                    return;
                }
                int key = InputUtil.fromTranslationKey("key.keyboard." + rkey.toLowerCase()).getCode();
                if (rkey.equalsIgnoreCase("none") || rkey.equalsIgnoreCase("null")) {
                    key = -1;
                }
                if (key == 0) {
                    BindCommand.sendMessage("Unknown key '" + rkey + "'!");
                    return;
                }
                module.bind.setValue(new Bind(key));
                BindCommand.sendMessage("Bind for " + Formatting.GREEN + module.getName() + Formatting.WHITE + " set to " + Formatting.GRAY + rkey.toUpperCase());
            }
            
    /*private boolean listening;
    private Module module;

    public BindCommand() {
        super("bind", new String[]{"<module>"});
        EVENT_BUS.register(this);
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            sendMessage("Please specify a module.");
            return;
        }
        String moduleName = commands[0];
        Module module = OyVey.moduleManager.getModuleByName(moduleName);
        if (module == null) {
            sendMessage("Unknown module '" + module + "'!");
            return;
        }

        sendMessage(Formatting.GRAY + "Press a key.");
        listening = true;
        this.module = module;
    }

    @Subscribe private void onKey(KeyEvent event) {
        if (nullCheck() || !listening) return;
        listening = false;
        if (event.getKey() == GLFW.GLFW_KEY_ESCAPE) {
            sendMessage(Formatting.GRAY + "Operation cancelled.");
            return;
        }

        sendMessage("Bind for " + Formatting.GREEN + module.getName() + Formatting.WHITE + " set to " + Formatting.GRAY + KeyboardUtil.getKeyName(event.getKey()));
        module.bind.setValue(new Bind(event.getKey()));
    }*/

}