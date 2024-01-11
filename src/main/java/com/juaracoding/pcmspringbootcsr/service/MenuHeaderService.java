package com.juaracoding.pcmspringbootcsr.service;

import com.juaracoding.pcmspringbootcsr.configuration.OtherConfig;
import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageGlobal;
import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageMenuHeader;
import com.juaracoding.pcmspringbootcsr.core.IService;
import com.juaracoding.pcmspringbootcsr.core.security.ModulAuthority;
import com.juaracoding.pcmspringbootcsr.dto.SearchParamDTO;
import com.juaracoding.pcmspringbootcsr.dto.menuheader.MenuHeaderDTO;
import com.juaracoding.pcmspringbootcsr.handler.ResponseHandler;
import com.juaracoding.pcmspringbootcsr.model.MenuHeader;
import com.juaracoding.pcmspringbootcsr.repo.MenuHeaderRepo;
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
    KODE MODUL 02
 */
@Service
@Transactional
public class MenuHeaderService implements IService<MenuHeader> {

    private MenuHeaderRepo menuHeaderRepo;
    private String[] strExceptionArr = new String[2];
    @Autowired
    private ModelMapper modelMapper;
    private Map<String,Object> objectMapper = new HashMap<String,Object>();
    private TransformToDTO transformToDTO = new TransformToDTO();
    private Map<String,Object> mapToken = new HashMap<String,Object>();
    @Autowired
    private ModulAuthority modulAuthority;
    private List<SearchParamDTO> listSearchParamDTO  = new ArrayList<>();
    @Autowired
    private CsvReader csvReader = new CsvReader();
    private String authorizationCode = "12";//ini disinkron kan dengan id di table MstMenu

    @Autowired
    public MenuHeaderService(MenuHeaderRepo menuHeaderRepo) {
        strExceptionArr[0]="MenuHeaderService";
        mapColumn();
        this.menuHeaderRepo = menuHeaderRepo;
    }

    private void mapColumn()
    {
        listSearchParamDTO.add(new SearchParamDTO("id","ID MENU HEADER"));
        listSearchParamDTO.add(new SearchParamDTO("nama","NAMA MENU HEADER"));
        listSearchParamDTO.add(new SearchParamDTO("deskripsi","DESKRIPSI MENU HEADER"));
    }

    @Override
    public ResponseEntity<Object> save(MenuHeader menuHeader, HttpServletRequest request) {
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
        if(!(Boolean)mapToken.get("isValid")){
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
                    HttpStatus.FORBIDDEN,
                    null,
                    "X-AUTH-001", request);
        }

        if(menuHeader==null)
        {
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_DATA_INVALID,
                    HttpStatus.BAD_REQUEST,
                    null,
                    "FV02001", request);//FAILED VALIDATION
        }
        try {//FORBIDEN
            mapToken = modulAuthority.checkAuthorization(request);//ambil userid dari token
            menuHeader.setCreatedBy(Integer.parseInt(mapToken.get("uid").toString()));
            menuHeader.setCreatedDate(new Date());
            menuHeaderRepo.save(menuHeader);
        } catch (Exception e) {
            strExceptionArr[1] = "save(MenuHeader menuHeader, HttpServletRequest request) --- LINE 82";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    null,
                    "FE02001", request);//FAILED ERROR
        }
        return new ResponseHandler().generateResponse(ConstantMessageGlobal.SUCCESS_SAVE,
                HttpStatus.CREATED,
                null,
                null, request);
    }

    @Override
    public ResponseEntity<Object> saveBatch(List<MenuHeader> lt, HttpServletRequest request) {
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
                    null,
                    "FV02011", request);
        }
        mapToken = modulAuthority.checkAuthorization(request);//ambil userid dari token
        Integer intUserId = Integer.parseInt(mapToken.get("uid").toString());
        for(int i=0;i<lt.size();i++)
        {
            lt.get(i).setCreatedBy(intUserId);//input userid di masing2 data yang akan di save
            lt.get(i).setCreatedDate(new Date());//input userid di masing2 data yang akan di save
        }

        try {
            menuHeaderRepo.saveAll(lt);
        } catch (Exception e) {
            strExceptionArr[1] = "saveBatch(List<MenuHeader> lt, HttpServletRequest request) --- LINE 115";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    null,
                    "FE02011", request);
        }
        return new ResponseHandler().generateResponse(ConstantMessageGlobal.SUCCESS_SAVE,
                HttpStatus.CREATED,
                null,
                null, request);
    }

    @Override
    public ResponseEntity<Object> edit(Long id, MenuHeader menuHeader, HttpServletRequest request) {
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
        if(!(Boolean)mapToken.get("isValid")){
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
                    HttpStatus.FORBIDDEN,
                    null,
                    "X-AUTH-001", request);
        }

        try {
            Optional<MenuHeader> optionalMenuHeader = menuHeaderRepo.findByIsActiveAndIdMenuHeader(true,id);
            if(optionalMenuHeader.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageMenuHeader.WARNING_MENU_HEADER_NOT_EXISTS,
                        HttpStatus.NOT_ACCEPTABLE,
                        null,
                        "FV02021",request);
            }
            MenuHeader nextMenuHeader = optionalMenuHeader.get();
            nextMenuHeader.setNamaMenuHeader(menuHeader.getNamaMenuHeader());
            nextMenuHeader.setDeskripsiMenuHeader(menuHeader.getDeskripsiMenuHeader());

            mapToken = modulAuthority.checkAuthorization(request);//ambil userid dari token
            nextMenuHeader.setModifiedBy(Integer.parseInt(mapToken.get("uid").toString()));
            nextMenuHeader.setModifiedDate(new Date());

        } catch (Exception e) {
            strExceptionArr[1] = " edit(Long id, MenuHeader menuHeader, HttpServletRequest request) --- LINE 149";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    null,
                    "FE02021", request);
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
        Optional<MenuHeader> optionalMenuHeader = null;
        MenuHeader nextMenuHeader = null;
        try {
            optionalMenuHeader = menuHeaderRepo.findByIsActiveAndIdMenuHeader(true,id);
            if(optionalMenuHeader.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageGlobal.WARNING_DATA_EMPTY,
                        HttpStatus.NOT_ACCEPTABLE,
                        null,
                        "FV02031",request);
            }
            nextMenuHeader = optionalMenuHeader.get();
            nextMenuHeader.setActive(false);
            mapToken = modulAuthority.checkAuthorization(request);//ambil userid dari token
            nextMenuHeader.setModifiedBy(Integer.parseInt(mapToken.get("uid").toString()));
            nextMenuHeader.setModifiedDate(new Date());
        } catch (Exception e) {
            strExceptionArr[1] = " deleteDivisi(Long idDemo, HttpServletRequest request) --- LINE 181";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    null,
                    "FE02031", request);
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
        Optional<MenuHeader> optionalMenuHeader = null;
        try{
            optionalMenuHeader = menuHeaderRepo.findByIsActiveAndIdMenuHeader(true,id);
            if(optionalMenuHeader.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageMenuHeader.WARNING_MENU_HEADER_NOT_EXISTS,
                        HttpStatus.NOT_ACCEPTABLE,
                        null,
                        "FV02041",request);
            }
        }catch (Exception e)
        {
            strExceptionArr[1] = "findById(Long id, HttpServletRequest request) --- LINE 344";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    null,
                    "FE02041", request);
        }
        MenuHeader menuHeader = optionalMenuHeader.get();
        MenuHeaderDTO menuHeaderDTO = modelMapper.map(menuHeader, new TypeToken<MenuHeaderDTO>() {}.getType());
        return new ResponseHandler().
                generateResponse(ConstantMessageGlobal.SUCCESS_FIND_BY,
                        HttpStatus.OK,
                        menuHeaderDTO,
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
        Page<MenuHeader> pageMenuHeader = null;
        List<MenuHeader> listMenuHeader = null;
        List<MenuHeaderDTO> listMenuHeaderDTO = null;
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
                                        null,//HANDLE NILAI PENCARIAN
                                        "FE02051",
                                        request);
                    }
                }
            }
            /*
                PENGISIAN DATA dan VALIDASI UNTUK PAGING ADA DI METHOD getDataByValue
             */
            pageMenuHeader = getDataByValue(pageable,columFirst,valueFirst);
            listMenuHeader = pageMenuHeader.getContent();
            if(listMenuHeader.size()==0)
            {
                return new ResponseHandler().
                        generateResponse(ConstantMessageGlobal.WARNING_DATA_EMPTY,
                                HttpStatus.NOT_FOUND,
                                null,
                                "FV02052",
                                request);
            }
            listMenuHeaderDTO = modelMapper.map(listMenuHeader, new TypeToken<List<MenuHeaderDTO>>() {}.getType());
            mapResult = transformToDTO.transformObject(objectMapper,listMenuHeaderDTO,pageMenuHeader);
            mapResult.put("searchParam",listSearchParamDTO);
        }

        catch (Exception e)
        {
            strExceptionArr[1] = "find(Pageable pageable, String columFirst, String valueFirst, HttpServletRequest request) --- LINE 272";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_DATA_INVALID,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null,
                    "FE02051", request);
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
                        "FV02061",//errorCode Fail Validation modul-code 001 sequence 001 range 081 - 090
                        request
                );
            }
            /*
                Extract isi / data di file csv nya
             */
            List<MenuHeader> listMenuHeader = csvReader.csvToMenuHeaderData(multipartFile.getInputStream());
            if (listMenuHeader.isEmpty()) {
                strExceptionArr[1] = "dataToExport(List<MenuHeader> lt,MultipartFile multipartFile, HttpServletRequest request) --- LINE 296";
                LoggingFile.exceptionStringz(strExceptionArr, new FileUploadException("FILE KOSONG"), OtherConfig.getFlagLoging());
                return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_EMPTY_FILE + " -- " + multipartFile.getOriginalFilename(),
                        HttpStatus.BAD_REQUEST, null, "FV02061", request);
            }
            mapToken = modulAuthority.checkAuthorization(request);//ambil userid dari token
            Integer intUserId = Integer.parseInt(mapToken.get("uid").toString());
            for(int i=0;i<listMenuHeader.size();i++)
            {
                listMenuHeader.get(i).setCreatedBy(intUserId);//input userid di masing2 data yang akan di save
                listMenuHeader.get(i).setCreatedDate(new Date());//input userid di masing2 data yang akan di save
            }
            menuHeaderRepo.saveAll(listMenuHeader);

        } catch (Exception e) {
            strExceptionArr[1] = "dataToExport(List<MenuHeader> lt,MultipartFile multipartFile, HttpServletRequest request) --- LINE 329";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST, null, "FE02061", request);
        }
        return new ResponseHandler().
                generateResponse(ConstantMessageGlobal.SUCCESS_UPLOADING_FILE,
                        HttpStatus.CREATED,
                        null,
                        null,
                        request);
    }

    private Page<MenuHeader> getDataByValue(Pageable pageable, String columnFirst, String valueFirst)
    {
        if(valueFirst.equals("") || valueFirst==null)
        {
            return menuHeaderRepo.findByIsActive(pageable,true);
        }
        if(columnFirst.equals("id"))
        {
            return menuHeaderRepo.findByIsActiveAndIdMenuHeader(pageable,true,Long.parseLong(valueFirst));
        } else if (columnFirst.equals("nama")) {
            return menuHeaderRepo.findByIsActiveAndNamaMenuHeaderContainsIgnoreCase(pageable,true,valueFirst);
        } else if (columnFirst.equals("deskripsi")) {
            return menuHeaderRepo.findByIsActiveAndDeskripsiMenuHeaderContainsIgnoreCase(pageable,true,valueFirst);
        }

        return menuHeaderRepo.findByIsActive(pageable,true);// ini default kalau parameter search nya tidak sesuai--- asumsi nya di hit bukan dari web
    }
}