package com.juaracoding.pcmspringbootcsr.dto.bahasa;/*
Created by IntelliJ IDEA 2022.2.3 (Community Edition)
Build #IC-232.10072.27, built on October 11, 2023
@Author LENOVO a.k.a. Virdha Haniva Pratiwie Ishak
Java Developer
Created on 02/01/2024 13:23
@Last Modified 02/01/2024 13:23
Version 1.0
*/

import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageBahasa;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class BahasaDTO {
    private Long idBahasa;
    @NotNull(message = ConstantMessageBahasa.ERROR_NAMA_BAHASA_IS_NULL)
    @NotBlank(message = ConstantMessageBahasa.ERROR_NAMA_BAHASA_IS_BLANK)
    @NotEmpty(message = ConstantMessageBahasa.ERROR_NAMA_BAHASA_IS_EMPTY)
    private String namaBahasa;
    @NotNull(message = ConstantMessageBahasa.ERROR_KODE_BAHASA_IS_NULL)
    @NotBlank(message = ConstantMessageBahasa.ERROR_KODE_BAHASA_IS_BLANK)
    @NotEmpty(message = ConstantMessageBahasa.ERROR_KODE_BAHASA_IS_EMPTY)
    @Length(max = 3, message = ConstantMessageBahasa.ERROR_KODE_BAHASA_LENGTH)
    private String kodeBahasa;

    public Long getIdBahasa() {
        return idBahasa;
    }

    public void setIdBahasa(Long idBahasa) {
        this.idBahasa = idBahasa;
    }

    public String getNamaBahasa() {
        return namaBahasa;
    }

    public void setNamaBahasa(String namaBahasa) {
        this.namaBahasa = namaBahasa;
    }

    public String getKodeBahasa() {
        return kodeBahasa;
    }

    public void setKodeBahasa(String kodeBahasa) {
        this.kodeBahasa = kodeBahasa;
    }
}


