package com.juaracoding.pcmspringbootcsr.dto.siswa;/*
Created by IntelliJ IDEA 2022.2.3 (Community Edition)
Build #IC-232.10072.27, built on October 11, 2023
@Author LENOVO a.k.a. Virdha Haniva Pratiwie Ishak
Java Developer
Created on 04/01/2024 10:37
@Last Modified 04/01/2024 10:37
Version 1.0
*/

import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageSiswa;
import com.juaracoding.pcmspringbootcsr.dto.matapelajaran.MataPelajaranOptionDTO;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class SiswaDTO {

    private Long idSiswa;
    @NotNull(message = ConstantMessageSiswa.ERROR_NAMA_SISWA_IS_NULL)
    @NotBlank(message = ConstantMessageSiswa.ERROR_NAMA_SISWA_IS_BLANK)
    @NotEmpty(message = ConstantMessageSiswa.ERROR_NAMA_SISWA_IS_EMPTY)
    private String namaSiswa;
    @NotNull(message = ConstantMessageSiswa.ERROR_ALAMAT_IS_NULL)
    @NotBlank(message = ConstantMessageSiswa.ERROR_ALAMAT_IS_BLANK)
    @NotEmpty(message = ConstantMessageSiswa.ERROR_ALAMAT_IS_EMPTY)
    @Length(max=255,message = ConstantMessageSiswa.ERROR_ALAMAT_LENGTH)
    private String alamat;
    @NotNull(message = ConstantMessageSiswa.ERROR_NO_HP_IS_NULL)
    @NotBlank(message = ConstantMessageSiswa.ERROR_NO_HP_IS_BLANK)
    @NotEmpty(message = ConstantMessageSiswa.ERROR_NO_HP_IS_EMPTY)
    private String noHp;
    @NotNull(message = ConstantMessageSiswa.ERROR_EMAIL_IS_NULL)
    @NotBlank(message = ConstantMessageSiswa.ERROR_EMAIL_IS_BLANK)
    @NotEmpty(message = ConstantMessageSiswa.ERROR_EMAIL_IS_EMPTY)
    private String email;
    private List<MataPelajaranOptionDTO> listMataPelajaran;

    public Long getIdSiswa() {
        return idSiswa;
    }

    public void setIdSiswa(Long idSiswa) {
        this.idSiswa = idSiswa;
    }

    public String getNamaSiswa() {
        return namaSiswa;
    }

    public void setNamaSiswa(String namaSiswa) {
        this.namaSiswa = namaSiswa;
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

    public List<MataPelajaranOptionDTO> getListMataPelajaran() {
        return listMataPelajaran;
    }

    public void setListMataPelajaran(List<MataPelajaranOptionDTO> listMataPelajaran) {
        this.listMataPelajaran = listMataPelajaran;
    }
}

