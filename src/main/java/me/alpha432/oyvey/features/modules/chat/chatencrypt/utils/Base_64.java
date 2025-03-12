package me.alpha432.oyvey.features.modules.chat.chatencrypt.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;


public class Base_64 {
	public Base_64() {

	}

	  private static final char[] PASSWORD = "!@_-_CRACKED_KEY_-_@!".toCharArray();
	    private static final byte[] SALT = {
	        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
	        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
	    };
	
		public static String decrypt(String string) throws GeneralSecurityException, IOException {
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
			SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
			Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
			pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
			byte[] decodedData = Base64.getDecoder().decode(string);
			return new String(pbeCipher.doFinal(decodedData), "UTF-8");
		}


        public static String encrypt(String property) throws GeneralSecurityException, UnsupportedEncodingException {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
            byte[] encryptedData = pbeCipher.doFinal(property.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encryptedData);
        }

	    public static void main(String[] args) throws Exception {
			String H = encrypt("y que quieres que aga ");
			System.out.println("aaaaaaaaaa  " + H);
			String A = decrypt("dc0J8oCq3Tw=");
			System.out.println("eeeeeeeeee  " + A);

		}
	}