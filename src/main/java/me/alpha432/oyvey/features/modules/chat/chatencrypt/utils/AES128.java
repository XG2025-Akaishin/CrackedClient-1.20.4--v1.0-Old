package me.alpha432.oyvey.features.modules.chat.chatencrypt.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;

public class AES128 {

    private SecretKeySpec key;
    private IvParameterSpec ivspec;

    public AES128(String key) {
        try {
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            ivspec = new IvParameterSpec( iv );

            SecretKeyFactory factory = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA256" );
            KeySpec spec = new PBEKeySpec( key.toCharArray(), "badg1cfzhvhxhfgfab".getBytes(), 65536, 128 );
            SecretKey tmp = factory.generateSecret( spec );
            this.key = new SecretKeySpec( tmp.getEncoded(), "AES" );
        } catch ( Exception e ) {
            System.out.println( "Error initializing encryption" );
        }
    }

    public String encrypt(String data){
        try {
            Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5Padding" );
            cipher.init( Cipher.ENCRYPT_MODE, key, ivspec );
            byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch ( Exception e ) {
            e.printStackTrace();
            System.out.println("Error encrypting text");
        }
        return null;
    }

    public String decrypt(String data) {
        try{
            Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5Padding" );
            cipher.init( Cipher.DECRYPT_MODE, key, ivspec );
            byte[] decodedData = Base64.getDecoder().decode(data);
            byte[] decryptedData = cipher.doFinal(decodedData);
            return new String(decryptedData);
        } catch ( Exception e ) {
            System.out.println( "Error decrypting text");
            throw new RuntimeException( e );
        }
    }
}