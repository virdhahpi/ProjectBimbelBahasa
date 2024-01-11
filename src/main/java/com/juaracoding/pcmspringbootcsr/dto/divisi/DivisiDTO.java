package com.juaracoding.pcmspringbootcsr.dto.divisi;

import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageDivisi;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class DivisiDTO {

    private Long idDivisi;
    @NotNull(message = ConstantMessageDivisi.ERROR_NAMA_DIVISI_IS_NULL)
    @NotBlank(message = ConstantMessageDivisi.ERROR_NAMA_DIVISI_IS_BLANK)
    @NotEmpty(message = ConstantMessageDivisi.ERROR_NAMA_DIVISI_IS_EMPTY)
    @Length(min = 5, max = 25, message = ConstantMessageDivisi.ERROR_NAMA_DIVISI_LENGTH)
    private String namaDivisi;

    @NotNull(message = ConstantMessageDivisi.ERROR_KODE_DIVISI_IS_NULL)
    @NotBlank(message = ConstantMessageDivisi.ERROR_KODE_DIVISI_IS_BLANK)
    @NotEmpty(message = ConstantMessageDivisi.ERROR_KODE_DIVISI_IS_EMPTY)
    @Length(min = 3, max = 5, message = ConstantMessageDivisi.ERROR_KODE_DIVISI_LENGTH)
    private String kodeDivisi;

    @NotNull(message = ConstantMessageDivisi.ERROR_DESKRIPSI_DIVISI_IS_NULL)
    @NotBlank(message = ConstantMessageDivisi.ERROR_DESKRIPSI_DIVISI_IS_BLANK)
    @NotEmpty(message = ConstantMessageDivisi.ERROR_DESKRIPSI_DIVISI_IS_EMPTY)
    @Length(min = 15, max = 55, message = ConstantMessageDivisi.ERROR_DESKRIPSI_DIVISI_LENGTH)
    private String deskripsiDivisi;

    public Long getIdDivisi() {
        return idDivisi;
    }

    public void setIdDivisi(Long idDivisi) {
        this.idDivisi = idDivisi;
    }

    public String getNamaDivisi() {
        return namaDivisi;
    }

    public void setNamaDivisi(String namaDivisi) {
        this.namaDivisi = namaDivisi;
    }

    public String getKodeDivisi() {
        return kodeDivisi;
    }

    public void setKodeDivisi(String kodeDivisi) {
        this.kodeDivisi = kodeDivisi;
    }

    public String getDeskripsiDivisi() {
        return deskripsiDivisi;
    }

    public void setDeskripsiDivisi(String deskripsiDivisi) {
        this.deskripsiDivisi = deskripsiDivisi;
    }
}
