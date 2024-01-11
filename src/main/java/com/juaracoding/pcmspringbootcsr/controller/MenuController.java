package com.juaracoding.pcmspringbootcsr.controller;

import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageGlobal;
import com.juaracoding.pcmspringbootcsr.core.security.ModulAuthority;
import com.juaracoding.pcmspringbootcsr.dto.menu.MenuDTO;
import com.juaracoding.pcmspringbootcsr.handler.ResponseHandler;
import com.juaracoding.pcmspringbootcsr.model.Menu;
import com.juaracoding.pcmspringbootcsr.service.MenuService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
    code number authorization 13 ==> xyt
 */
@RestController
@RequestMapping("/api/usrmgmnt")
public class MenuController {

    private MenuService menuService;
    @Autowired
    private ModelMapper modelMapper;
    private Map<String,Object> objectMapper = new HashMap<String,Object>();
    private Map<String,String> mapSorting = new HashMap<String,String>();
    private String [] strExceptionArr = new String[2];
    private String strAuthorization = "";
    private Map<String,Object> mapToken = new HashMap<String,Object>();
    @Autowired
    private ModulAuthority modulAuthority;

    @Autowired
    public MenuController(MenuService menuService) {
        strExceptionArr [0] = "MenuController";
        mapSorting();
        this.menuService = menuService;
    }
    private void mapSorting()
    {
        mapSorting.put("id","idMenu");
        mapSorting.put("nama","namaMenu");
        mapSorting.put("path","pathMenu");
        mapSorting.put("endPoint","endPoint");
        mapSorting.put("group","namaMenuHeader");
    }

    @PostMapping("/menu/v1")
    public ResponseEntity<Object> save(@Valid @RequestBody MenuDTO menuDTO
            , HttpServletRequest request
    )
    {
        Menu menu = modelMapper.map(menuDTO, new TypeToken<Menu>() {}.getType());
        return menuService.save(menu,request);
    }
    @PostMapping("/menu/v1/batch")
    public ResponseEntity<Object> saveBatch(@RequestBody List<MenuDTO>  listMenuDTO
            , HttpServletRequest request
    )
    {
        List<Menu> listMenu = modelMapper.map(listMenuDTO, new TypeToken<List<Menu>>() {}.getType());
        return menuService.saveBatch(listMenu,request);
    }

    @PutMapping("/menu/v1/{id}")
    public ResponseEntity<Object> edit(@Valid @RequestBody MenuDTO menuDTO,
                                       @PathVariable("id") Long id,
                                       HttpServletRequest request
    )
    {
        Menu menu = modelMapper.map(menuDTO, new TypeToken<Menu>() {}.getType());
        return menuService.edit(id,menu,request);
    }

    @GetMapping("/menu/v1/{id}")
    public ResponseEntity<Object> findById(HttpServletRequest request, @PathVariable("id") Long id)
    {
        return menuService.findById(id,request);
    }

    @GetMapping("/menu/v1")
    public ResponseEntity<Object> find(
            @RequestParam(value = "page") Integer pagez,
            @RequestParam(value = "sort") String sortz,//default nya pasti asc
            @RequestParam(value = "sortby") String sortzBy,//id, nama, deskripsi, kode
            @RequestParam(value = "columnFirst") String columnFirst,
            @RequestParam(value = "valueFirst") String valueFirst,
            @RequestParam(value = "sizeComponent") String sizeComponent,// jumlah data per page
            HttpServletRequest request
    ){
        Pageable page = null;
        pagez = pagez==null?0:pagez;
        sortzBy = (sortzBy==null || sortzBy.equals(""))?"id":sortzBy;//penanda kalau null dari FE itu berarti kayak buka menu baru
        sortz = (sortz.equals("") || sortz==null)? "asc" : sortzBy;

        if(!(pagez==null && sizeComponent==null)){
            sortzBy = mapSorting.get(sortzBy);// id = idMenu, nama = namaMenu dst....
            Pageable pageable = PageRequest.of(pagez,Integer.parseInt(sizeComponent.equals("")?"10":sizeComponent),
                    sortz.equals("desc")? Sort.by(sortzBy).descending():Sort.by(sortzBy));
            return menuService.find(pageable,columnFirst,valueFirst,request);
        }

        return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_NOT_AVAILABLE,
                HttpStatus.BAD_REQUEST,
                null,
                "Not Available", request);
    }

    @DeleteMapping("/menu/v1/{id}")
    public ResponseEntity<Object> delete(HttpServletRequest request, @PathVariable("id") Long id)
    {
        return menuService.delete(id,request);
    }

    /*
        upload data tidak ada di modul ini
     */
}
