package com.juaracoding.pcmspringbootcsr.service;/*
Created by IntelliJ IDEA 2022.2.3 (Community Edition)
Build #IC-232.10072.27, built on October 11, 2023
@Author LENOVO a.k.a. Virdha Haniva Pratiwie Ishak
Java Developer
Created on 04/01/2024 11:09
@Last Modified 04/01/2024 11:09
Version 1.0
*/

import com.juaracoding.pcmspringbootcsr.configuration.OtherConfig;
import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageSiswa;
import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageGlobal;
import com.juaracoding.pcmspringbootcsr.core.IService;
import com.juaracoding.pcmspringbootcsr.core.security.ModulAuthority;
import com.juaracoding.pcmspringbootcsr.dto.SearchParamDTO;
import com.juaracoding.pcmspringbootcsr.dto.matapelajaran.MataPelajaranOptionDTO;
import com.juaracoding.pcmspringbootcsr.dto.siswa.SiswaDTO;
import com.juaracoding.pcmspringbootcsr.handler.ResponseHandler;
import com.juaracoding.pcmspringbootcsr.model.Siswa;
import com.juaracoding.pcmspringbootcsr.repo.*;
import com.juaracoding.pcmspringbootcsr.util.LoggingFile;
import com.juaracoding.pcmspringbootcsr.util.TransformToDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;

/*
    Modul Code = 13
 */
@Service
@Transactional
public class SiswaService implements IService<Siswa> {
    private SiswaRepo siswaRepo;
    @Autowired
    private MataPelajaranRepo mataPelajaranRepo;
    private String[] strExceptionArr = new String[2];
    @Autowired
    private ModelMapper modelMapper;
    private Map<String,Object> objectMapper = new HashMap<String,Object>();
    private TransformToDTO transformToDTO = new TransformToDTO();
    private List<SearchParamDTO> listSearchParamDTO  = new ArrayList<>();
    private Map<String,Object> mapToken = new HashMap<String,Object>();
    private List<MataPelajaranOptionDTO> ltMataPelajaranOptDTO = null;
    private String authorizationCode = "20";

    @Autowired
    private ModulAuthority modulAuthority;

    private Map<String,Object> mapComponent = new HashMap<String,Object>();

    @Autowired
    public SiswaService(SiswaRepo siswaRepo) {
        strExceptionArr[0]="SiswaService";
        mapColumn();
        this.siswaRepo = siswaRepo;
    }

    private void mapColumn()
    {
        listSearchParamDTO.add(new SearchParamDTO("id","ID"));
        listSearchParamDTO.add(new SearchParamDTO("nama","NAMA SISWA"));
        listSearchParamDTO.add(new SearchParamDTO("alamat","ALAMAT"));
        listSearchParamDTO.add(new SearchParamDTO("nohp","NO HP"));
        listSearchParamDTO.add(new SearchParamDTO("email","EMAIL"));

    }

    @Override
    public ResponseEntity<Object> save(Siswa siswa, HttpServletRequest request) {
        getMasterComponent();//perubahan 21-12-2023
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
        if(!(Boolean)mapToken.get("isValid")){
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
                    HttpStatus.FORBIDDEN,
                    null,
                    "X-AUTH-001", request);
        }
        if(siswa==null)
        {
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_DATA_INVALID,
                    HttpStatus.BAD_REQUEST,
                    mapComponent,//perubahan 21-12-2023
                    "FV13001", request);//FAILED VALIDATION
        }
        try {

            mapToken = modulAuthority.checkAuthorization(request);//ambil userid dari token
            siswa.setCreatedBy(Integer.parseInt(mapToken.get("uid").toString()));
            siswa.setCreatedDate(new Date());
            siswaRepo.save(siswa);
        } catch (Exception e) {
            strExceptionArr[1] = "save(Siswa siswa, HttpServletRequest request) --- LINE 82";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    mapComponent,//perubahan 21-12-2023
                    "FE13001", request);//FAILED ERROR
        }
        return new ResponseHandler().generateResponse(ConstantMessageGlobal.SUCCESS_SAVE,
                HttpStatus.CREATED,
                mapComponent,
                null, request);
    }

    @Override
    public ResponseEntity<Object> saveBatch(List<Siswa> lt, HttpServletRequest request) {
        getMasterComponent();
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
        if(!(Boolean)mapToken.get("isValid")){
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
                    HttpStatus.FORBIDDEN,
                    null,
                    "X-AUTH-001", request);
        }
        if(lt.size()==0 || lt==null)
        {
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.WARNING_DATA_EMPTY,
                    HttpStatus.BAD_REQUEST,
                    mapComponent,//perubahan 21-12-2023
                    "FV13011", request);
        }
        mapToken = modulAuthority.checkAuthorization(request);//ambil userid dari token
        Integer intUserId = Integer.parseInt(mapToken.get("uid").toString());
        for(int i=0;i<lt.size();i++)
        {
            lt.get(i).setCreatedBy(intUserId);//input userid di masing2 data yang akan di save
            lt.get(i).setCreatedDate(new Date());//input userid di masing2 data yang akan di save
        }
        try {
            siswaRepo.saveAll(lt);
        } catch (Exception e) {
            strExceptionArr[1] = "saveBatch(List<Siswa> lt, HttpServletRequest request) --- LINE 115";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    mapComponent,//perubahan 21-12-2023
                    "FE13011", request);
        }
        return new ResponseHandler().generateResponse(ConstantMessageGlobal.SUCCESS_SAVE,
                HttpStatus.CREATED,
                mapComponent,//perubahan 21-12-2023
                null, request);
    }

    @Override
    public ResponseEntity<Object> edit(Long id, Siswa siswa, HttpServletRequest request) {
        getMasterComponent();
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
        if(!(Boolean)mapToken.get("isValid")){
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
                    HttpStatus.FORBIDDEN,
                    null,
                    "X-AUTH-001", request);
        }
        try {
            Optional<Siswa> optionalSiswa = siswaRepo.findByIsActiveAndIdSiswa(true,id);
            if(optionalSiswa.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageSiswa.WARNING_SISWA_NOT_EXISTS,
                        HttpStatus.NOT_ACCEPTABLE,
                        mapComponent,//perubahan 21-12-2023
                        "FV13021",request);
            }
            Siswa nextSiswa = optionalSiswa.get();
            nextSiswa.setNamaSiswa(siswa.getNamaSiswa());
            nextSiswa.setAlamat(siswa.getAlamat());
            nextSiswa.setNoHp(siswa.getNoHp());
            nextSiswa.setEmail(siswa.getEmail());
            nextSiswa.setListMataPelajaran(siswa.getListMataPelajaran());
            mapToken = modulAuthority.checkAuthorization(request);//ambil userid dari token
            nextSiswa.setModifiedBy(Integer.parseInt(mapToken.get("uid").toString()));
            nextSiswa.setModifiedDate(new Date());

        } catch (Exception e) {
            strExceptionArr[1] = " edit(Long id, Siswa siswa, HttpServletRequest request) --- LINE 141";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    mapComponent,//perubahan 21-12-2023
                    "FE13021", request);
        }
        return new ResponseHandler().generateResponse(ConstantMessageGlobal.SUCCESS_UPDATE,
                HttpStatus.CREATED,
                mapComponent,//perubahan 21-12-2023
                null, request);
    }

    @Override
    public ResponseEntity<Object> delete(Long id, HttpServletRequest request) {
        getMasterComponent();
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
        if(!(Boolean)mapToken.get("isValid")){
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
                    HttpStatus.FORBIDDEN,
                    null,
                    "X-AUTH-001", request);
        }
        Optional<Siswa> optionalSiswa = null;
        Siswa nextSiswa = null;
        try {
            optionalSiswa = siswaRepo.findByIsActiveAndIdSiswa(true,id);
            if(optionalSiswa.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageGlobal.WARNING_DATA_EMPTY,
                        HttpStatus.NOT_ACCEPTABLE,
                        mapComponent,//perubahan 21-12-2023
                        "FV13031",request);
            }
            nextSiswa = optionalSiswa.get();
            nextSiswa.setActive(false);
            mapToken = modulAuthority.checkAuthorization(request);//ambil userid dari token
            nextSiswa.setModifiedBy(Integer.parseInt(mapToken.get("uid").toString()));
            nextSiswa.setModifiedDate(new Date());

        } catch (Exception e) {
            strExceptionArr[1] = " delete(Long id, HttpServletRequest request)  --- LINE 174";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    mapComponent,//perubahan 21-12-2023
                    "FE13031", request);
        }
        return new ResponseHandler().generateResponse(ConstantMessageGlobal.SUCCESS_DELETE,
                HttpStatus.OK,
                mapComponent,
                null, request);
    }

    @Override
    public ResponseEntity<Object> findById(Long id, HttpServletRequest request)
    {
        getMasterComponent();
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
        if(!(Boolean)mapToken.get("isValid")){
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
                    HttpStatus.FORBIDDEN,
                    null,
                    "X-AUTH-001", request);
        }
        Optional<Siswa> optionalSiswa = null;
        try{
            optionalSiswa = siswaRepo.findByIsActiveAndIdSiswa(true,id);
            if(optionalSiswa.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageSiswa.WARNING_SISWA_NOT_EXISTS,
                        HttpStatus.NOT_ACCEPTABLE,
                        mapComponent,//perubahan 21-12-2023
                        "FV13041",request);
            }
        }catch (Exception e)
        {
            strExceptionArr[1] = "findById(Long id, HttpServletRequest request) --- LINE 344";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    mapComponent,//perubahan 21-12-2023
                    "FE13041", request);
        }
        Siswa nextSiswa = optionalSiswa.get();
        SiswaDTO siswaDTO = modelMapper.map(nextSiswa, new TypeToken<SiswaDTO>() {}.getType());
        return new ResponseHandler().
                generateResponse(ConstantMessageGlobal.SUCCESS_FIND_BY,
                        HttpStatus.OK,
                        siswaDTO,
                        mapComponent,//perubahan 21-12-2023
                        request);
    }

    @Override
    public ResponseEntity<Object> find(Pageable pageable, String columFirst, String valueFirst, HttpServletRequest request) {
        getMasterComponent();//perubahan 21-12-2023
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
//        if(!(Boolean)mapToken.get("isValid")){
//            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
//                    HttpStatus.FORBIDDEN,
//                    null,
//                    "X-AUTH-001", request);
//        }
        Page<Siswa> pageSiswa = null;
        List<Siswa> listSiswa = null;
        List<SiswaDTO> listSiswaDTO = null;
        Map<String,Object> mapResult = null;

        try
        {
            /*
                SET DEFAULT PENCARIAN UNTUK MENCEGAH ERROR JIKA PARAMETER COLUMN TIDAK DIISI ATAU NULL
             */
            if(columFirst.equals("id"))
            {
                if(!valueFirst.equals("") && valueFirst!=null)
                {
                    try
                    {
                        /*
                            UNTUK ID YANG BER TIPE NUMERIC
                            TIDAK PERLU DIGUNAKAN JIKA ID BER TIPE STRING
                         */
                        Long.parseLong(valueFirst);
                    }
                    catch (Exception e)
                    {
                        strExceptionArr[1] = "find(Pageable pageable, String columFirst, String valueFirst, HttpServletRequest request) --- LINE 252";
                        LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
                        return new ResponseHandler().
                                generateResponse(ConstantMessageGlobal.WARNING_DATA_EMPTY,
                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                        mapComponent,//perubahan 21-12-2023
                                        "FE13051",
                                        request);
                    }
                }
            }
            /*
                PENGISIAN DATA dan VALIDASI UNTUK PAGING ADA DI METHOD getDataByValue
             */
            pageSiswa = getDataByValue(pageable,columFirst,valueFirst);
            listSiswa = pageSiswa.getContent();
            if(listSiswa.size()==0)
            {
                return new ResponseHandler().
                        generateResponse(ConstantMessageGlobal.WARNING_DATA_EMPTY,
                                HttpStatus.NOT_FOUND,
                                mapComponent,
                                "FV13052",
                                request);
            }
            listSiswaDTO = modelMapper.map(listSiswa, new TypeToken<List<SiswaDTO>>() {}.getType());
            mapResult = transformToDTO.transformObject(objectMapper,listSiswaDTO,pageSiswa,listSearchParamDTO,columFirst,valueFirst,mapComponent);
        }

        catch (Exception e)
        {
            strExceptionArr[1] = "find(Pageable pageable, String columFirst, String valueFirst, HttpServletRequest request) --- LINE 272";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_DATA_INVALID,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    mapComponent,
                    "FE13051", request);
        }
        return new ResponseHandler().
                generateResponse(ConstantMessageGlobal.SUCCESS_FIND_BY,
                        HttpStatus.OK,
                        mapResult,
                        null,
                        request);
    }

    public ResponseEntity<Object> dashBoard(HttpServletRequest request)
    {
        getMasterComponent();
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
        if(!(Boolean)mapToken.get("isValid")){
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
                    HttpStatus.FORBIDDEN,
                    null,
                    "X-AUTH-001", request);
        }
        Optional<Siswa> optionalSiswa = null;
        try{
            optionalSiswa = siswaRepo.findByIsActiveAndNoHp(true,mapToken.get("pn").toString());
            if(optionalSiswa.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageSiswa.WARNING_SISWA_NOT_EXISTS,
                        HttpStatus.NOT_ACCEPTABLE,
                        mapComponent,//perubahan 21-12-2023
                        "FV13041",request);
            }
        }catch (Exception e)
        {
            strExceptionArr[1] = "dashBoard(HttpServletRequest request) --- LINE 344";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    mapComponent,//perubahan 21-12-2023
                    "FE13041", request);
        }
        Siswa nextSiswa = optionalSiswa.get();
        SiswaDTO siswaDTO = modelMapper.map(nextSiswa, new TypeToken<SiswaDTO>() {}.getType());
        return new ResponseHandler().
                generateResponse(ConstantMessageGlobal.SUCCESS_FIND_BY,
                        HttpStatus.OK,
                        siswaDTO,
                        mapComponent,//perubahan 21-12-2023
                        request);
    }

    @Override
    public ResponseEntity<Object> dataToExport(MultipartFile multipartFile, HttpServletRequest request) {

        /*
            biarkan saja kosong
            karena mapping ini dilakukan di client
            jika user diminta untuk mengisi data di file csv maka akan kesulitan untuk merelasikan data nya
         */
        return null;
    }

    private Page<Siswa> getDataByValue(Pageable pageable, String columnFirst, String valueFirst)
    {
        if(valueFirst.equals("") || valueFirst==null)
        {
            return siswaRepo.findByIsActive(pageable,true);
        }
        if(columnFirst.equals("id"))
        {
            return siswaRepo.findByIsActiveAndIdSiswa(pageable,true,Long.parseLong(valueFirst));
        } else if (columnFirst.equals("nama")) {
            return siswaRepo.findByIsActiveAndNamaSiswaContainsIgnoreCase(pageable,true,valueFirst);
        } else if (columnFirst.equals("alamat")) {
            return siswaRepo.findByIsActiveAndAlamatContainsIgnoreCase(pageable,true,valueFirst);
        } else if (columnFirst.equals("nohp")) {
            return siswaRepo.findByIsActiveAndNoHpContainsIgnoreCase(pageable, true, valueFirst);
        } else if (columnFirst.equals("email")) {
            return siswaRepo.findByIsActiveAndEmailContainsIgnoreCase(pageable, true, valueFirst);
        }

        return siswaRepo.findByIsActive(pageable,true);// ini default kalau parameter search nya tidak sesuai--- asumsi nya di hit bukan dari web
    }

    private Map<String,Object> getMasterComponent(){
        ltMataPelajaranOptDTO =  modelMapper.map(mataPelajaranRepo.findByIsActive(true), new TypeToken<List<MataPelajaranOptionDTO>>() {}.getType());
        mapComponent.put("listMataPelajaran",ltMataPelajaranOptDTO);

        return mapComponent;
    }
}


