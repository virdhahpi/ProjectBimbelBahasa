package com.juaracoding.pcmspringbootcsr.dto.regis;

import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageUser;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class VerifyTokenDTO {


    @NotNull(message = ConstantMessageUser.ERROR_TOKEN_IS_NULL)
    @NotBlank(message = ConstantMessageUser.ERROR_TOKEN_IS_BLANK)
    @NotEmpty(message = ConstantMessageUser.ERROR_TOKEN_IS_EMPTY)
    private String token;

    /*
        Digunakan untuk validasi
        jadi format email diabaikan
        notifikasi hanya jika parameter email tidak ada
     */
    @NotNull(message = ConstantMessageUser.ERROR_EMAIL_IS_NULL)
    @NotBlank(message = ConstantMessageUser.ERROR_EMAIL_IS_BLANK)
    @NotEmpty(message = ConstantMessageUser.ERROR_EMAIL_IS_EMPTY)
    private String email;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
