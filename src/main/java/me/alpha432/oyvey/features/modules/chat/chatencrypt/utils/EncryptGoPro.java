package me.alpha432.oyvey.features.modules.chat.chatencrypt.utils;

import java.util.Arrays;

public class EncryptGoPro {
    public static int[] encrypt(String plaintext, int key) {
        int[] encoded = new int[plaintext.length()];
        for (int i = 0; i < plaintext.length(); i++) {
            char c = plaintext.charAt(i);
            int keyValue = getNumericKeyValue(key, i % String.valueOf(key).length());
            encoded[i] = c + keyValue;
        }
        return encoded;
    }
    
    public static String decrypt(int[] encoded, int key) {
        StringBuilder decrypted = new StringBuilder();
        for (int i = 0; i < encoded.length; i++) {
            int keyValue = getNumericKeyValue(key, i % String.valueOf(key).length());
            char c = (char) (encoded[i] - keyValue);
            decrypted.append(c);
        }
        return decrypted.toString();
    }
    
    public static int getNumericKeyValue(int key, int index) {
        String keyString = String.valueOf(key);
        char c = keyString.charAt(index);
        return Character.getNumericValue(c);
    }
    
    public static String ConvertString(int[] input) {
        return Arrays.toString(input).replace("[","{").replace("]", "}");
    }

    public static int[] convertStringToIntArray(String input) {
        input = input.replace("{", "").replace("}", ""); // Elimina los caracteres {}
        String[] strArray = input.split(", "); // Separa la cadena en un arreglo de strings
        int[] intArray = new int[strArray.length];
    
        for (int i = 0; i < strArray.length; i++) {
            intArray[i] = Integer.parseInt(strArray[i]); // Convierte cada string en un entero
        }
    
        return intArray;
    }
    
}