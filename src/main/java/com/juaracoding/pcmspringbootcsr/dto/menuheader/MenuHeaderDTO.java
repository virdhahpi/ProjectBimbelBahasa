package com.juaracoding.pcmspringbootcsr.dto.menuheader;

import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageMenuHeader;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class MenuHeaderDTO {

    private Long idMenuHeader;

    @NotNull(message = ConstantMessageMenuHeader.ERROR_NAMA_MENU_HEADER_IS_NULL)
    @NotBlank(message = ConstantMessageMenuHeader.ERROR_NAMA_MENU_HEADER_IS_BLANK)
    @NotEmpty(message = ConstantMessageMenuHeader.ERROR_NAMA_MENU_HEADER_IS_EMPTY)
    @Pattern(regexp = ConstantMessageMenuHeader.REGEX_NAMA_MENU_HEADER_LENGTH,message = ConstantMessageMenuHeader.ERROR_DESKRIPSI_MENU_HEADER_LENGTH)
    private String namaMenuHeader;

    @NotNull(message = ConstantMessageMenuHeader.ERROR_DESKRIPSI_MENU_HEADER_IS_NULL)
    @NotBlank(message = ConstantMessageMenuHeader.ERROR_DESKRIPSI_MENU_HEADER_IS_BLANK)
    @NotEmpty(message = ConstantMessageMenuHeader.ERROR_DESKRIPSI_MENU_HEADER_IS_EMPTY)
    @Pattern(regexp = ConstantMessageMenuHeader.REGEX_DESKRIPSI_MENU_HEADER_LENGTH,message = ConstantMessageMenuHeader.ERROR_DESKRIPSI_MENU_HEADER_LENGTH)
    private String deskripsiMenuHeader;

    public Long getIdMenuHeader() {
        return idMenuHeader;
    }

    public void setIdMenuHeader(Long idMenuHeader) {
        this.idMenuHeader = idMenuHeader;
    }

    public String getNamaMenuHeader() {
        return namaMenuHeader;
    }

    public void setNamaMenuHeader(String namaMenuHeader) {
        this.namaMenuHeader = namaMenuHeader;
    }

    public String getDeskripsiMenuHeader() {
        return deskripsiMenuHeader;
    }

    public void setDeskripsiMenuHeader(String deskripsiMenuHeader) {
        this.deskripsiMenuHeader = deskripsiMenuHeader;
    }
}
