package com.juaracoding.pcmspringbootcsr.controller;/*
Created by IntelliJ IDEA 2022.2.3 (Community Edition)
Build #IC-232.10072.27, built on October 11, 2023
@Author LENOVO a.k.a. Virdha Haniva Pratiwie Ishak
Java Developer
Created on 04/01/2024 8:47
@Last Modified 04/01/2024 8:47
Version 1.0
*/

import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageGlobal;
import com.juaracoding.pcmspringbootcsr.core.security.ModulAuthority;
import com.juaracoding.pcmspringbootcsr.dto.matapelajaran.MataPelajaranDTO;
import com.juaracoding.pcmspringbootcsr.handler.ResponseHandler;
import com.juaracoding.pcmspringbootcsr.model.MataPelajaran;
import com.juaracoding.pcmspringbootcsr.service.MataPelajaranService;
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
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/program")
public class MataPelajaranController {

    private MataPelajaranService mataPelajaranService;
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
    public MataPelajaranController(MataPelajaranService mataPelajaranService) {
        strExceptionArr [0] = "MataPelajaranController";
        mapSorting();
        this.mataPelajaranService = mataPelajaranService;
    }
    private void mapSorting()
    {
        mapSorting.put("id","idMataPelajaran");
        mapSorting.put("nama","namaMataPelajaran");
        mapSorting.put("kode","kodeMataPelajaran");
        mapSorting.put("kodeKelas","kodeKelas");
        mapSorting.put("bahasa","namaBahasa");
        mapSorting.put("tutor","namaTutor");
    }

    @PostMapping("/mapel/v1")
    public ResponseEntity<Object> save(@Valid @RequestBody MataPelajaranDTO mataPelajaranDTO
            , HttpServletRequest request
    )
    {
        MataPelajaran mataPelajaran = modelMapper.map(mataPelajaranDTO, new TypeToken<MataPelajaran>() {}.getType());
        return mataPelajaranService.save(mataPelajaran,request);
    }
    @PostMapping("/mapel/v1/batch")
    public ResponseEntity<Object> saveBatch(@RequestBody List<MataPelajaranDTO> listMataPelajaranDTO
            , HttpServletRequest request
    )
    {
        List<MataPelajaran> listMataPelajaran = modelMapper.map(listMataPelajaranDTO, new TypeToken<List<MataPelajaran>>() {}.getType());
        return mataPelajaranService.saveBatch(listMataPelajaran,request);
    }

    @PutMapping("/mapel/v1/{id}")
    public ResponseEntity<Object> edit(@Valid @RequestBody MataPelajaranDTO mataPelajaranDTO,
                                       @PathVariable("id") Long id,
                                       HttpServletRequest request
    )
    {
        MataPelajaran mataPelajaran = modelMapper.map(mataPelajaranDTO, new TypeToken<MataPelajaran>() {}.getType());
        return mataPelajaranService.edit(id,mataPelajaran,request);
    }

    @GetMapping("/mapel/v1/{id}")
    public ResponseEntity<Object> findById(HttpServletRequest request, @PathVariable("id") Long id)
    {
        return mataPelajaranService.findById(id,request);
    }

    @GetMapping("/mapel/v1")
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
        sortzBy = (sortzBy==null || sortzBy.equals(""))?"id":sortzBy;//penanda kalau null dari FE itu berarti kayak buka MataPelajaran baru
        sortz = (sortz.equals("") || sortz==null)? "asc" : sortzBy;

        if(!(pagez==null && sizeComponent==null)){
            sortzBy = mapSorting.get(sortzBy);// id = idMataPelajaran, nama = namaMataPelajaran dst....
            Pageable pageable = PageRequest.of(pagez,Integer.parseInt(sizeComponent.equals("")?"10":sizeComponent),
                    sortz.equals("desc")? Sort.by(sortzBy).descending():Sort.by(sortzBy));
            return mataPelajaranService.find(pageable,columnFirst,valueFirst,request);
        }

        return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_NOT_AVAILABLE,
                HttpStatus.BAD_REQUEST,
                null,
                "Not Available", request);
    }


    @DeleteMapping("/mapel/v1/{id}")
    public ResponseEntity<Object> delete(HttpServletRequest request, @PathVariable("id") Long id)
    {
        return mataPelajaranService.delete(id,request);
    }

    /*
        upload data tidak ada di modul ini
     */
}

