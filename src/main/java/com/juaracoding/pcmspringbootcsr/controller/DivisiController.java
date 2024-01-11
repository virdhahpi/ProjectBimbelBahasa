package com.juaracoding.pcmspringbootcsr.controller;

import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageGlobal;
import com.juaracoding.pcmspringbootcsr.dto.divisi.DivisiDTO;
import com.juaracoding.pcmspringbootcsr.handler.ResponseHandler;
import com.juaracoding.pcmspringbootcsr.model.Divisi;
import com.juaracoding.pcmspringbootcsr.service.DivisiService;
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
    code number authorization 11 ==> bwu

    zz
    00
    yy => 11
    xx => 22
pqrstuvwxyz
109876543210
 */
@RestController
@RequestMapping("/api/usrmgmnt")
public class DivisiController {
    private DivisiService divisiService;
    @Autowired
    private ModelMapper modelMapper;
    private Map<String,Object> objectMapper = new HashMap<String,Object>();
    private Map<String,String> mapSorting = new HashMap<String,String>();
    private List<Divisi> lsCPUpload = new ArrayList<Divisi>();
    private String [] strExceptionArr = new String[2];

    @Autowired
    public DivisiController(DivisiService divisiService) {
        strExceptionArr[0] = "DivisiController";
        mapSorting();
        this.divisiService = divisiService;
    }

    private void mapSorting()
    {
        mapSorting.put("id","idDivisi");
        mapSorting.put("nama","namaDivisi");
        mapSorting.put("kode","kodeDivisi");
        mapSorting.put("deskripsi","deskripsiDivisi");
    }

    /*
        {
            "namaDivisi" : "Marketing",
            "kodeDivisi" : "MKT01",
            "deskripsiDivisi" : "SALES FORCE SALES FORCE SALES FORCE SALES FORCE"
        }
     */
    @PostMapping("/divisi/v1")
    public ResponseEntity<Object> save(@Valid @RequestBody DivisiDTO divisiDTO
            , HttpServletRequest request
    )
    {
        Divisi divisi = modelMapper.map(divisiDTO, new TypeToken<Divisi>() {}.getType());
        return divisiService.save(divisi,request);
    }
    @PostMapping("/divisi/v1/batch")
    public ResponseEntity<Object> saveBatch(@RequestBody List<DivisiDTO>  listDivisiDTO
            , HttpServletRequest request
    )
    {
        List<Divisi> listDivisi = modelMapper.map(listDivisiDTO, new TypeToken<List<Divisi>>() {}.getType());
        return divisiService.saveBatch(listDivisi,request);
    }

    @PutMapping("/divisi/v1/{id}")
    public ResponseEntity<Object> edit(@Valid @RequestBody DivisiDTO divisiDTO,
                                     @PathVariable("id") Long id,
                                     HttpServletRequest request
    )
    {
        Divisi divisi = modelMapper.map(divisiDTO, new TypeToken<Divisi>() {}.getType());
        return divisiService.edit(id,divisi,request);
    }

    @GetMapping("/divisi/v1/{id}")
    public ResponseEntity<Object> findById(HttpServletRequest request, @PathVariable("id") Long id)
    {
        return divisiService.findById(id,request);
    }

    /*
        dijadikan bulk edit dulu di postman
        INI QUERY PARAMS NYA :
        page:0
        sort:asc
        sortby:id
        columnFirst:nama
        valueFirst:mat
        sizeComponent:3
     */
    @GetMapping("/divisi/v1")
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
            return divisiService.find(pageable,columnFirst,valueFirst,request);
        }
        /*
            jika tidak ada data maka ada notifikasi tidak tersedia di response nya
         */
        return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_NOT_AVAILABLE,
                HttpStatus.BAD_REQUEST,
                null,
                "Not Available", request);
    }

    @DeleteMapping("/divisi/v1/{id}")
    public ResponseEntity<Object> delete(HttpServletRequest request, @PathVariable("id") Long id)
    {
        return divisiService.delete(id,request);
    }

    @PostMapping("/divisi/v1/uploaddata")
    public ResponseEntity<Object> export(@RequestParam("datadivisi")
                                        @RequestHeader MultipartFile file,
                                        HttpServletRequest request)
    {
        return divisiService.dataToExport(file,request);//untuk data BULK
    }
}