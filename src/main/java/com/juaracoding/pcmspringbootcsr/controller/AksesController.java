package com.juaracoding.pcmspringbootcsr.controller;

import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageGlobal;
import com.juaracoding.pcmspringbootcsr.core.security.ModulAuthority;
import com.juaracoding.pcmspringbootcsr.dto.akses.AksesDTO;
import com.juaracoding.pcmspringbootcsr.handler.ResponseHandler;
import com.juaracoding.pcmspringbootcsr.model.Akses;
import com.juaracoding.pcmspringbootcsr.service.AksesService;
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

@RestController
@RequestMapping("/api/usrmgmnt")
public class AksesController {

    private AksesService aksesService;
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
    public AksesController(AksesService aksesService) {
        strExceptionArr [0] = "AksesController";
        mapSorting();
        this.aksesService = aksesService;
    }
    private void mapSorting()
    {
        mapSorting.put("id","idAkses");
        mapSorting.put("nama","namaAkses");
    }

    @PostMapping("/akses/v1")
    public ResponseEntity<Object> save(@Valid @RequestBody AksesDTO aksesDTO
            , HttpServletRequest request
    )
    {
        Akses akses = modelMapper.map(aksesDTO, new TypeToken<Akses>() {}.getType());
        return aksesService.save(akses,request);
    }
    @PostMapping("/akses/v1/batch")
    public ResponseEntity<Object> saveBatch(@RequestBody List<AksesDTO> listAksesDTO
            , HttpServletRequest request
    )
    {
        List<Akses> listAkses = modelMapper.map(listAksesDTO, new TypeToken<List<Akses>>() {}.getType());
        return aksesService.saveBatch(listAkses,request);
    }

    @PutMapping("/akses/v1/{id}")
    public ResponseEntity<Object> edit(@Valid @RequestBody AksesDTO aksesDTO,
                                       @PathVariable("id") Long id,
                                       HttpServletRequest request
    )
    {
        Akses akses = modelMapper.map(aksesDTO, new TypeToken<Akses>() {}.getType());
        return aksesService.edit(id,akses,request);
    }

    @GetMapping("/akses/v1/{id}")
    public ResponseEntity<Object> findById(HttpServletRequest request, @PathVariable("id") Long id)
    {
        return aksesService.findById(id,request);
    }

    @GetMapping("/akses/v1")
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
            sortzBy = mapSorting.get(sortzBy);// id = idAkses, nama = namaAkses dst....
            Pageable pageable = PageRequest.of(pagez,Integer.parseInt(sizeComponent.equals("")?"10":sizeComponent),
                    sortz.equals("desc")? Sort.by(sortzBy).descending():Sort.by(sortzBy));
            return aksesService.find(pageable,columnFirst,valueFirst,request);
        }

        return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_NOT_AVAILABLE,
                HttpStatus.BAD_REQUEST,
                null,
                "Not Available", request);
    }

    @DeleteMapping("/akses/v1/{id}")
    public ResponseEntity<Object> delete(HttpServletRequest request, @PathVariable("id") Long id)
    {
        return aksesService.delete(id,request);
    }

    /*
        upload data tidak ada di modul ini
     */
}
