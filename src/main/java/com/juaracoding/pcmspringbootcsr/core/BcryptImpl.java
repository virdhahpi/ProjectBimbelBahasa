package com.juaracoding.pcmspringbootcsr.core;

import java.util.function.Function;

public class BcryptImpl {

    private static final BcryptCustom bcrypt = new BcryptCustom(11);

    public static String hash(String password) {
        return bcrypt.hash(password);
    }

    public static boolean verifyAndUpdateHash(String password, String hash, Function<String, Boolean> updateFunc) {
        return bcrypt.verifyAndUpdateHash(password, hash, updateFunc);
    }

    public static boolean verifyHash(String password , String hash)
    {
        return bcrypt.verifyHash(password,hash);
    }
    
    public static void main(String[] args) {
        String strUserName = "allysazahra12";
        String strPwd = "allysazahra12GwBanget#4567";
        String strAfterEncrypt1 = hash(strPwd);
        System.out.println("strAfterEncrypt1 => "+strAfterEncrypt1);
        System.out.println(verifyHash("210539","$2a$11$nZ2PYxp01.M5mMdsVj9Jp.Wm0WUHqV7jaYOT40rv4RBAIVMgV8xk6"));
    }
}