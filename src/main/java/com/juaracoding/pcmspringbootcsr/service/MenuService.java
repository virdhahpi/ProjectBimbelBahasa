package com.juaracoding.pcmspringbootcsr.service;

import com.juaracoding.pcmspringbootcsr.configuration.OtherConfig;
import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageGlobal;
import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageMenu;
import com.juaracoding.pcmspringbootcsr.core.IService;
import com.juaracoding.pcmspringbootcsr.core.security.ModulAuthority;
import com.juaracoding.pcmspringbootcsr.dto.menu.MenuDTO;
import com.juaracoding.pcmspringbootcsr.dto.SearchParamDTO;
import com.juaracoding.pcmspringbootcsr.dto.menuheader.MenuHeaderOptionDTO;
import com.juaracoding.pcmspringbootcsr.handler.ResponseHandler;
import com.juaracoding.pcmspringbootcsr.model.Menu;
import com.juaracoding.pcmspringbootcsr.repo.MenuHeaderRepo;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;


/*
    Modul Code = 03
 */
@Service
@Transactional
public class MenuService implements IService<Menu> {
    private MenuRepo menuRepo;
    private MenuHeaderRepo menuHeaderRepo;
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
    private List<MenuHeaderOptionDTO> ltMenuHeaderDTO = null;
    private String authorizationCode = "13";
    @Autowired
    public MenuService(MenuRepo menuRepo, MenuHeaderRepo menuHeaderRepo) {
        strExceptionArr[0]="MenuService";
        mapColumn();
        this.menuRepo = menuRepo;
        this.menuHeaderRepo = menuHeaderRepo;
    }

    private void mapColumn()
    {
        listSearchParamDTO.add(new SearchParamDTO("id","ID"));
        listSearchParamDTO.add(new SearchParamDTO("nama","NAMA"));
        listSearchParamDTO.add(new SearchParamDTO("path","PATH"));
        listSearchParamDTO.add(new SearchParamDTO("group","GROUP"));
    }

    @Override
    public ResponseEntity<Object> save(Menu menu, HttpServletRequest request) {
        getMasterComponent();
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
        if(!(Boolean)mapToken.get("isValid")){
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
                    HttpStatus.FORBIDDEN,
                    null,
                    "X-AUTH-001", request);
        }
        if(menu==null)
        {
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_DATA_INVALID,
                    HttpStatus.BAD_REQUEST,
                    mapComponent,
                    "FV03001", request);//FAILED VALIDATION
        }
        try {//FORBIDEN
            mapToken = modulAuthority.checkAuthorization(request);//ambil userid dari token
            menu.setCreatedBy(Integer.parseInt(mapToken.get("uid").toString()));
            menu.setCreatedDate(new Date());
            menuRepo.save(menu);
        } catch (Exception e) {
            strExceptionArr[1] = "save(Menu menu, HttpServletRequest request) --- LINE 82";
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
    public ResponseEntity<Object> saveBatch(List<Menu> lt, HttpServletRequest request) {
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
            menuRepo.saveAll(lt);
        } catch (Exception e) {
            strExceptionArr[1] = "saveBatch(List<Menu> lt, HttpServletRequest request) --- LINE 115";
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
    public ResponseEntity<Object> edit(Long id, Menu menu, HttpServletRequest request) {
        getMasterComponent();
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
        if(!(Boolean)mapToken.get("isValid")){
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
                    HttpStatus.FORBIDDEN,
                    null,
                    "X-AUTH-001", request);
        }
        try {
            Optional<Menu> optionalMenu = menuRepo.findByIsActiveAndIdMenu(true,id);
            if(optionalMenu.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageMenu.WARNING_MENU_NOT_EXISTS,
                        HttpStatus.NOT_ACCEPTABLE,
                        mapComponent,
                        "FV03021",request);
            }
            Menu nextMenu = optionalMenu.get();
            nextMenu.setNamaMenu(menu.getNamaMenu());
            nextMenu.setPathMenu(menu.getPathMenu());
            nextMenu.setEndPoint(menu.getEndPoint());
            nextMenu.setMenuHeader(menu.getMenuHeader());

            mapToken = modulAuthority.checkAuthorization(request);//ambil userid dari token
            nextMenu.setModifiedBy(Integer.parseInt(mapToken.get("uid").toString()));
            nextMenu.setModifiedDate(new Date());

        } catch (Exception e) {
            strExceptionArr[1] = " edit(Long id, Menu menu, HttpServletRequest request) --- LINE 149";
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
        Optional<Menu> optionalMenu = null;
        Menu nextMenu = null;
        try {
            optionalMenu = menuRepo.findByIsActiveAndIdMenu(true,id);
            if(optionalMenu.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageGlobal.WARNING_DATA_EMPTY,
                        HttpStatus.NOT_ACCEPTABLE,
                        mapComponent,
                        "FV03031",request);
            }
            nextMenu = optionalMenu.get();
            nextMenu.setActive(false);
            mapToken = modulAuthority.checkAuthorization(request);//ambil userid dari token
            nextMenu.setModifiedBy(Integer.parseInt(mapToken.get("uid").toString()));
            nextMenu.setModifiedDate(new Date());

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
        Optional<Menu> optionalMenu = null;
        try{
            optionalMenu = menuRepo.findByIsActiveAndIdMenu(true,id);
            if(optionalMenu.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageMenu.WARNING_MENU_NOT_EXISTS,
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
        Menu menu = optionalMenu.get();
        MenuDTO menuDTO = modelMapper.map(menu, new TypeToken<MenuDTO>() {}.getType());
        mapComponent.put("listGroup",ltMenuHeaderDTO);
        mapComponent.put("menu",menuDTO);
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
        if(!(Boolean)mapToken.get("isValid")){
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_FORBIDEN,
                    HttpStatus.FORBIDDEN,
                    null,
                    "X-AUTH-001", request);
        }

        Page<Menu> pageMenu = null;
        List<Menu> listMenu = null;
        List<MenuDTO> listMenuDTO = null;
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
                                        mapComponent,
                                        "FE03051",
                                        request);
                    }
                }
            }
            /*
                PENGISIAN DATA dan VALIDASI UNTUK PAGING ADA DI METHOD getDataByValue
             */
            pageMenu = getDataByValue(pageable,columFirst,valueFirst);
            listMenu = pageMenu.getContent();
            if(listMenu.size()==0)
            {
                return new ResponseHandler().
                        generateResponse(ConstantMessageGlobal.WARNING_DATA_EMPTY,
                                HttpStatus.NOT_FOUND,
                                mapComponent,
                                "FV03052",
                                request);
            }
            listMenuDTO = modelMapper.map(listMenu, new TypeToken<List<MenuDTO>>() {}.getType());
            mapResult = transformToDTO.transformObject(objectMapper,listMenuDTO,pageMenu,listSearchParamDTO,ltMenuHeaderDTO,columFirst,valueFirst);
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

    private Page<Menu> getDataByValue(Pageable pageable, String columnFirst, String valueFirst)
    {
        if(valueFirst.equals("") || valueFirst==null)
        {
            return menuRepo.findByIsActive(pageable,true);
        }
        if(columnFirst.equals("id"))
        {
            return menuRepo.findByIsActiveAndIdMenu(pageable,true,Long.parseLong(valueFirst));
        } else if (columnFirst.equals("nama")) {
            return menuRepo.findByIsActiveAndNamaMenuContainsIgnoreCase(pageable,true,valueFirst);
        } else if (columnFirst.equals("path]")) {
            return menuRepo.findByIsActiveAndPathMenuContainsIgnoreCase(pageable,true,valueFirst);
        }else if (columnFirst.equals("endPoint")) {
            return menuRepo.findByIsActiveAndEndPointContainsIgnoreCase(pageable,true,valueFirst);
        }

        return menuRepo.findByIsActive(pageable,true);// ini default kalau parameter search nya tidak sesuai--- asumsi nya di hit bukan dari web
    }

    private Map<String,Object> getMasterComponent(){
        ltMenuHeaderDTO =  modelMapper.map(menuHeaderRepo.findByIsActive(true), new TypeToken<List<MenuHeaderOptionDTO>>() {}.getType());
        mapComponent.put("listGroup",ltMenuHeaderDTO);

        return mapComponent;
    }
}