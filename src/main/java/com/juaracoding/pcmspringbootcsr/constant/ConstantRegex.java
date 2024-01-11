package com.juaracoding.pcmspringbootcsr.constant;

/*
    class yang berisi Pesan Error dan Regex untuk seluruh validasi di field pada class DTO
 */
public class ConstantRegex {

    public final static String EMAIL_RFC532 = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    public final static String EMAIL_RFC532_INVALID = "Contoh Format : johndoe1@provider.com";
    public final static String ERROR_TOKEN_FORMAT = "^[0-9]{6}$";
    public final static String ERROR_TOKEN_INVALID = "Format Token 6 Digit Angka";
    public final static String REGEX_PASSWORD  = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[*.!@$%^&(){}:;<>,.?/~_+-=|]).{10,20}$";
    public final static String ERROR_PASSWORD_INVALID = "Kombinasi Huruf,Angka dan spesial character !! contoh : PaulPaul@1234";
    public final static String REGEX_NO_HP  = "^((((62)|0))8)([0-9]{8,12})$";
    public final static String ERROR_NO_HP_INVALID = "Format No HP  : 628 atau 08 12345678";
    public final static String REGEX_NAMA_LENGKAP  = "^[a-zA-Z\\s]{10,50}$";
    public final static String ERROR_NAMA_LENGKAP_INVALID = "Hanya diperbolehkan alfabet , minimal 10 maksimal 50 karakter";

}
