package com.juaracoding.pcmspringbootcsr.core;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.AESLightEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;

public class Crypto {

    private static String defaultKey = "aafd12f438cae52538b479e3199ddec2f06cb58faafd12f6";
    public static String performEncrypt(String keyText, String plainText) {
        try{
            byte[] key = Hex.decode(keyText.getBytes());
            byte[] ptBytes = plainText.getBytes();
            BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESLightEngine()));
            cipher.init(true, new KeyParameter(key));
            byte[] rv = new byte[cipher.getOutputSize(ptBytes.length)];
            int oLen = cipher.processBytes(ptBytes, 0, ptBytes.length, rv, 0);
            cipher.doFinal(rv, oLen);
            return new String(Hex.encode(rv));
        } catch(Exception e) {
            return "Error";
        }
    }

    public static String performEncrypt(String cryptoText) {
        return performEncrypt(defaultKey, cryptoText);
    }

    public static String performDecrypt(String keyText, String cryptoText) {
        try {
            byte[] key = Hex.decode(keyText.getBytes());
            byte[] cipherText = Hex.decode(cryptoText.getBytes());
            BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESLightEngine()));
            cipher.init(false, new KeyParameter(key));
            byte[] rv = new byte[cipher.getOutputSize(cipherText.length)];
            int oLen = cipher.processBytes(cipherText, 0, cipherText.length, rv, 0);
            cipher.doFinal(rv, oLen);
            return new String(rv).trim();
        } catch(Exception e) {
            return "Error";
        }
    }

    public static String performDecrypt(String cryptoText) {
        return performDecrypt(defaultKey, cryptoText);
    }



    public static void main(String[] args) {

        String strToEncrypt = "onge";//put text to encrypt in here
        String encryptionResult = new Crypto().performEncrypt(strToEncrypt);
        System.out.println("Encryption Result : "+encryptionResult);
        // KEY -> aafd12f438cae52538b479e3199ddec2f06cb58faafd12f6
//        System.out.println("KEY PENTING BANGET !!! "+defaultKey);;
        //ENCRYPT -> 528b01943544a1dcef7a692a0628e46b ->

        //ENCRYPT -> bdcc9507be280e3e5489a5dce01b42ea
        //KEY -> aafd12f438cae52538b479e2089ddec2f06cb58faafd12f6

        String strToDecrypt = "8403b727f5c433e8543c07d116ad9ba52183fd1b9a0e99b99a07f96a666687e8307b03d6179e6d9bbbf5631b542d45becd6de219e7b3dc4b8bd34a21110a34d7b6b52dc1f976eaa50b5f420f79dcbbc53bac18c6f776c02945ae810b1f17bbb5c32e06f46854ce20ce74b0f4277b291c9c406ac678e07c86484fb3d16614e56e067cfbaf980be75dad8a0496256438f22e8635b0bd03ed01639ff07a60047d225668ffd00cb205ea85bc89fd2dfa4a7f9fed745f708c16ca21ebd4f7364163b198afc25143e55be542f5a500d2e9065adb3d9f5bc15c1bec983f9798c8b18fbeaebf0184d21177f59ebc25d3f322a9c13880525943324d8c5711c646c9d89050b7dc718893de1bf45d8590252a4e3f9b6539f5b3588b6f48eabaab36dae45907";//put text to decrypt in here
        String decriptionResult = new Crypto().performDecrypt(strToDecrypt);
        System.out.println("Decryption Result : "+decriptionResult);
//        System.out.println("Untuk VIVO X5 DEFAULT AJA BELUM DI SET ".length());
        //585107f50fa1e0649bd32da95d5cf41c
        //585107f50fa1e0649bd32da95d5cf41c
        //585107f50fa1e0649bd32da95d5cf41c
    }
}