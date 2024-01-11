package com.juaracoding.pcmspringbootcsr.dto.regis;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.juaracoding.pcmspringbootcsr.constant.ConstantRegex;
import com.juaracoding.pcmspringbootcsr.constant.ConstantUser;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

public class RegisDTO {

    @NotNull(message = ConstantUser.ERROR_USERNAME_IS_NULL)
    @NotBlank(message = ConstantUser.ERROR_USERNAME_IS_BLANK)
    @NotEmpty(message = ConstantUser.ERROR_USERNAME_IS_EMPTY)
    @Pattern(regexp = "^\\w{8,15}$",message = "Format user tidak boleh spasi (minimal 8 maksimal 15)")
    private String username;

    @NotNull(message = ConstantUser.ERROR_PASSWORD_IS_NULL)
    @NotBlank(message = ConstantUser.ERROR_PASSWORD_IS_BLANK)
    @NotEmpty(message = ConstantUser.ERROR_PASSWORD_IS_EMPTY)
    @Pattern(regexp = ConstantRegex.REGEX_PASSWORD,message = ConstantRegex.ERROR_PASSWORD_INVALID)
    private String password;


    @NotNull(message = ConstantUser.ERROR_EMAIL_IS_NULL)
    @NotBlank(message = ConstantUser.ERROR_EMAIL_IS_BLANK)
    @NotEmpty(message = ConstantUser.ERROR_EMAIL_IS_EMPTY)
    @Pattern(regexp = ConstantRegex.EMAIL_RFC532,message = ConstantRegex.EMAIL_RFC532_INVALID)
    private String email;


    @NotNull(message = ConstantUser.ERROR_NO_HP_IS_NULL)
    @NotBlank(message = ConstantUser.ERROR_NO_HP_IS_BLANK)
    @NotEmpty(message = ConstantUser.ERROR_NO_HP_IS_EMPTY)
    @Pattern(regexp = ConstantRegex.REGEX_NO_HP,message = ConstantRegex.ERROR_NO_HP_INVALID)//((62 | 0 )8)([0-9].{8,12})
    private String noHp;

    @NotNull(message = ConstantUser.ERROR_NAMA_LENGKAP_IS_NULL)
    @NotBlank(message = ConstantUser.ERROR_NAMA_LENGKAP_IS_BLANK)
    @NotEmpty(message = ConstantUser.ERROR_NAMA_LENGKAP_IS_EMPTY)
    @Pattern(regexp = ConstantRegex.REGEX_NAMA_LENGKAP,message = ConstantRegex.ERROR_NAMA_LENGKAP_INVALID)
    private String namaLengkap;

    @NotNull(message = ConstantUser.ERROR_ALAMAT_IS_NULL)
    @NotBlank(message = ConstantUser.ERROR_ALAMAT_IS_BLANK)
    @NotEmpty(message = ConstantUser.ERROR_ALAMAT_IS_EMPTY)
    @Length(min=40,max = 255,message = ConstantUser.ERROR_ALAMAT_LENGTH)
    private String alamat;

    /*
        untuk date karena format default setelah menggunakan security adalah jackson
        maka di setiap field yang menggunakan date time java harus diberi anotasi
        @JsonIgnore
     */
    @NotNull(message = ConstantUser.ERROR_DOB_IS_NULL)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate tanggalLahir;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public LocalDate getTanggalLahir() {
        return tanggalLahir;
    }

    public void setTanggalLahir(LocalDate tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }
}
