package com.juaracoding.pcmspringbootcsr.dto.tutor;/*
Created by IntelliJ IDEA 2022.2.3 (Community Edition)
Build #IC-232.10072.27, built on October 11, 2023
@Author LENOVO a.k.a. Virdha Haniva Pratiwie Ishak
Java Developer
Created on 03/01/2024 22:29
@Last Modified 03/01/2024 22:29
Version 1.0
*/

import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageTutor;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class TutorDTO {
    private Long idTutor;
    @NotNull(message = ConstantMessageTutor.ERROR_NAMA_TUTOR_IS_NULL)
    @NotBlank(message = ConstantMessageTutor.ERROR_NAMA_TUTOR_IS_BLANK)
    @NotEmpty(message = ConstantMessageTutor.ERROR_NAMA_TUTOR_IS_EMPTY)
    private String namaTutor;
    @NotNull(message = ConstantMessageTutor.ERROR_ALAMAT_IS_NULL)
    @NotBlank(message = ConstantMessageTutor.ERROR_ALAMAT_IS_BLANK)
    @NotEmpty(message = ConstantMessageTutor.ERROR_ALAMAT_IS_EMPTY)
    @Length(max=255,message = ConstantMessageTutor.ERROR_ALAMAT_LENGTH)
    private String alamat;
    @NotNull(message = ConstantMessageTutor.ERROR_NO_HP_IS_NULL)
    @NotBlank(message = ConstantMessageTutor.ERROR_NO_HP_IS_BLANK)
    @NotEmpty(message = ConstantMessageTutor.ERROR_NO_HP_IS_EMPTY)
    private String noHp;
    @NotNull(message = ConstantMessageTutor.ERROR_EMAIL_IS_NULL)
    @NotBlank(message = ConstantMessageTutor.ERROR_EMAIL_IS_BLANK)
    @NotEmpty(message = ConstantMessageTutor.ERROR_EMAIL_IS_EMPTY)
    private String email;

    public Long getIdTutor() {
        return idTutor;
    }

    public void setIdTutor(Long idTutor) {
        this.idTutor = idTutor;
    }

    public String getNamaTutor() {
        return namaTutor;
    }

    public void setNamaTutor(String namaTutor) {
        this.namaTutor = namaTutor;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

