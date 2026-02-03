package com.company.eventbooking.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class CryptoUtil {

    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY="MySuperSecretKey";

    private static SecretKeySpec getKey(){
        return new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
    }

    public static String encrypt(String data){
        if(data == null){
            return null;
        }
        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE,getKey());
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
        }
        catch(Exception e){
            throw new RuntimeException("Encryption failed",e);
        }

    }

    public static String decrypt(String encyptedData){
        if(encyptedData==null) return null;
        try{
            Cipher cipher=Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE,getKey());
            byte[] decoded=Base64.getDecoder().decode(encyptedData);
            return new String(cipher.doFinal(decoded));
        }
        catch(Exception e){
            throw new RuntimeException("Decryption failed",e);
        }
    }

    
}
