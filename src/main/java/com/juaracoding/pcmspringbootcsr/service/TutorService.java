package com.juaracoding.pcmspringbootcsr.service;/*
Created by IntelliJ IDEA 2022.2.3 (Community Edition)
Build #IC-232.10072.27, built on October 11, 2023
@Author LENOVO a.k.a. Virdha Haniva Pratiwie Ishak
Java Developer
Created on 04/01/2024 6:44
@Last Modified 04/01/2024 6:44
Version 1.0
*/
/*
    Modul Code = 12
 */
import com.juaracoding.pcmspringbootcsr.configuration.OtherConfig;
import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageTutor;
import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageGlobal;
import com.juaracoding.pcmspringbootcsr.core.IService;
import com.juaracoding.pcmspringbootcsr.core.security.ModulAuthority;
import com.juaracoding.pcmspringbootcsr.dto.SearchParamDTO;
import com.juaracoding.pcmspringbootcsr.dto.tutor.TutorDTO;
import com.juaracoding.pcmspringbootcsr.handler.ResponseHandler;
import com.juaracoding.pcmspringbootcsr.model.Tutor;
import com.juaracoding.pcmspringbootcsr.repo.TutorRepo;
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
public class TutorService implements IService<Tutor> {
    private TutorRepo tutorRepo;
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
    private String authorizationCode = "18";//ini disinkron kan dengan id di table MstMenu

    @Autowired
    public TutorService(TutorRepo tutorRepo) {
        strExceptionArr[0]="TutorService";
        mapColumn();
        this.tutorRepo = tutorRepo;
    }

    private void mapColumn()
    {

        //FE CSR
//        listSearchParamDTO.add(new SearchParamDTO("id","ID TUTOR "));
        listSearchParamDTO.add(new SearchParamDTO("nama","NAMA TUTOR"));
        listSearchParamDTO.add(new SearchParamDTO("alamat","ALAMAT"));
        listSearchParamDTO.add(new SearchParamDTO("nohp","NO HP"));
        listSearchParamDTO.add(new SearchParamDTO("email","EMAIL"));
    }

    @Override
    public ResponseEntity<Object> save(Tutor tutor, HttpServletRequest request) {
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
        if(!(Boolean)mapToken.get("isValid")){
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
                    HttpStatus.FORBIDDEN,
                    null,
                    "X-AUTH-001", request);
        }

        if(tutor==null)
        {
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_DATA_INVALID,
                    HttpStatus.BAD_REQUEST,
                    null,
                    "FE12001", request);
        }
        try {

            tutor.setCreatedBy(Integer.parseInt(mapToken.get("uid").toString()));
            tutor.setCreatedDate(new Date());
            tutorRepo.save(tutor);
        } catch (Exception e) {
            strExceptionArr[1] = "save(Tutor tutor, WebRequest request) --- LINE 70";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    null,
                    "FE12001", request);
        }
        return new ResponseHandler().generateResponse(ConstantMessageGlobal.SUCCESS_SAVE,
                HttpStatus.CREATED,
                null,
                null, request);
    }

    @Override
    public ResponseEntity<Object> saveBatch(List<Tutor> lt, HttpServletRequest request) {
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
                    "FE12011", request);
        }
        Integer intUserId = Integer.parseInt(mapToken.get("uid").toString());
        for(int i=0;i<lt.size();i++)
        {
            lt.get(i).setCreatedBy(intUserId);//input userid di masing2 data yang akan di save
        }

        try {
            tutorRepo.saveAll(lt);
        } catch (Exception e) {
            strExceptionArr[1] = "saveBatch(List<Tutor> lt, HttpServletRequest request) --- LINE 68";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    null,
                    "FE12001", request);
        }
        return new ResponseHandler().generateResponse(ConstantMessageGlobal.SUCCESS_SAVE,
                HttpStatus.CREATED,
                null,
                null, request);
    }

    @Override
    public ResponseEntity<Object> edit(Long id, Tutor tutor, HttpServletRequest request) {
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
        if(!(Boolean)mapToken.get("isValid")){
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
                    HttpStatus.FORBIDDEN,
                    null,
                    "X-AUTH-001", request);
        }

        try {
            Optional<Tutor> optionalTutor = tutorRepo.findById(id);
            if(optionalTutor.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageTutor.WARNING_TUTOR_NOT_EXISTS,
                        HttpStatus.NOT_ACCEPTABLE,
                        null,
                        "FV12021",request);
            }
            Tutor nextTutor = optionalTutor.get();
            nextTutor.setNamaTutor(tutor.getNamaTutor());
            nextTutor.setAlamat(tutor.getAlamat());
            nextTutor.setNoHp(tutor.getNoHp());
            nextTutor.setEmail(tutor.getEmail());
            mapToken = modulAuthority.checkAuthorization(request);//ambil userid dari token
            nextTutor.setModifiedBy(Integer.parseInt(mapToken.get("uid").toString()));
            nextTutor.setModifiedDate(new Date());

        } catch (Exception e) {
            strExceptionArr[1] = " edit(Long id, Tutor tutor, HttpServletRequest request) --- LINE 140";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    null,
                    "FE12021", request);
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
        Optional<Tutor> opTutor = null;
        Tutor nextTutor = null;
        try {
            opTutor = tutorRepo.findById(id);

            if(opTutor.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageGlobal.WARNING_DATA_EMPTY,
                        HttpStatus.NOT_ACCEPTABLE,
                        null,
                        "FV12031",request);
            }
            nextTutor = opTutor.get();
            nextTutor.setActive(false);
            mapToken = modulAuthority.checkAuthorization(request);//ambil userid dari token
            nextTutor.setModifiedBy(Integer.parseInt(mapToken.get("uid").toString()));
            nextTutor.setModifiedDate(new Date());
        } catch (Exception e) {
            strExceptionArr[1] = "delete(Long id, HttpServletRequest request) --- LINE 344";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    null,
                    "FE12031", request);
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
        Optional<Tutor> opTutor = null;
        try{
            opTutor = tutorRepo.findByIsActiveAndIdTutor(true,id);
            if(opTutor.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageTutor.WARNING_TUTOR_NOT_EXISTS,
                        HttpStatus.NOT_ACCEPTABLE,
                        null,
                        "FV12041",request);
            }
        }catch (Exception e)
        {
            strExceptionArr[1] = "findById(Long id, HttpServletRequest request) --- LINE 344";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    null,
                    "FE12041", request);
        }
        Tutor tutor = opTutor.get();
        TutorDTO TutorDTO = modelMapper.map(tutor, new TypeToken<TutorDTO>() {}.getType());
        return new ResponseHandler().
                generateResponse(ConstantMessageGlobal.SUCCESS_FIND_BY,
                        HttpStatus.OK,
                        TutorDTO,
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
        Page<Tutor> pageTutor = null;
        List<Tutor> listTutor = null;
        List<TutorDTO> listTutorDTO = null;
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
                                        "FE12051",
                                        request);
                    }
                }
            }
            /*
                PENGISIAN DATA dan VALIDASI UNTUK PAGING ADA DI METHOD getDataByValue
             */
            pageTutor = getDataByValue(pageable,columFirst,valueFirst);
            listTutor = pageTutor.getContent();
            if(listTutor.size()==0)
            {
                return new ResponseHandler().
                        generateResponse(ConstantMessageGlobal.WARNING_DATA_EMPTY,
                                HttpStatus.NOT_FOUND,
                                null,
                                "FV12052",
                                request);
            }
            listTutorDTO = modelMapper.map(listTutor, new TypeToken<List<TutorDTO>>() {}.getType());
            mapResult = transformToDTO.transformObject(objectMapper,listTutorDTO,pageTutor);
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
                    "FE12051", request);
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

    private Page<Tutor> getDataByValue(Pageable pageable, String columnFirst, String valueFirst)
    {
        if(valueFirst.equals("") || valueFirst==null)
        {
            return tutorRepo.findByIsActive(pageable,true);
        }
        if(columnFirst.equals("id"))
        {
            return tutorRepo.findByIsActiveAndIdTutor(pageable,true,Long.parseLong(valueFirst));
        } else if (columnFirst.equals("nama")) {
            return tutorRepo.findByIsActiveAndNamaTutorContainsIgnoreCase(pageable,true,valueFirst);
        } else if (columnFirst.equals("alamat")) {
            return tutorRepo.findByIsActiveAndAlamatContainsIgnoreCase(pageable,true,valueFirst);
        } else if (columnFirst.equals("nohp")) {
            return tutorRepo.findByIsActiveAndNoHpContainsIgnoreCase(pageable, true, valueFirst);
        } else if (columnFirst.equals("email")) {
            return tutorRepo.findByIsActiveAndEmailContainsIgnoreCase(pageable, true, valueFirst);
        }
        return tutorRepo.findByIsActive(pageable,true);// ini default kalau parameter search nya tidak sesuai--- asumsi nya di hit bukan dari web
    }
}

