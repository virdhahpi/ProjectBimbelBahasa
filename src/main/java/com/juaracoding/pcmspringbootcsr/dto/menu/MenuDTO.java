package com.juaracoding.pcmspringbootcsr.dto.menu;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageMenu;
import com.juaracoding.pcmspringbootcsr.dto.akses.AksesDTO;
import com.juaracoding.pcmspringbootcsr.dto.menuheader.MenuHeaderOptionDTO;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class MenuDTO {
    private Long idMenu;

    @NotNull(message = ConstantMessageMenu.ERROR_MENU_HEADER_IS_NULL)
    private MenuHeaderOptionDTO menuHeader;
    @NotNull(message = ConstantMessageMenu.ERROR_NAMA_MENU_IS_NULL)
    @NotBlank(message = ConstantMessageMenu.ERROR_NAMA_MENU_IS_BLANK)
    @NotEmpty(message = ConstantMessageMenu.ERROR_NAMA_MENU_IS_EMPTY)
    @Length(min = 3,max = 25, message = ConstantMessageMenu.ERROR_NAMA_MENU_LENGTH)
    private String namaMenu;
    @NotNull(message = ConstantMessageMenu.ERROR_PATH_MENU_IS_NULL)
    @NotBlank(message = ConstantMessageMenu.ERROR_PATH_MENU_IS_BLANK)
    @NotEmpty(message = ConstantMessageMenu.ERROR_PATH_MENU_IS_EMPTY)
    @Length(min = 5,max = 50, message = ConstantMessageMenu.ERROR_PATH_MENU_LENGTH)
    private String pathMenu;
    @NotNull(message = ConstantMessageMenu.ERROR_END_POINT_MENU_IS_NULL)
    @NotBlank(message = ConstantMessageMenu.ERROR_END_POINT_MENU_IS_BLANK)
    @NotEmpty(message = ConstantMessageMenu.ERROR_END_POINT_MENU_IS_EMPTY)
    @Length(min = 15,max = 30, message = ConstantMessageMenu.ERROR_END_POINT_MENU_LENGTH)
    private String endPoint;

    @JsonBackReference
    private List<AksesDTO> listAksesMenu;

    public List<AksesDTO> getListAksesMenu() {
        return listAksesMenu;
    }

    public void setListAksesMenu(List<AksesDTO> listAksesMenu) {
        this.listAksesMenu = listAksesMenu;
    }

    public MenuHeaderOptionDTO getMenuHeader() {
        return menuHeader;
    }

    public void setMenuHeader(MenuHeaderOptionDTO menuHeader) {
        this.menuHeader = menuHeader;
    }

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
    public String getPathMenu() {
        return pathMenu;
    }
    public void setPathMenu(String pathMenu) {
        this.pathMenu = pathMenu;
    }
    public String getEndPoint() {
        return endPoint;
    }
    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }
}
