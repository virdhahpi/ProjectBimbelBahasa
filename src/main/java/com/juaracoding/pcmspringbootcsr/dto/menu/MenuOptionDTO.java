package com.juaracoding.pcmspringbootcsr.dto.menu;

import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageMenu;
import com.juaracoding.pcmspringbootcsr.dto.menuheader.MenuHeaderOptionDTO;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class MenuOptionDTO {
    private Long idMenu;

    private String namaMenu;

    public Long getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(Long idMenu) {
        this.idMenu = idMenu;
    }
    public String getNamaMenu() {
        return namaMenu;
    }
    public void setNamaMenu(String namaMenu) {
        this.namaMenu = namaMenu;
    }
}
