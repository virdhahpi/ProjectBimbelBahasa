package com.juaracoding.pcmspringbootcsr.service;

import com.juaracoding.pcmspringbootcsr.configuration.OtherConfig;
import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageAkses;
import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageGlobal;
import com.juaracoding.pcmspringbootcsr.core.IService;
import com.juaracoding.pcmspringbootcsr.core.security.ModulAuthority;
import com.juaracoding.pcmspringbootcsr.dto.akses.AksesDTO;
import com.juaracoding.pcmspringbootcsr.dto.SearchParamDTO;
import com.juaracoding.pcmspringbootcsr.dto.divisi.DivisiOptionDTO;
import com.juaracoding.pcmspringbootcsr.dto.menu.MenuOptionDTO;
import com.juaracoding.pcmspringbootcsr.handler.ResponseHandler;
import com.juaracoding.pcmspringbootcsr.model.Akses;
import com.juaracoding.pcmspringbootcsr.repo.AksesRepo;
import com.juaracoding.pcmspringbootcsr.repo.DivisiRepo;
import com.juaracoding.pcmspringbootcsr.repo.MenuRepo;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


/*
    Modul Code = 04
 */
@Service
@Transactional
public class AksesService implements IService<Akses> {
    private AksesRepo aksesRepo;
    private DivisiRepo divisiRepo;
    @Autowired
    private MenuRepo menuRepo;
    private String[] strExceptionArr = new String[2];
    @Autowired
    private ModelMapper modelMapper;
    private Map<String,Object> objectMapper = new HashMap<String,Object>();
    private TransformToDTO transformToDTO = new TransformToDTO();
    private List<SearchParamDTO> listSearchParamDTO  = new ArrayList<>();
    private Map<String,Object> mapToken = new HashMap<String,Object>();
    private List<DivisiOptionDTO> ltDivisiOptDTO = null;
    private List<MenuOptionDTO> ltMenuOptDTO = null;
    private String authorizationCode = "14";

    @Autowired
    private ModulAuthority modulAuthority;

    private Map<String,Object> mapComponent = new HashMap<String,Object>();

    @Autowired
    public AksesService(AksesRepo aksesRepo, DivisiRepo divisiRepo) {
        strExceptionArr[0]="AksesService";
        mapColumn();
        this.aksesRepo = aksesRepo;
        this.divisiRepo = divisiRepo;
    }

    private void mapColumn()
    {
        listSearchParamDTO.add(new SearchParamDTO("id","ID"));
        listSearchParamDTO.add(new SearchParamDTO("nama","NAMA"));
        listSearchParamDTO.add(new SearchParamDTO("namaDivisi","DIVISI"));
    }

    @Override
    public ResponseEntity<Object> save(Akses akses, HttpServletRequest request) {
        getMasterComponent();//perubahan 21-12-2023
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
        if(!(Boolean)mapToken.get("isValid")){
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
                    HttpStatus.FORBIDDEN,
                    null,
                    "X-AUTH-001", request);
        }
        if(akses==null)
        {
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_DATA_INVALID,
                    HttpStatus.BAD_REQUEST,
                    mapComponent,//perubahan 21-12-2023
                    "FV04001", request);//FAILED VALIDATION
        }
        try {

            mapToken = modulAuthority.checkAuthorization(request);//ambil userid dari token
            akses.setCreatedBy(Integer.parseInt(mapToken.get("uid").toString()));
            akses.setCreatedDate(new Date());
            aksesRepo.save(akses);
        } catch (Exception e) {
            strExceptionArr[1] = "save(Akses akses, HttpServletRequest request) --- LINE 82";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    mapComponent,//perubahan 21-12-2023
                    "FE04001", request);//FAILED ERROR
        }
        return new ResponseHandler().generateResponse(ConstantMessageGlobal.SUCCESS_SAVE,
                HttpStatus.CREATED,
                mapComponent,
                null, request);
    }

    @Override
    public ResponseEntity<Object> saveBatch(List<Akses> lt, HttpServletRequest request) {
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
                    "FV04011", request);
        }
        mapToken = modulAuthority.checkAuthorization(request);//ambil userid dari token
        Integer intUserId = Integer.parseInt(mapToken.get("uid").toString());
        for(int i=0;i<lt.size();i++)
        {
            lt.get(i).setCreatedBy(intUserId);//input userid di masing2 data yang akan di save
            lt.get(i).setCreatedDate(new Date());//input userid di masing2 data yang akan di save
        }
        try {
            aksesRepo.saveAll(lt);
        } catch (Exception e) {
            strExceptionArr[1] = "saveBatch(List<Akses> lt, HttpServletRequest request) --- LINE 115";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    mapComponent,//perubahan 21-12-2023
                    "FE04011", request);
        }
        return new ResponseHandler().generateResponse(ConstantMessageGlobal.SUCCESS_SAVE,
                HttpStatus.CREATED,
                mapComponent,//perubahan 21-12-2023
                null, request);
    }

    @Override
    public ResponseEntity<Object> edit(Long id, Akses akses, HttpServletRequest request) {
        getMasterComponent();
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
        if(!(Boolean)mapToken.get("isValid")){
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
                    HttpStatus.FORBIDDEN,
                    null,
                    "X-AUTH-001", request);
        }
        try {
            Optional<Akses> optionalAkses = aksesRepo.findByIsActiveAndIdAkses(true,id);
            if(optionalAkses.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageAkses.WARNING_AKSES_NOT_EXISTS,
                        HttpStatus.NOT_ACCEPTABLE,
                        mapComponent,//perubahan 21-12-2023
                        "FV04021",request);
            }
            Akses nextAkses = optionalAkses.get();
            nextAkses.setNamaAkses(akses.getNamaAkses());
            nextAkses.setDivisi(akses.getDivisi());
            nextAkses.setListMenuAkses(akses.getListMenuAkses());
            mapToken = modulAuthority.checkAuthorization(request);//ambil userid dari token
            nextAkses.setModifiedBy(Integer.parseInt(mapToken.get("uid").toString()));
            nextAkses.setModifiedDate(new Date());

        } catch (Exception e) {
            strExceptionArr[1] = " edit(Long id, Akses akses, HttpServletRequest request) --- LINE 141";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    mapComponent,//perubahan 21-12-2023
                    "FE04021", request);
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
        Optional<Akses> optionalAkses = null;
        Akses nextAkses = null;
        try {
            optionalAkses = aksesRepo.findByIsActiveAndIdAkses(true,id);
            if(optionalAkses.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageGlobal.WARNING_DATA_EMPTY,
                        HttpStatus.NOT_ACCEPTABLE,
                        mapComponent,//perubahan 21-12-2023
                        "FV04031",request);
            }
            nextAkses = optionalAkses.get();
            nextAkses.setActive(false);
            mapToken = modulAuthority.checkAuthorization(request);//ambil userid dari token
            nextAkses.setModifiedBy(Integer.parseInt(mapToken.get("uid").toString()));
            nextAkses.setModifiedDate(new Date());

        } catch (Exception e) {
            strExceptionArr[1] = " delete(Long id, HttpServletRequest request)  --- LINE 174";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    mapComponent,//perubahan 21-12-2023
                    "FE04031", request);
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
        Optional<Akses> optionalAkses = null;
        try{
            optionalAkses = aksesRepo.findByIsActiveAndIdAkses(true,id);
            if(optionalAkses.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageAkses.WARNING_AKSES_NOT_EXISTS,
                        HttpStatus.NOT_ACCEPTABLE,
                        mapComponent,//perubahan 21-12-2023
                        "FV04041",request);
            }
        }catch (Exception e)
        {
            strExceptionArr[1] = "findById(Long id, HttpServletRequest request) --- LINE 344";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,
                    HttpStatus.BAD_REQUEST,
                    mapComponent,//perubahan 21-12-2023
                    "FE04041", request);
        }
        Akses nextAkses = optionalAkses.get();
        AksesDTO aksesDTO = modelMapper.map(nextAkses, new TypeToken<AksesDTO>() {}.getType());
        return new ResponseHandler().
                generateResponse(ConstantMessageGlobal.SUCCESS_FIND_BY,
                        HttpStatus.OK,
                        aksesDTO,
                        mapComponent,//perubahan 21-12-2023
                        request);
    }

    @Override
    public ResponseEntity<Object> find(Pageable pageable, String columFirst, String valueFirst, HttpServletRequest request) {
        getMasterComponent();//perubahan 21-12-2023
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
        if(!(Boolean)mapToken.get("isValid")){
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
                    HttpStatus.FORBIDDEN,
                    null,
                    "X-AUTH-001", request);
        }
        Page<Akses> pageAkses = null;
        List<Akses> listAkses = null;
        List<AksesDTO> listAksesDTO = null;
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
                                        "FE04051",
                                        request);
                    }
                }
            }
            /*
                PENGISIAN DATA dan VALIDASI UNTUK PAGING ADA DI METHOD getDataByValue
             */
            pageAkses = getDataByValue(pageable,columFirst,valueFirst);
            listAkses = pageAkses.getContent();
            if(listAkses.size()==0)
            {
                return new ResponseHandler().
                        generateResponse(ConstantMessageGlobal.WARNING_DATA_EMPTY,
                                HttpStatus.NOT_FOUND,
                                mapComponent,
                                "FV04052",
                                request);
            }
            listAksesDTO = modelMapper.map(listAkses, new TypeToken<List<AksesDTO>>() {}.getType());
            mapResult = transformToDTO.transformObject(objectMapper,listAksesDTO,pageAkses,listSearchParamDTO,columFirst,valueFirst,ltDivisiOptDTO,ltMenuOptDTO);
//            mapResult.put("searchParam",listSearchParamDTO);
//            mapResult.put("columnFirst",columFirst);
//            mapResult.put("valueFirst",valueFirst);
//            mapResult.put("listGroupDivisi",ltDivisiOptDTO);
//            mapResult.put("listGroupMenu",ltMenuOptDTO);
        }

        catch (Exception e)
        {
            strExceptionArr[1] = "find(Pageable pageable, String columFirst, String valueFirst, HttpServletRequest request) --- LINE 272";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_DATA_INVALID,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    mapComponent,
                    "FE04051", request);
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

    private Page<Akses> getDataByValue(Pageable pageable, String columnFirst, String valueFirst)
    {
        if(valueFirst.equals("") || valueFirst==null)
        {
            return aksesRepo.findByIsActive(pageable,true);
        }
        if(columnFirst.equals("id"))
        {
            return aksesRepo.findByIsActiveAndIdAkses(pageable,true,Long.parseLong(valueFirst));
        } else if (columnFirst.equals("nama")) {
            return aksesRepo.findByIsActiveAndNamaAksesContainsIgnoreCase(pageable,true,valueFirst);
        }else if (columnFirst.equals("namaDivisi")) {
            return aksesRepo.findByNamaDivisi(pageable,valueFirst);
        }

        return aksesRepo.findByIsActive(pageable,true);// ini default kalau parameter search nya tidak sesuai--- asumsi nya di hit bukan dari web
    }

    private Map<String,Object> getMasterComponent(){
        ltDivisiOptDTO =  modelMapper.map(divisiRepo.findByIsActive(true), new TypeToken<List<DivisiOptionDTO>>() {}.getType());
        ltMenuOptDTO =  modelMapper.map(menuRepo.findByIsActive(true), new TypeToken<List<MenuOptionDTO>>() {}.getType());
        mapComponent.put("listGroupDivisi",ltDivisiOptDTO);
        mapComponent.put("listGroupMenu",ltMenuOptDTO);

        return mapComponent;
    }
}
