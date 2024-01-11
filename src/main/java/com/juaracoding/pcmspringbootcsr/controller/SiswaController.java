package com.juaracoding.pcmspringbootcsr.controller;/*
Created by IntelliJ IDEA 2022.2.3 (Community Edition)
Build #IC-232.10072.27, built on October 11, 2023
@Author LENOVO a.k.a. Virdha Haniva Pratiwie Ishak
Java Developer
Created on 04/01/2024 11:47
@Last Modified 04/01/2024 11:47
Version 1.0
*/

import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageGlobal;
import com.juaracoding.pcmspringbootcsr.dto.siswa.SiswaDTO;
import com.juaracoding.pcmspringbootcsr.handler.ResponseHandler;
import com.juaracoding.pcmspringbootcsr.model.Siswa;
import com.juaracoding.pcmspringbootcsr.service.SiswaService;
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
public class SiswaController {
    private SiswaService siswaService;
    @Autowired
    private ModelMapper modelMapper;
    private Map<String,Object> objectMapper = new HashMap<String,Object>();
    private Map<String,String> mapSorting = new HashMap<String,String>();
    private List<Siswa> lsCPUpload = new ArrayList<Siswa>();
    private String [] strExceptionArr = new String[2];

    @Autowired
    public SiswaController(SiswaService siswaService) {
        strExceptionArr[0] = "SiswaController";
        mapSorting();
        this.siswaService = siswaService;
    }

    private void mapSorting()
    {
        mapSorting.put("id","idSiswa");
        mapSorting.put("nama","namaSiswa");
        mapSorting.put("alamat","alamat");
        mapSorting.put("nohp","noHp");
        mapSorting.put("email","email");
    }

    @PostMapping("/siswa/v1")
    public ResponseEntity<Object> save(@Valid @RequestBody SiswaDTO siswaDTO
            , HttpServletRequest request
    )
    {
        Siswa siswa = modelMapper.map(siswaDTO, new TypeToken<Siswa>() {}.getType());
        return siswaService.save(siswa,request);
    }
    @PostMapping("/siswa/v1/batch")
    public ResponseEntity<Object> saveBatch(@RequestBody List<SiswaDTO>  listSiswaDTO
            , HttpServletRequest request
    )
    {
        List<Siswa> listSiswa = modelMapper.map(listSiswaDTO, new TypeToken<List<Siswa>>() {}.getType());
        return siswaService.saveBatch(listSiswa,request);
    }

    @PutMapping("/siswa/v1/{id}")
    public ResponseEntity<Object> edit(@Valid @RequestBody SiswaDTO siswaDTO,
                                       @PathVariable("id") Long id,
                                       HttpServletRequest request
    )
    {
        Siswa siswa = modelMapper.map(siswaDTO, new TypeToken<Siswa>() {}.getType());
        return siswaService.edit(id,siswa,request);
    }

    @GetMapping("/siswa/v1/{id}")
    public ResponseEntity<Object> findById(HttpServletRequest request, @PathVariable("id") Long id)
    {
        return siswaService.findById(id,request);
    }

    @GetMapping("/siswa/v1/dashboard")
    public ResponseEntity<Object> dashboard(HttpServletRequest request, @PathVariable("id") Long id)
    {
        return siswaService.findById(id,request);
    }

    @GetMapping("/siswa/v1")
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
            sortzBy = mapSorting.get(sortzBy);// id = idSiswa, nama = namaSiswa dst....
            Pageable pageable = PageRequest.of(pagez,Integer.parseInt(sizeComponent.equals("")?"10":sizeComponent),
                    sortz.equals("desc")? Sort.by(sortzBy).descending():Sort.by(sortzBy));
            return siswaService.find(pageable,columnFirst,valueFirst,request);
        }
        /*
            jika tidak ada data maka ada notifikasi tidak tersedia di response nya
         */
        return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_NOT_AVAILABLE,
                HttpStatus.BAD_REQUEST,
                null,
                "Not Available", request);
    }

    @DeleteMapping("/siswa/v1/{id}")
    public ResponseEntity<Object> delete(HttpServletRequest request, @PathVariable("id") Long id)
    {
        return siswaService.delete(id,request);
    }

    @PostMapping("/siswa/v1/uploaddata")
    public ResponseEntity<Object> export(@RequestParam("datasiswa")
                                         @RequestHeader MultipartFile file,
                                         HttpServletRequest request)
    {
        return siswaService.dataToExport(file,request);//untuk data BULK
    }
}

