package com.juaracoding.pcmspringbootcsr.constant;

public class ConstantMessageUser {
    /*START EMAIL*/
    public final static String ERROR_EMAIL_IS_NULL = "EMAIL TIDAK BOLEH NULL";
    public final static String ERROR_EMAIL_IS_BLANK = "EMAIL TIDAK BOLEH DIISI SPASI SAJA";
    public final static String ERROR_EMAIL_IS_EMPTY = "EMAIL TIDAK BOLEH KOSONG";
    public final static String ERROR_EMAIL_ISEXIST = "REGISTRASI GAGAL! EMAIL SUDAH TERDAFTAR";
    /*END OF EMAIL*/
    /*START REGIS VALIDATION*/
    public final static String ERROR_USER_ISACTIVE = "REGISTRASI GAGAL! EMAIL SUDAH TERDAFTAR";
    public final static String ERROR_NOHP_ISEXIST = "REGISTRASI GAGAL! NO HP SUDAH TERDAFTAR";
    public final static String ERROR_USERNAME_ISEXIST = "REGISTRASI GAGAL! USERNAME SUDAH TERDAFTAR";
    public final static String SUCCESS_CHECK_REGIS = "PENGECEKAN REGISTRASI BERHASIL";
    public final static String ERROR_USER_NOT_EXISTS = "USER TIDAK DITEMUKAN !!";

    /*END OF REGIS VALIDATION*/

    /*START TOKEN*/
    public final static String ERROR_TOKEN_IS_NULL = "TOKEN TIDAK BOLEH NULL";
    public final static String ERROR_TOKEN_IS_BLANK = "TOKEN TIDAK BOLEH DIISI SPASI SAJA";
    public final static String ERROR_TOKEN_IS_EMPTY = "TOKEN TIDAK BOLEH KOSONG";
    public final static String ERROR_INVALID_TOKEN = "TOKEN TIDAK TIDAK SESUAI";
    public final static String SUCCESS_SENDING_NEW_TOKEN = "TOKEN BARU BERHASIL DIKIRIM KE EMAIL";
    public final static String SUCCESS_TOKEN_MATCH = "TOKEN COCOK";

    /*END OF TOKEN*/
    public final static String ERROR_FLOW_INVALID = "PROSES TIDAK SESUAI";
    public final static String ERROR_LOGIN_FAILED = "USERNAME ATAU PASSWORD SALAH !!";
    public final static String SUCCESS_LOGIN = "LOGIN BERHASIL !!";
    public final static String SUCCESS_CHANGE_PWD = "PASSWORD BERHASIL DIUBAH !!";
    public final static String ERROR_PASSWORD_CONFIRM_FAILED = "PERMINTAAN TIDAK VALID !!";

}