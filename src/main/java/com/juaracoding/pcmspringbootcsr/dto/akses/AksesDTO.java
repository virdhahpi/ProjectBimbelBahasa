package com.juaracoding.pcmspringbootcsr.dto.akses;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageAkses;
import com.juaracoding.pcmspringbootcsr.dto.divisi.DivisiDTO;
import com.juaracoding.pcmspringbootcsr.dto.divisi.DivisiOptionDTO;
import com.juaracoding.pcmspringbootcsr.dto.menu.MenuDTO;
import com.juaracoding.pcmspringbootcsr.dto.menu.MenuOptionDTO;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class AksesDTO {

    private Long idAkses;

    @NotNull(message = ConstantMessageAkses.ERROR_NAMA_AKSES_IS_NULL)
    @NotBlank(message = ConstantMessageAkses.ERROR_NAMA_AKSES_IS_BLANK)
    @NotEmpty(message = ConstantMessageAkses.ERROR_NAMA_AKSES_IS_EMPTY)
    @Length(min = 5,max = 25, message = ConstantMessageAkses.ERROR_NAMA_AKSES_LENGTH)
    private String namaAkses;

    private List<MenuOptionDTO> listMenuAkses;

    @NotNull(message = ConstantMessageAkses.ERROR_DIVISI_IS_NULL)
    private DivisiOptionDTO divisi;

    public Long getIdAkses() {
        return idAkses;
    }

    public void setIdAkses(Long idAkses) {
        this.idAkses = idAkses;
    }

    public String getNamaAkses() {
        return namaAkses;
    }

    public void setNamaAkses(String namaAkses) {
        this.namaAkses = namaAkses;
    }

    public List<MenuOptionDTO> getListMenuAkses() {
        return listMenuAkses;
    }

    public void setListMenuAkses(List<MenuOptionDTO> listMenuAkses) {
        this.listMenuAkses = listMenuAkses;
    }

    public DivisiOptionDTO getDivisi() {
        return divisi;
    }

    public void setDivisi(DivisiOptionDTO divisi) {
        this.divisi = divisi;
    }
}
