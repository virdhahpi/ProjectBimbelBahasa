package com.juaracoding.pcmspringbootcsr.controller;/*
Created by IntelliJ IDEA 2022.2.3 (Community Edition)
Build #IC-232.10072.27, built on October 11, 2023
@Author LENOVO a.k.a. Virdha Haniva Pratiwie Ishak
Java Developer
Created on 02/01/2024 13:23
@Last Modified 02/01/2024 13:23
Version 1.0
*/

import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageGlobal;
import com.juaracoding.pcmspringbootcsr.dto.bahasa.BahasaDTO;
import com.juaracoding.pcmspringbootcsr.handler.ResponseHandler;
import com.juaracoding.pcmspringbootcsr.model.Bahasa;
import com.juaracoding.pcmspringbootcsr.service.BahasaService;
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

@RestController
@RequestMapping("/api/program")
public class BahasaController {
    private BahasaService bahasaService;
    @Autowired
    private ModelMapper modelMapper;
    private Map<String,Object> objectMapper = new HashMap<String,Object>();
    private Map<String,String> mapSorting = new HashMap<String,String>();
    private List<Bahasa> lsCPUpload = new ArrayList<Bahasa>();
    private String [] strExceptionArr = new String[2];

    @Autowired
    public BahasaController(BahasaService bahasaService) {
        strExceptionArr[0] = "BahasaController";
        mapSorting();
        this.bahasaService = bahasaService;
    }

    private void mapSorting()
    {
        mapSorting.put("id","idBahasa");
        mapSorting.put("nama","namaBahasa");
        mapSorting.put("kode","kodeBahasa");
    }

    /*
        {
            "namaBahasa" : "Korea",
            "kodeBahasa" : "KOR",
        }
     */
    @PostMapping("/bahasa/v1")
    public ResponseEntity<Object> save(@Valid @RequestBody BahasaDTO BahasaDTO
            , HttpServletRequest request
    )
    {
        Bahasa bahasa = modelMapper.map(BahasaDTO, new TypeToken<Bahasa>() {}.getType());
        return bahasaService.save(bahasa,request);
    }
    @PostMapping("/bahasa/v1/batch")
    public ResponseEntity<Object> saveBatch(@RequestBody List<BahasaDTO>  listBahasaDTO
            , HttpServletRequest request
    )
    {
        List<Bahasa> listBahasa = modelMapper.map(listBahasaDTO, new TypeToken<List<Bahasa>>() {}.getType());
        return bahasaService.saveBatch(listBahasa,request);
    }

    @PutMapping("/bahasa/v1/{id}")
    public ResponseEntity<Object> edit(@Valid @RequestBody BahasaDTO BahasaDTO,
                                       @PathVariable("id") Long id,
                                       HttpServletRequest request
    )
    {
        Bahasa bahasa = modelMapper.map(BahasaDTO, new TypeToken<Bahasa>() {}.getType());
        return bahasaService.edit(id,bahasa,request);
    }

    @GetMapping("/bahasa/v1/{id}")
    public ResponseEntity<Object> findById(HttpServletRequest request, @PathVariable("id") Long id)
    {
        return bahasaService.findById(id,request);
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
    @GetMapping("/bahasa/v1")
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
            sortzBy = mapSorting.get(sortzBy);// id = idBahasa, nama = namaBahasa dst....
            Pageable pageable = PageRequest.of(pagez,Integer.parseInt(sizeComponent.equals("")?"10":sizeComponent),
                    sortz.equals("desc")? Sort.by(sortzBy).descending():Sort.by(sortzBy));
            return bahasaService.find(pageable,columnFirst,valueFirst,request);
        }
        /*
            jika tidak ada data maka ada notifikasi tidak tersedia di response nya
         */
        return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_NOT_AVAILABLE,
                HttpStatus.BAD_REQUEST,
                null,
                "Not Available", request);
    }

    @DeleteMapping("/bahasa/v1/{id}")
    public ResponseEntity<Object> delete(HttpServletRequest request, @PathVariable("id") Long id)
    {
        return bahasaService.delete(id,request);
    }

    @PostMapping("/bahasa/v1/uploaddata")
    public ResponseEntity<Object> export(@RequestParam("dataBahasa")
                                         @RequestHeader MultipartFile file,
                                         HttpServletRequest request)
    {
        return bahasaService.dataToExport(file,request);//untuk data BULK
    }
}

