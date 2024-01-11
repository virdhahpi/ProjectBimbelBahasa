package com.juaracoding.pcmspringbootcsr.dto.regis;

import com.juaracoding.pcmspringbootcsr.constant.ConstantUser;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class LoginDTO {


    @NotNull(message = ConstantUser.ERROR_USERNAME_IS_NULL)
    @NotBlank(message = ConstantUser.ERROR_USERNAME_IS_BLANK)
    @NotEmpty(message = ConstantUser.ERROR_USERNAME_IS_EMPTY)
    private String username;

    /*
        UNTUK PROSES LOGIN
        VALIDASI PASSWORD NYA PASSWORD KOSONG SAJA
        UNTUK PASSWORD FORMAT NYA TIDAK PERLU, KARENA ITU HANYA ADA DI FORM REGISTRASI
     */
    @NotNull(message = ConstantUser.ERROR_PASSWORD_IS_NULL)
    @NotBlank(message = ConstantUser.ERROR_PASSWORD_IS_BLANK)
    @NotEmpty(message = ConstantUser.ERROR_PASSWORD_IS_EMPTY)
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
