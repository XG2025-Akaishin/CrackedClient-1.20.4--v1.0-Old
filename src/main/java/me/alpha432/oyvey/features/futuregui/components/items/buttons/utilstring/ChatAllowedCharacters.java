package me.alpha432.oyvey.features.futuregui.components.items.buttons.utilstring;

public class ChatAllowedCharacters {
    public static boolean isAllowedCharacter(char c) {
        // Add your character validation logic here
        return Character.isLetterOrDigit(c) || c == ' ' || c == '.' || c == ',' || c == '!' || c == '?' || c == ':';
    }
}