package com.juaracoding.pcmspringbootcsr.controller;/*
Created by IntelliJ IDEA 2022.2.3 (Community Edition)
Build #IC-232.10072.27, built on October 11, 2023
@Author LENOVO a.k.a. Virdha Haniva Pratiwie Ishak
Java Developer
Created on 04/01/2024 7:11
@Last Modified 04/01/2024 7:11
Version 1.0
*/

import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageGlobal;
import com.juaracoding.pcmspringbootcsr.dto.tutor.TutorDTO;
import com.juaracoding.pcmspringbootcsr.handler.ResponseHandler;
import com.juaracoding.pcmspringbootcsr.model.Tutor;
import com.juaracoding.pcmspringbootcsr.service.TutorService;
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
public class TutorController {
    private TutorService tutorService;
    @Autowired
    private ModelMapper modelMapper;
    private Map<String,Object> objectMapper = new HashMap<String,Object>();
    private Map<String,String> mapSorting = new HashMap<String,String>();
    private List<Tutor> lsCPUpload = new ArrayList<Tutor>();
    private String [] strExceptionArr = new String[2];

    @Autowired
    public TutorController(TutorService tutorService) {
        strExceptionArr[0] = "TutorController";
        mapSorting();
        this.tutorService = tutorService;
    }

    private void mapSorting()
    {
        mapSorting.put("id","idTutor");
        mapSorting.put("nama","namaTutor");
        mapSorting.put("alamat","alamat");
        mapSorting.put("nohp","noHp");
        mapSorting.put("email","email");
    }

    @PostMapping("/tutor/v1")
    public ResponseEntity<Object> save(@Valid @RequestBody TutorDTO tutorDTO
            , HttpServletRequest request
    )
    {
        Tutor tutor = modelMapper.map(tutorDTO, new TypeToken<Tutor>() {}.getType());
        return tutorService.save(tutor,request);
    }
    @PostMapping("/tutor/v1/batch")
    public ResponseEntity<Object> saveBatch(@RequestBody List<TutorDTO>  listTutorDTO
            , HttpServletRequest request
    )
    {
        List<Tutor> listTutor = modelMapper.map(listTutorDTO, new TypeToken<List<Tutor>>() {}.getType());
        return tutorService.saveBatch(listTutor,request);
    }

    @PutMapping("/tutor/v1/{id}")
    public ResponseEntity<Object> edit(@Valid @RequestBody TutorDTO tutorDTO,
                                       @PathVariable("id") Long id,
                                       HttpServletRequest request
    )
    {
        Tutor tutor = modelMapper.map(tutorDTO, new TypeToken<Tutor>() {}.getType());
        return tutorService.edit(id,tutor,request);
    }

    @GetMapping("/tutor/v1/{id}")
    public ResponseEntity<Object> findById(HttpServletRequest request, @PathVariable("id") Long id)
    {
        return tutorService.findById(id,request);
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
    @GetMapping("/tutor/v1")
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
            sortzBy = mapSorting.get(sortzBy);// id = idTutor, nama = namaTutor dst....
            Pageable pageable = PageRequest.of(pagez,Integer.parseInt(sizeComponent.equals("")?"10":sizeComponent),
                    sortz.equals("desc")? Sort.by(sortzBy).descending():Sort.by(sortzBy));
            return tutorService.find(pageable,columnFirst,valueFirst,request);
        }
        /*
            jika tidak ada data maka ada notifikasi tidak tersedia di response nya
         */
        return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_NOT_AVAILABLE,
                HttpStatus.BAD_REQUEST,
                null,
                "Not Available", request);
    }

    @DeleteMapping("/tutor/v1/{id}")
    public ResponseEntity<Object> delete(HttpServletRequest request, @PathVariable("id") Long id)
    {
        return tutorService.delete(id,request);
    }

    @PostMapping("/tutor/v1/uploaddata")
    public ResponseEntity<Object> export(@RequestParam("datatutor")
                                         @RequestHeader MultipartFile file,
                                         HttpServletRequest request)
    {
        return tutorService.dataToExport(file,request);//untuk data BULK
    }
}

