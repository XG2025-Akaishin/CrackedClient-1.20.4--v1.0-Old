package me.alpha432.oyvey.features.settings;

import com.google.common.base.Converter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.alpha432.oyvey.util.traits.Util;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;

public class Bind implements Util {
    private int key;

    public Bind(int key) {
        this.key = key;
    }

    public static Bind none() {
        return new Bind(-1);
    }

    public int getKey() {
        return this.key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public boolean isEmpty() {
        return this.key < 0;
    }

    public String toString() {
        if (key == -1) return "None";

        String kn = this.key > 0 ? GLFW.glfwGetKeyName(this.key, GLFW.glfwGetKeyScancode(this.key)) : "None";
        if (kn == null) {
            try {
                for (Field declaredField : GLFW.class.getDeclaredFields()) {
                    if (declaredField.getName().startsWith("GLFW_KEY_")) {
                        int a = (int) declaredField.get(null);
                        if (a == this.key) {
                            String nb = declaredField.getName().substring("GLFW_KEY_".length());
                            kn = nb.substring(0, 1).toUpperCase() + nb.substring(1).toLowerCase();
                        }
                    }
                }
            } catch (Exception ignore) {
                kn = "unknown." + this.key;
            }
        }

        return this.key == -1 ? "None" : (kn + "").toUpperCase();
        /*
        String kn = this.key > 0 ? GLFW.glfwGetKeyName(this.key, GLFW.glfwGetKeyScancode(this.key)) : "None";
        if (kn == null) {
            try {
                for (Field declaredField : GLFW.class.getDeclaredFields()) {
                    if (declaredField.getName().startsWith("GLFW_KEY_")) {
                        int a = (int) declaredField.get(null);
                        if (a == this.key) {
                            String nb = declaredField.getName().substring("GLFW_KEY_".length());
                            kn = nb.substring(0, 1).toUpperCase() + nb.substring(1).toLowerCase();
                        }
                    }
                }
            } catch (Exception ignored) {
                kn = "None";
            }
        }

        return (kn + "").toUpperCase();
        /*
        return this.isEmpty() ? "None" : (this.key < 0
                ? "None"
                : this.capitalise(InputUtil.fromKeyCode(this.key, 0).getTranslationKey()));
                */
    }

    public boolean isDown() {
        return !this.isEmpty() && GLFW.glfwGetKey(mc.getWindow().getHandle(), this.getKey()) == 1;
    }

    private String capitalise(String str) {
        if (str.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(str.charAt(0)) + (str.length() != 1 ? str.substring(1).toLowerCase() : "");
    }

    public static class BindConverter
            extends Converter<Bind, JsonElement> {
        public JsonElement doForward(Bind bind) {
            return new JsonPrimitive(bind.toString());
        }

        public Bind doBackward(JsonElement jsonElement) {
            String s = jsonElement.getAsString();
            if (s.equalsIgnoreCase("None")) {
                return Bind.none();
            }
            int key = -1;
            try {
                key = InputUtil.fromTranslationKey(s.toUpperCase()).getCode();
            } catch (Exception exception) {
                // empty catch block
            }
            if (key == 0) {
                return Bind.none();
            }
            return new Bind(key);
        }
    }
}