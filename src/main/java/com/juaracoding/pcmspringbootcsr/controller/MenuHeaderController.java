package com.juaracoding.pcmspringbootcsr.controller;

import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageGlobal;
import com.juaracoding.pcmspringbootcsr.core.security.ModulAuthority;
import com.juaracoding.pcmspringbootcsr.dto.menuheader.MenuHeaderDTO;
import com.juaracoding.pcmspringbootcsr.handler.ResponseHandler;
import com.juaracoding.pcmspringbootcsr.model.MenuHeader;
import com.juaracoding.pcmspringbootcsr.service.MenuHeaderService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
    code number authorization 12 ==> bwu
 */
@RestController
@RequestMapping("/api/usrmgmnt")
public class MenuHeaderController {
    private MenuHeaderService menuHeaderService;
    @Autowired
    private ModelMapper modelMapper;
    private Map<String,Object> objectMapper = new HashMap<String,Object>();
    private Map<String,String> mapSorting = new HashMap<String,String>();
    private List<MenuHeader> lsCPUpload = new ArrayList<MenuHeader>();
    private String [] strExceptionArr = new String[2];
    private String strAuthorization = "";
    private Map<String,Object> mapToken = new HashMap<String,Object>();
    @Autowired
    private ModulAuthority modulAuthority;

    @Autowired
    public MenuHeaderController(MenuHeaderService menuHeaderService) {
        strExceptionArr [0] = "MenuHeaderController";
        mapSorting();
        this.menuHeaderService = menuHeaderService;
    }

    private void mapSorting()
    {
        mapSorting.put("id","idMenuHeader");
        mapSorting.put("nama","namaMenuHeader");
        mapSorting.put("deskripsi","deskripsiMenuHeader");
    }

    @PostMapping("/menuheader/v1")
    public ResponseEntity<Object> save(@Valid @RequestBody MenuHeaderDTO menuHeaderDTO
            , HttpServletRequest request
    )
    {
        MenuHeader menuHeader = modelMapper.map(menuHeaderDTO, new TypeToken<MenuHeader>() {}.getType());
        return menuHeaderService.save(menuHeader,request);
    }
    @PostMapping("/menuheader/v1/batch")
    public ResponseEntity<Object> saveBatch(@RequestBody List<MenuHeaderDTO>  listMenuHeaderDTO
            , HttpServletRequest request
    )
    {
        List<MenuHeader> listMenuHeader = modelMapper.map(listMenuHeaderDTO, new TypeToken<List<MenuHeader>>() {}.getType());
        return menuHeaderService.saveBatch(listMenuHeader,request);
    }

    @PutMapping("/menuheader/v1/{id}")
    public ResponseEntity<Object> edit(@Valid @RequestBody MenuHeaderDTO menuHeaderDTO,
                                       @PathVariable("id") Long id,
                                       HttpServletRequest request
    )
    {
        MenuHeader menuHeader = modelMapper.map(menuHeaderDTO, new TypeToken<MenuHeader>() {}.getType());
        return menuHeaderService.edit(id,menuHeader,request);
    }

    @GetMapping("/menuheader/v1/{id}")
    public ResponseEntity<Object> findById(HttpServletRequest request, @PathVariable("id") Long id)
    {
        return menuHeaderService.findById(id,request);
    }

    @GetMapping("/menuheader/v1")
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
            sortzBy = mapSorting.get(sortzBy);// id = idDivisi, nama = namaDivisi dst....
            Pageable pageable = PageRequest.of(pagez,Integer.parseInt(sizeComponent.equals("")?"10":sizeComponent),
                    sortz.equals("desc")? Sort.by(sortzBy).descending():Sort.by(sortzBy));
            return menuHeaderService.find(pageable,columnFirst,valueFirst,request);
        }

        return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_NOT_AVAILABLE,
                HttpStatus.BAD_REQUEST,
                null,
                "Not Available", request);
    }

    @DeleteMapping("/menuheader/v1/{id}")
    public ResponseEntity<Object> delete(HttpServletRequest request, @PathVariable("id") Long id)
    {
        return menuHeaderService.delete(id,request);
    }

    @PostMapping("/menuheader/v1/uploaddata")
    public ResponseEntity<Object> export(@RequestParam("datamenuheader")
                                         @RequestHeader MultipartFile file,
                                         HttpServletRequest request)
    {

        return menuHeaderService.dataToExport(file,request);
    }


}
