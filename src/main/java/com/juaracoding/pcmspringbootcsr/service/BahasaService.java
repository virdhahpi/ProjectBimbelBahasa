package com.juaracoding.pcmspringbootcsr.service;/*
Created by IntelliJ IDEA 2022.2.3 (Community Edition)
Build #IC-232.10072.27, built on October 11, 2023
@Author LENOVO a.k.a. Virdha Haniva Pratiwie Ishak
Java Developer
Created on 02/01/2024 13:22
@Last Modified 02/01/2024 13:22
Version 1.0
*/

/*
    Modul Code = 10
 */

import com.juaracoding.pcmspringbootcsr.configuration.OtherConfig;
import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageBahasa;
import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageGlobal;
import com.juaracoding.pcmspringbootcsr.core.IService;
import com.juaracoding.pcmspringbootcsr.core.security.ModulAuthority;
import com.juaracoding.pcmspringbootcsr.dto.SearchParamDTO;
import com.juaracoding.pcmspringbootcsr.dto.bahasa.BahasaDTO;
import com.juaracoding.pcmspringbootcsr.handler.ResponseHandler;
import com.juaracoding.pcmspringbootcsr.model.Bahasa;
import com.juaracoding.pcmspringbootcsr.repo.BahasaRepo;
import com.juaracoding.pcmspringbootcsr.util.CsvReader;
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

@Service
@Transactional
public class BahasaService implements IService<Bahasa> {
    private BahasaRepo bahasaRepo;
    private String[] strExceptionArr = new String[2];
    @Autowired
    private ModelMapper modelMapper;
    private Map<String,Object> objectMapper = new HashMap<String,Object>();
    private TransformToDTO transformToDTO = new TransformToDTO();
    //    private Map<String,String> mapColumnSearch = new HashMap<String,String>();// UNTUK MEMBENTUK LIST DATA KOMPONEN SEARCH DI CLIENT
    private Map<String,Object> mapToken = new HashMap<String,Object>();
    @Autowired
    private ModulAuthority modulAuthority;
    private List<SearchParamDTO> listSearchParamDTO  = new ArrayList<>();
    @Autowired
    private CsvReader csvReader = new CsvReader();
    private String authorizationCode = "16";//ini disinkron kan dengan id di table MstMenu

    @Autowired
    public BahasaService(BahasaRepo bahasaRepo) {
        strExceptionArr[0]="BahasaService";
        mapColumn();
        this.bahasaRepo = bahasaRepo;
    }

    private void mapColumn()
    {

        //FE CSR
//        listSearchParamDTO.add(new SearchParamDTO("id","ID BAHASA "));
        listSearchParamDTO.add(new SearchParamDTO("nama","NAMA BAHASA"));
        listSearchParamDTO.add(new SearchParamDTO("kode","KODE BAHASA"));
    }

    @Override
    public ResponseEntity<Object> save(Bahasa bahasa, HttpServletRequest request) {
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
        if(!(Boolean)mapToken.get("isValid")){
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
                    HttpStatus.FORBIDDEN,
                    null,
                    "X-AUTH-001", request);
        }

        if(bahasa==null)
        {
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_DATA_INVALID,
                    HttpStatus.BAD_REQUEST,
                    null,
                    "FE10001", request);
        }
        try {

            bahasa.setCreatedBy(Integer.parseInt(mapToken.get("uid").toString()));
            bahasa.setCreatedDate(new Date());
            bahasaRepo.save(bahasa);
        } catch (Exception e) {
            strExceptionArr[1] = "save(Bahasa bahasa, WebRequest request) --- LINE 70";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    null,
                    "FE10001", request);
        }
        return new ResponseHandler().generateResponse(ConstantMessageGlobal.SUCCESS_SAVE,
                HttpStatus.CREATED,
                null,
                null, request);
    }

    @Override
    public ResponseEntity<Object> saveBatch(List<Bahasa> lt, HttpServletRequest request) {
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
        if(!(Boolean)mapToken.get("isValid")){
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
                    HttpStatus.FORBIDDEN,
                    null,
                    "X-AUTH-001", request);
        }

        if(lt.size()==0 || lt==null)
        {
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_DATA_INVALID,
                    HttpStatus.BAD_REQUEST,
                    null,
                    "FE10011", request);
        }
        Integer intUserId = Integer.parseInt(mapToken.get("uid").toString());
        for(int i=0;i<lt.size();i++)
        {
            lt.get(i).setCreatedBy(intUserId);//input userid di masing2 data yang akan di save
        }

        try {
            bahasaRepo.saveAll(lt);
        } catch (Exception e) {
            strExceptionArr[1] = "saveBatch(List<Bahasa> lt, HttpServletRequest request) --- LINE 68";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    null,
                    "FE10001", request);
        }
        return new ResponseHandler().generateResponse(ConstantMessageGlobal.SUCCESS_SAVE,
                HttpStatus.CREATED,
                null,
                null, request);
    }

    @Override
    public ResponseEntity<Object> edit(Long id, Bahasa bahasa, HttpServletRequest request) {
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
        if(!(Boolean)mapToken.get("isValid")){
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
                    HttpStatus.FORBIDDEN,
                    null,
                    "X-AUTH-001", request);
        }

        try {
            Optional<Bahasa> optionalBahasa = bahasaRepo.findById(id);
            if(optionalBahasa.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageBahasa.WARNING_BAHASA_NOT_EXISTS,
                        HttpStatus.NOT_ACCEPTABLE,
                        null,
                        "FV10021",request);
            }
            Bahasa nextBahasa = optionalBahasa.get();
            nextBahasa.setNamaBahasa(bahasa.getNamaBahasa());
            nextBahasa.setKodeBahasa(bahasa.getKodeBahasa());
            mapToken = modulAuthority.checkAuthorization(request);//ambil userid dari token
            nextBahasa.setModifiedBy(Integer.parseInt(mapToken.get("uid").toString()));
            nextBahasa.setModifiedDate(new Date());

        } catch (Exception e) {
            strExceptionArr[1] = " edit(Long id, Bahasa Bahasa, HttpServletRequest request) --- LINE 140";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    null,
                    "FE10021", request);
        }
        return new ResponseHandler().generateResponse(ConstantMessageGlobal.SUCCESS_UPDATE,
                HttpStatus.CREATED,
                null,
                null, request);
    }

    @Override
    public ResponseEntity<Object> delete(Long id, HttpServletRequest request) {
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
        if(!(Boolean)mapToken.get("isValid")){
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
                    HttpStatus.FORBIDDEN,
                    null,
                    "X-AUTH-001", request);
        }
        Optional<Bahasa> opBahasa = null;
        Bahasa nextBahasa = null;
        try {
            opBahasa = bahasaRepo.findById(id);

            if(opBahasa.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageGlobal.WARNING_DATA_EMPTY,
                        HttpStatus.NOT_ACCEPTABLE,
                        null,
                        "FV10031",request);
            }
            nextBahasa = opBahasa.get();
            nextBahasa.setActive(false);
            mapToken = modulAuthority.checkAuthorization(request);//ambil userid dari token
            nextBahasa.setModifiedBy(Integer.parseInt(mapToken.get("uid").toString()));
            nextBahasa.setModifiedDate(new Date());
        } catch (Exception e) {
            strExceptionArr[1] = "delete(Long id, HttpServletRequest request) --- LINE 344";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    null,
                    "FE10031", request);
        }
        return new ResponseHandler().generateResponse(ConstantMessageGlobal.SUCCESS_DELETE,
                HttpStatus.OK,
                null,
                null, request);
    }

    @Override
    public ResponseEntity<Object> findById(Long id, HttpServletRequest request)
    {
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
        if(!(Boolean)mapToken.get("isValid")){
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
                    HttpStatus.FORBIDDEN,
                    null,
                    "X-AUTH-001", request);
        }
        Optional<Bahasa> opBahasa = null;
        try{
            opBahasa = bahasaRepo.findByIsActiveAndIdBahasa(true,id);
            if(opBahasa.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageBahasa.WARNING_BAHASA_NOT_EXISTS,
                        HttpStatus.NOT_ACCEPTABLE,
                        null,
                        "FV10041",request);
            }
        }catch (Exception e)
        {
            strExceptionArr[1] = "findById(Long id, HttpServletRequest request) --- LINE 344";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    null,
                    "FE10041", request);
        }
        Bahasa bahasa = opBahasa.get();
        BahasaDTO BahasaDTO = modelMapper.map(bahasa, new TypeToken<BahasaDTO>() {}.getType());
        return new ResponseHandler().
                generateResponse(ConstantMessageGlobal.SUCCESS_FIND_BY,
                        HttpStatus.OK,
                        BahasaDTO,
                        null,
                        request);
    }

    @Override
    public ResponseEntity<Object> find(Pageable pageable, String columFirst, String valueFirst, HttpServletRequest request) {
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
//        if(!(Boolean)mapToken.get("isValid")){
//            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
//                    HttpStatus.FORBIDDEN,
//                    null,
//                    "X-AUTH-001", request);
//        }
        Page<Bahasa> pageBahasa = null;
        List<Bahasa> listBahasa = null;
        List<BahasaDTO> listBahasaDTO = null;
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
                        strExceptionArr[1] = "find(Pageable pageable, String columFirst, String valueFirst, HttpServletRequest request) --- LINE 202";
                        LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
                        return new ResponseHandler().
                                generateResponse(ConstantMessageGlobal.WARNING_DATA_EMPTY,
                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                        null,//HANDLE NILAI PENCARIAN
                                        "FE10051",
                                        request);
                    }
                }
            }
            /*
                PENGISIAN DATA dan VALIDASI UNTUK PAGING ADA DI METHOD getDataByValue
             */
            pageBahasa = getDataByValue(pageable,columFirst,valueFirst);
            listBahasa = pageBahasa.getContent();
            if(listBahasa.size()==0)
            {
                return new ResponseHandler().
                        generateResponse(ConstantMessageGlobal.WARNING_DATA_EMPTY,
                                HttpStatus.NOT_FOUND,
                                null,
                                "FV10052",
                                request);
            }
            listBahasaDTO = modelMapper.map(listBahasa, new TypeToken<List<BahasaDTO>>() {}.getType());
            mapResult = transformToDTO.transformObject(objectMapper,listBahasaDTO,pageBahasa);
            mapResult.put("searchParam",listSearchParamDTO);
            mapResult.put("columnFirst",columFirst);
            mapResult.put("valueFirst",valueFirst);
        }
        catch (Exception e)
        {
            strExceptionArr[1] = "find(Pageable pageable, String columFirst, String valueFirst, HttpServletRequest request) --- LINE 272";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_DATA_INVALID,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null,
                    "FE10051", request);
        }
        return new ResponseHandler().
                generateResponse(ConstantMessageGlobal.SUCCESS_FIND_BY,
                        HttpStatus.OK,
                        mapResult,
                        null,
                        request);
    }

    @Override
    public ResponseEntity<Object> dataToExport(MultipartFile multipartFile, HttpServletRequest request) {
        return null;
    }

    private Page<Bahasa> getDataByValue(Pageable pageable, String columnFirst, String valueFirst)
    {
        if(valueFirst.equals("") || valueFirst==null)
        {
            return bahasaRepo.findByIsActive(pageable,true);
        }
        if(columnFirst.equals("id"))
        {
            return bahasaRepo.findByIsActiveAndIdBahasa(pageable,true,Long.parseLong(valueFirst));
        } else if (columnFirst.equals("nama")) {
            return bahasaRepo.findByIsActiveAndNamaBahasaContainsIgnoreCase(pageable,true,valueFirst);
        } else if (columnFirst.equals("kode")) {
            return bahasaRepo.findByIsActiveAndKodeBahasaContainsIgnoreCase(pageable,true,valueFirst);
        }

        return bahasaRepo.findByIsActive(pageable,true);// ini default kalau parameter search nya tidak sesuai--- asumsi nya di hit bukan dari web
    }
}

