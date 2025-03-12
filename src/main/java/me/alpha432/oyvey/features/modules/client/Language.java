package me.alpha432.oyvey.features.modules.client;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;

public class Language extends Module {

    public Setting<ModeLanguage> mode = this.register(new Setting("LanguageMode", ModeLanguage.Ingles));

    public Language() {
        super("Notificador", "Muestra una notificación", Category.CLIENT, false, false, false);
    }
    
    public static String M = "none";
    
    public static String SystemLang(ModeLanguage mode, String ingles,String español,String latino,String ruso,String japones,String chino) {
        switch (mode) {
            case Ingles: {
                M = " " + ingles;
                break;
            }
        }
        switch (mode) {
            case Español: {
                M = " " + español;
                break;
            }
        }
        switch (mode) {
            case Latino: {
                M = " " + latino;
                break;
            }
        }
        switch (mode) {
            case Ruso: {
                M = " " + ruso;
                break;
            }
        }
        switch (mode) {
            case Japones: {
                M = " " + japones;
                break;
            }
        }
        switch (mode) {
            case Chino: {
                M = " " + chino;
                break;
            }
        }

        return M;
    }

    public static enum ModeLanguage {
        Ingles,
        Español,
        Latino,
        Ruso,
        Japones,
        Chino
    }
}