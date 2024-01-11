package com.juaracoding.pcmspringbootcsr.service;

import com.juaracoding.pcmspringbootcsr.configuration.OtherConfig;
import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageDivisi;
import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageGlobal;
import com.juaracoding.pcmspringbootcsr.core.IService;
import com.juaracoding.pcmspringbootcsr.core.security.ModulAuthority;
import com.juaracoding.pcmspringbootcsr.dto.SearchParamDTO;
import com.juaracoding.pcmspringbootcsr.dto.divisi.DivisiDTO;
import com.juaracoding.pcmspringbootcsr.handler.ResponseHandler;
import com.juaracoding.pcmspringbootcsr.model.Divisi;
import com.juaracoding.pcmspringbootcsr.repo.DivisiRepo;
import com.juaracoding.pcmspringbootcsr.util.CsvReader;
import com.juaracoding.pcmspringbootcsr.util.LoggingFile;
import com.juaracoding.pcmspringbootcsr.util.TransformToDTO;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/*
    Modul Code = 01
 */
@Service
@Transactional
public class DivisiService implements IService<Divisi> {
    private DivisiRepo divisiRepo;
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
    private String authorizationCode = "11";//ini disinkron kan dengan id di table MstMenu

    @Autowired
    public DivisiService(DivisiRepo divisiRepo) {
        strExceptionArr[0]="DivisiService";
        mapColumn();
        this.divisiRepo = divisiRepo;
    }

    private void mapColumn()
    {

        //FE CSR
        listSearchParamDTO.add(new SearchParamDTO("id","ID DIVISI"));
        listSearchParamDTO.add(new SearchParamDTO("nama","NAMA DIVISI"));
        listSearchParamDTO.add(new SearchParamDTO("kode","KODE DIVISI"));
        listSearchParamDTO.add(new SearchParamDTO("deskripsi","DESKRIPSI DIVISI"));
    }

    @Override
    public ResponseEntity<Object> save(Divisi divisi, HttpServletRequest request) {
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
        if(!(Boolean)mapToken.get("isValid")){
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
                    HttpStatus.FORBIDDEN,
                    null,
                    "X-AUTH-001", request);
        }

        if(divisi==null)
        {
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_DATA_INVALID,
                    HttpStatus.BAD_REQUEST,
                    null,
                    "FE01001", request);
        }
        try {

            divisi.setCreatedBy(Integer.parseInt(mapToken.get("uid").toString()));
            divisi.setCreatedDate(new Date());
            divisiRepo.save(divisi);
        } catch (Exception e) {
            strExceptionArr[1] = "save(Divisi divisi, WebRequest request) --- LINE 70";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    null,
                    "FE01001", request);
        }
        return new ResponseHandler().generateResponse(ConstantMessageGlobal.SUCCESS_SAVE,
                HttpStatus.CREATED,
                null,
                null, request);
    }

    @Override
    public ResponseEntity<Object> saveBatch(List<Divisi> lt, HttpServletRequest request) {
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
                    "FE01011", request);
        }
        Integer intUserId = Integer.parseInt(mapToken.get("uid").toString());
        for(int i=0;i<lt.size();i++)
        {
            lt.get(i).setCreatedBy(intUserId);//input userid di masing2 data yang akan di save
        }

        try {
            divisiRepo.saveAll(lt);
        } catch (Exception e) {
            strExceptionArr[1] = "saveBatch(List<Divisi> lt, HttpServletRequest request) --- LINE 68";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    null,
                    "FE01001", request);
        }
        return new ResponseHandler().generateResponse(ConstantMessageGlobal.SUCCESS_SAVE,
                HttpStatus.CREATED,
                null,
                null, request);
    }

    @Override
    public ResponseEntity<Object> edit(Long id, Divisi divisi, HttpServletRequest request) {
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
        if(!(Boolean)mapToken.get("isValid")){
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
                    HttpStatus.FORBIDDEN,
                    null,
                    "X-AUTH-001", request);
        }

        try {
            Optional<Divisi> optionalDivisi = divisiRepo.findById(id);
            if(optionalDivisi.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageDivisi.WARNING_DIVISI_NOT_EXISTS,
                        HttpStatus.NOT_ACCEPTABLE,
                        null,
                        "FV01021",request);
            }
            Divisi nextDivisi = optionalDivisi.get();
            nextDivisi.setNamaDivisi(divisi.getNamaDivisi());
            nextDivisi.setKodeDivisi(divisi.getKodeDivisi());
            nextDivisi.setDeskripsiDivisi(divisi.getDeskripsiDivisi());
            mapToken = modulAuthority.checkAuthorization(request);//ambil userid dari token
            nextDivisi.setModifiedBy(Integer.parseInt(mapToken.get("uid").toString()));
            nextDivisi.setModifiedDate(new Date());

        } catch (Exception e) {
            strExceptionArr[1] = " edit(Long idDivisi, Divisi divisi, WebRequest request) --- LINE 140";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    null,
                    "FE01021", request);
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
        Optional<Divisi> opDivisi = null;
        Divisi nextDivisi = null;
        try {
            opDivisi = divisiRepo.findById(id);

            if(opDivisi.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageGlobal.WARNING_DATA_EMPTY,
                        HttpStatus.NOT_ACCEPTABLE,
                        null,
                        "FV01031",request);
            }
            nextDivisi = opDivisi.get();
            nextDivisi.setActive(false);
            mapToken = modulAuthority.checkAuthorization(request);//ambil userid dari token
            nextDivisi.setModifiedBy(Integer.parseInt(mapToken.get("uid").toString()));
            nextDivisi.setModifiedDate(new Date());
        } catch (Exception e) {
            strExceptionArr[1] = " deleteDivisi(Long idDemo, WebRequest request) --- LINE 344";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    null,
                    "FE01031", request);
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
        Optional<Divisi> opDivisi = null;
        try{
            opDivisi = divisiRepo.findByIsActiveAndIdDivisi(true,id);
            if(opDivisi.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageDivisi.WARNING_DIVISI_NOT_EXISTS,
                        HttpStatus.NOT_ACCEPTABLE,
                        null,
                        "FV01041",request);
            }
        }catch (Exception e)
        {
            strExceptionArr[1] = "findById(Long id, HttpServletRequest request) --- LINE 344";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    null,
                    "FE01041", request);
        }
        Divisi divisi = opDivisi.get();
        DivisiDTO divisiDTO = modelMapper.map(divisi, new TypeToken<DivisiDTO>() {}.getType());
        return new ResponseHandler().
                generateResponse(ConstantMessageGlobal.SUCCESS_FIND_BY,
                        HttpStatus.OK,
                        divisiDTO,
                        null,
                        request);
    }

    @Override
    public ResponseEntity<Object> find(Pageable pageable, String columFirst, String valueFirst, HttpServletRequest request) {
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
        if(!(Boolean)mapToken.get("isValid")){
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
                    HttpStatus.FORBIDDEN,
                    null,
                    "X-AUTH-001", request);
        }
        Page<Divisi> pageDivisi = null;
        List<Divisi> listDivisi = null;
        List<DivisiDTO> listDivisiDTO = null;
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
                                        "FE01051",
                                        request);
                    }
                }
            }
            /*
                PENGISIAN DATA dan VALIDASI UNTUK PAGING ADA DI METHOD getDataByValue
             */
            pageDivisi = getDataByValue(pageable,columFirst,valueFirst);
            listDivisi = pageDivisi.getContent();
            if(listDivisi.size()==0)
            {
                return new ResponseHandler().
                        generateResponse(ConstantMessageGlobal.WARNING_DATA_EMPTY,
                                HttpStatus.NOT_FOUND,
                                null,
                                "FV01052",
                                request);
            }
            listDivisiDTO = modelMapper.map(listDivisi, new TypeToken<List<DivisiDTO>>() {}.getType());
            mapResult = transformToDTO.transformObject(objectMapper,listDivisiDTO,pageDivisi);
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
                    "FE01051", request);
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
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
        if(!(Boolean)mapToken.get("isValid")){
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
                    HttpStatus.FORBIDDEN,
                    null,
                    "X-AUTH-001", request);
        }
        try {
            /*
                PENGECEKAN APAKAH FORMAT FILE BENAR2 CSV ATAU BUKAN
                JIKA HANYA EKSTENSI FILE SAJA YANG .csv TETAPI FORMAT NYA BUKAN CSV
                MAKA AKAN MENGEMBALIKAN ERROR UNSUPPORTED MEDIA TYPE
             */
            if(!CsvReader.isCsv(multipartFile))
            {
                return new ResponseHandler().generateResponse(
                        ConstantMessageGlobal.ERROR_NOT_CSV_FILE,//message
                        HttpStatus.UNSUPPORTED_MEDIA_TYPE,//httpstatus
                        null,//object
                        "FV01061",//errorCode Fail Validation modul-code 001 sequence 001 range 081 - 090
                        request
                );
            }
            /*
                Extract isi / data di file csv nya
             */
            List<Divisi> listDivisi = csvReader.csvToDivisiData(multipartFile.getInputStream());
            if (listDivisi.isEmpty()) {
                strExceptionArr[1] = "dataToExport(List<Divisi> lt,MultipartFile multipartFile, HttpServletRequest request) --- LINE 296";
                LoggingFile.exceptionStringz(strExceptionArr, new FileUploadException("FILE KOSONG"), OtherConfig.getFlagLoging());
                return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_EMPTY_FILE + " -- " + multipartFile.getOriginalFilename(),
                        HttpStatus.BAD_REQUEST, null, "FV01061", request);
            }
            mapToken = modulAuthority.checkAuthorization(request);//ambil userid dari token
            Integer intUserId = Integer.parseInt(mapToken.get("uid").toString());
            for(int i=0;i<listDivisi.size();i++)
            {
                listDivisi.get(i).setCreatedBy(intUserId);//input userid di masing2 data yang akan di save
                listDivisi.get(i).setCreatedDate(new Date());//input userid di masing2 data yang akan di save
            }
            divisiRepo.saveAll(listDivisi);

        } catch (Exception e) {
            strExceptionArr[1] = "dataToExport(List<Divisi> lt,MultipartFile multipartFile, HttpServletRequest request) --- LINE 274";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST, null, "FE01061", request);
        }
        return new ResponseHandler().
                generateResponse(ConstantMessageGlobal.SUCCESS_UPLOADING_FILE,
                        HttpStatus.CREATED,
                        null,
                        null,
                        request);
    }

    private Page<Divisi> getDataByValue(Pageable pageable, String columnFirst, String valueFirst)
    {
        if(valueFirst.equals("") || valueFirst==null)
        {
            return divisiRepo.findByIsActive(pageable,true);
        }
        if(columnFirst.equals("id"))
        {
            return divisiRepo.findByIsActiveAndIdDivisi(pageable,true,Long.parseLong(valueFirst));
        } else if (columnFirst.equals("nama")) {
            return divisiRepo.findByIsActiveAndNamaDivisiContainsIgnoreCase(pageable,true,valueFirst);
        } else if (columnFirst.equals("kode")) {
            return divisiRepo.findByIsActiveAndKodeDivisiContainsIgnoreCase(pageable,true,valueFirst);
        } else if (columnFirst.equals("deskripsi")) {
            return divisiRepo.findByIsActiveAndDeskripsiDivisiContainsIgnoreCase(pageable,true,valueFirst);
        }

        return divisiRepo.findByIsActive(pageable,true);// ini default kalau parameter search nya tidak sesuai--- asumsi nya di hit bukan dari web
    }

}