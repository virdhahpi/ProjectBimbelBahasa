package com.juaracoding.pcmspringbootcsr.service;/*
Created by IntelliJ IDEA 2022.2.3 (Community Edition)
Build #IC-232.10072.27, built on October 11, 2023
@Author LENOVO a.k.a. Virdha Haniva Pratiwie Ishak
Java Developer
Created on 04/01/2024 7:17
@Last Modified 04/01/2024 7:17
Version 1.0
*/

import com.juaracoding.pcmspringbootcsr.configuration.OtherConfig;
import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageGlobal;
import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageMataPelajaran;
import com.juaracoding.pcmspringbootcsr.core.IService;
import com.juaracoding.pcmspringbootcsr.core.security.ModulAuthority;
import com.juaracoding.pcmspringbootcsr.dto.SearchParamDTO;
import com.juaracoding.pcmspringbootcsr.dto.bahasa.BahasaOptionDTO;
import com.juaracoding.pcmspringbootcsr.dto.menu.MenuDTO;
import com.juaracoding.pcmspringbootcsr.dto.tutor.TutorOptionDTO;
import com.juaracoding.pcmspringbootcsr.handler.ResponseHandler;
import com.juaracoding.pcmspringbootcsr.repo.BahasaRepo;
import com.juaracoding.pcmspringbootcsr.repo.MataPelajaranRepo;
import com.juaracoding.pcmspringbootcsr.dto.matapelajaran.MataPelajaranDTO;
import com.juaracoding.pcmspringbootcsr.model.MataPelajaran;
import com.juaracoding.pcmspringbootcsr.repo.TutorRepo;
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
    Modul Code = 11
 */
@Service
@Transactional
public class MataPelajaranService implements IService<MataPelajaran> {
    private MataPelajaranRepo mataPelajaranRepo;
    private BahasaRepo bahasaRepo;
    private TutorRepo tutorRepo;
    private String[] strExceptionArr = new String[2];
    @Autowired
    private ModelMapper modelMapper;
    private Map<String,Object> objectMapper = new HashMap<String,Object>();
    private TransformToDTO transformToDTO = new TransformToDTO();
    private Map<String,Object> mapComponent = new HashMap<String,Object>();
    private Map<String,Object> mapToken = new HashMap<String,Object>();
    private List<SearchParamDTO> listSearchParamDTO  = new ArrayList<>();
    @Autowired
    private ModulAuthority modulAuthority;
    private List<BahasaOptionDTO> ltBahasaOptDTO = null;
    private List<TutorOptionDTO> ltTutorOptDTO = null;
    private String authorizationCode = "19";
    @Autowired
    public MataPelajaranService(MataPelajaranRepo mataPelajaranRepo, BahasaRepo bahasaRepo, TutorRepo tutorRepo) {
        strExceptionArr[0]="MataPelajaranService";
        mapColumn();
        this.mataPelajaranRepo = mataPelajaranRepo;
        this.bahasaRepo = bahasaRepo;
        this.tutorRepo = tutorRepo;
    }

    private void mapColumn()
    {
//        listSearchParamDTO.add(new SearchParamDTO("id","ID"));
        listSearchParamDTO.add(new SearchParamDTO("nama","MAPEL"));
        listSearchParamDTO.add(new SearchParamDTO("kode","KODE"));
        listSearchParamDTO.add(new SearchParamDTO("kodeKelas","KELAS"));
        listSearchParamDTO.add(new SearchParamDTO("bahasa","BAHASA"));
        listSearchParamDTO.add(new SearchParamDTO("tutor","TUTOR"));
    }

    @Override
    public ResponseEntity<Object> save(MataPelajaran mataPelajaran, HttpServletRequest request) {
        getMasterComponent();
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
        if(!(Boolean)mapToken.get("isValid")){
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
                    HttpStatus.FORBIDDEN,
                    null,
                    "X-AUTH-001", request);
        }
        if(mataPelajaran==null)
        {
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_DATA_INVALID,
                    HttpStatus.BAD_REQUEST,
                    mapComponent,
                    "FV03001", request);//FAILED VALIDATION
        }
        try {//FORBIDEN
            mapToken = modulAuthority.checkAuthorization(request);//ambil userid dari token
            mataPelajaran.setCreatedBy(Integer.parseInt(mapToken.get("uid").toString()));
            mataPelajaran.setCreatedDate(new Date());
            mataPelajaranRepo.save(mataPelajaran);
        } catch (Exception e) {
            strExceptionArr[1] = "save(MataPelajaran mataPelajaran, HttpServletRequest request) --- LINE 82";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    mapComponent,
                    "FE03001", request);//FAILED ERROR
        }

        return new ResponseHandler().generateResponse(ConstantMessageGlobal.SUCCESS_SAVE,
                HttpStatus.CREATED,
                mapComponent,
                null, request);
    }

    @Override
    public ResponseEntity<Object> saveBatch(List<MataPelajaran> lt, HttpServletRequest request) {
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
                    mapComponent,
                    "FV03011", request);
        }
        mapToken = modulAuthority.checkAuthorization(request);//ambil userid dari token
        Integer intUserId = Integer.parseInt(mapToken.get("uid").toString());
        for(int i=0;i<lt.size();i++)
        {
            lt.get(i).setCreatedBy(intUserId);//input userid di masing2 data yang akan di save
            lt.get(i).setCreatedDate(new Date());//input userid di masing2 data yang akan di save
        }

        try {
            mataPelajaranRepo.saveAll(lt);
        } catch (Exception e) {
            strExceptionArr[1] = "saveBatch(List<MataPelajaran> lt, HttpServletRequest request) --- LINE 115";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    mapComponent,
                    "FE03011", request);
        }
        return new ResponseHandler().generateResponse(ConstantMessageGlobal.SUCCESS_SAVE,
                HttpStatus.CREATED,
                mapComponent,
                null, request);
    }

    @Override
    public ResponseEntity<Object> edit(Long id, MataPelajaran mataPelajaran, HttpServletRequest request) {
        getMasterComponent();
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
        if(!(Boolean)mapToken.get("isValid")){
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
                    HttpStatus.FORBIDDEN,
                    null,
                    "X-AUTH-001", request);
        }
        try {
            Optional<MataPelajaran> optionalMataPelajaran = mataPelajaranRepo.findByIsActiveAndIdMataPelajaran(true,id);
            if(optionalMataPelajaran.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageMataPelajaran.WARNING_MATA_PELAJARAN_NOT_EXISTS,
                        HttpStatus.NOT_ACCEPTABLE,
                        mapComponent,
                        "FV03021",request);
            }
            MataPelajaran nextMataPelajaran = optionalMataPelajaran.get();
            nextMataPelajaran.setNamaMataPelajaran(mataPelajaran.getNamaMataPelajaran());
            nextMataPelajaran.setKodeMataPelajaran(mataPelajaran.getKodeMataPelajaran());
            nextMataPelajaran.setKodeKelas(mataPelajaran.getKodeKelas());
            nextMataPelajaran.setBahasa(mataPelajaran.getBahasa());
            nextMataPelajaran.setTutor(mataPelajaran.getTutor());

            mapToken = modulAuthority.checkAuthorization(request);//ambil userid dari token
            nextMataPelajaran.setModifiedBy(Integer.parseInt(mapToken.get("uid").toString()));
            nextMataPelajaran.setModifiedDate(new Date());

        } catch (Exception e) {
            strExceptionArr[1] = " edit(Long id, MataPelajaran mataPelajaran, HttpServletRequest request) --- LINE 149";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    mapComponent,
                    "FE03021", request);
        }
        return new ResponseHandler().generateResponse(ConstantMessageGlobal.SUCCESS_UPDATE,
                HttpStatus.CREATED,
                mapComponent,
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
        Optional<MataPelajaran> optionalMataPelajaran = null;
        MataPelajaran nextMataPelajaran = null;
        try {
            optionalMataPelajaran = mataPelajaranRepo.findByIsActiveAndIdMataPelajaran(true,id);
            if(optionalMataPelajaran.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageGlobal.WARNING_DATA_EMPTY,
                        HttpStatus.NOT_ACCEPTABLE,
                        mapComponent,
                        "FV03031",request);
            }
            nextMataPelajaran = optionalMataPelajaran.get();
            nextMataPelajaran.setActive(false);
            mapToken = modulAuthority.checkAuthorization(request);//ambil userid dari token
            nextMataPelajaran.setModifiedBy(Integer.parseInt(mapToken.get("uid").toString()));
            nextMataPelajaran.setModifiedDate(new Date());

        } catch (Exception e) {
            strExceptionArr[1] = " deleteDivisi(Long idDemo, HttpServletRequest request) --- LINE 181";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    mapComponent,
                    "FE03031", request);
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
        Optional<MataPelajaran> optionalMataPelajaran = null;
        try{
            optionalMataPelajaran = mataPelajaranRepo.findByIsActiveAndIdMataPelajaran(true,id);
            if(optionalMataPelajaran.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageMataPelajaran.WARNING_MATA_PELAJARAN_NOT_EXISTS,
                        HttpStatus.NOT_ACCEPTABLE,
                        mapComponent,
                        "FV03041",request);
            }
        }catch (Exception e)
        {
            strExceptionArr[1] = "findById(Long id, HttpServletRequest request) --- LINE 344";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    mapComponent,
                    "FE03041", request);
        }
        MataPelajaran mataPelajaran = optionalMataPelajaran.get();
        MataPelajaranDTO mataPelajaranDTO = modelMapper.map(mataPelajaran, new TypeToken<MataPelajaranDTO>() {}.getType());
        mapComponent.put("listGroupBahasa",ltBahasaOptDTO);
        mapComponent.put("listGroupTutor",ltTutorOptDTO);
        mapComponent.put("mataPelajaran",mataPelajaranDTO);
        return new ResponseHandler().
                generateResponse(ConstantMessageGlobal.SUCCESS_FIND_BY,
                        HttpStatus.OK,
                        mapComponent,
                        null,
                        request);
    }

    @Override
    public ResponseEntity<Object> find(Pageable pageable, String columFirst, String valueFirst, HttpServletRequest request) {
        getMasterComponent();
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
//        if(!(Boolean)mapToken.get("isValid")){
//            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
//                    HttpStatus.FORBIDDEN,
//                    null,
//                    "X-AUTH-001", request);
//        }

        Page<MataPelajaran> pageMataPelajaran = null;
        List<MataPelajaran> listMataPelajaran = null;
        List<MataPelajaranDTO> listMataPelajaranDTO = null;
        Map<String,Object> mapResult = null;
        try {
            /*
                SET DEFAULT PENCARIAN UNTUK MENCEGAH ERROR JIKA PARAMETER COLUMN TIDAK DIISI ATAU NULL
             */
            if (columFirst.equals("id")) {
                if (!valueFirst.equals("") && valueFirst != null) {
                    try {
                        /*
                            UNTUK ID YANG BER TIPE NUMERIC
                            TIDAK PERLU DIGUNAKAN JIKA ID BER TIPE STRING
                         */
                        Long.parseLong(valueFirst);
                    } catch (Exception e) {
                        strExceptionArr[1] = "find(Pageable pageable, String columFirst, String valueFirst, HttpServletRequest request) --- LINE 252";
                        LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
                        return new ResponseHandler().
                                generateResponse(ConstantMessageGlobal.WARNING_DATA_EMPTY,
                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                        mapComponent,
                                        "FE03051",
                                        request);
                    }
                }
            }
            /*
                PENGISIAN DATA dan VALIDASI UNTUK PAGING ADA DI METHOD getDataByValue
             */
            pageMataPelajaran = getDataByValue(pageable, columFirst, valueFirst);
            listMataPelajaran = pageMataPelajaran.getContent();
            if (listMataPelajaran.size() == 0) {
                return new ResponseHandler().
                        generateResponse(ConstantMessageGlobal.WARNING_DATA_EMPTY,
                                HttpStatus.NOT_FOUND,
                                mapComponent,
                                "FV03052",
                                request);
            }
            listMataPelajaranDTO = modelMapper.map(listMataPelajaran, new TypeToken<List<MataPelajaranDTO>>() {
            }.getType());
            mapResult = transformToDTO.transformObject(objectMapper,listMataPelajaranDTO,pageMataPelajaran,listSearchParamDTO,columFirst,valueFirst,mapComponent);
        }
        catch (Exception e)
        {
            strExceptionArr[1] = "find(Pageable pageable, String columFirst, String valueFirst, HttpServletRequest request) --- LINE 272";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_DATA_INVALID,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    mapComponent,
                    "FE03051", request);
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

        /*
            biarkan saja kosong
            karena mapping ini dilakukan di client
            jika user diminta untuk mengisi data di file csv maka akan kesulitan untuk merelasikan data nya
         */
        return null;
    }

    private Page<MataPelajaran> getDataByValue(Pageable pageable, String columnFirst, String valueFirst)
    {
        if(valueFirst.equals("") || valueFirst==null)
        {
            return mataPelajaranRepo.findByIsActive(pageable,true);
        }
        if(columnFirst.equals("id"))
        {
            return mataPelajaranRepo.findByIsActiveAndIdMataPelajaran(pageable,true,Long.parseLong(valueFirst));
        } else if (columnFirst.equals("nama")) {
            return mataPelajaranRepo.findByIsActiveAndNamaMataPelajaranContainsIgnoreCase(pageable,true,valueFirst);
        } else if (columnFirst.equals("kode")) {
            return mataPelajaranRepo.findByIsActiveAndKodeMataPelajaranContainsIgnoreCase(pageable,true,valueFirst);
        } else if (columnFirst.equals("kodeKelas")) {
            return mataPelajaranRepo.findByIsActiveAndKodeKelasContainsIgnoreCase(pageable,true,valueFirst);
        } else if (columnFirst.equals("bahasa")) {
            return mataPelajaranRepo.findByNamaBahasa(pageable, valueFirst,true);
        } else if (columnFirst.equals("tutor")) {
            return mataPelajaranRepo.findByNamaTutor(pageable, valueFirst,true);
        }

        return mataPelajaranRepo.findByIsActive(pageable,true);// ini default kalau parameter search nya tidak sesuai--- asumsi nya di hit bukan dari web
    }

    private Map<String,Object> getMasterComponent(){
        ltBahasaOptDTO =  modelMapper.map(bahasaRepo.findByIsActive(true), new TypeToken<List<BahasaOptionDTO>>() {}.getType());
        ltTutorOptDTO =  modelMapper.map(tutorRepo.findByIsActive(true), new TypeToken<List<TutorOptionDTO>>() {}.getType());
        mapComponent.put("listBahasa",ltBahasaOptDTO);
        mapComponent.put("listTutor",ltTutorOptDTO);
        return mapComponent;
    }
}

