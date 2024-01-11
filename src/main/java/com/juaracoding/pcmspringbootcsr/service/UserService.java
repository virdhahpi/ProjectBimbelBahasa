package com.juaracoding.pcmspringbootcsr.service;

import com.juaracoding.pcmspringbootcsr.configuration.OtherConfig;
import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageGlobal;
import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageUser;
import com.juaracoding.pcmspringbootcsr.core.BcryptImpl;
import com.juaracoding.pcmspringbootcsr.core.IService;
import com.juaracoding.pcmspringbootcsr.core.security.JwtUtility;
import com.juaracoding.pcmspringbootcsr.core.security.ModulAuthority;
import com.juaracoding.pcmspringbootcsr.dto.MenuCSRDTO;
import com.juaracoding.pcmspringbootcsr.dto.SearchParamDTO;
import com.juaracoding.pcmspringbootcsr.dto.UserDTO;
import com.juaracoding.pcmspringbootcsr.dto.akses.AksesOptionDTO;
import com.juaracoding.pcmspringbootcsr.dto.regis.ForgetPasswordDTO;
import com.juaracoding.pcmspringbootcsr.handler.RequestCapture;
import com.juaracoding.pcmspringbootcsr.handler.ResponseHandler;
import com.juaracoding.pcmspringbootcsr.model.*;
import com.juaracoding.pcmspringbootcsr.repo.AksesRepo;
import com.juaracoding.pcmspringbootcsr.repo.UserRepo;
import com.juaracoding.pcmspringbootcsr.util.ExecuteSMTP;
import com.juaracoding.pcmspringbootcsr.util.LogTable;
import com.juaracoding.pcmspringbootcsr.util.LoggingFile;
import com.juaracoding.pcmspringbootcsr.util.TransformToDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;

/*
    Modul Code = 05
 */
@Service
@Transactional
public class UserService implements IService<User> , UserDetailsService {
    private UserRepo userRepo;
    private AksesRepo aksesRepo;
    private String [] strExceptionArr = new String[2];
    private Map<String,Object> mapz = new HashMap<>();
    @Autowired
    private ModelMapper modelMapper;
    private String[] strProfile = new String[3];
    private StringBuilder sBuild = new StringBuilder();
    @Autowired
    private JwtUtility jwtUtility;
    private ModulAuthority modulAuthority = new ModulAuthority();
    /*
        declare object for generate menu to FE
     */
    private Map<String,Object> mapTemp = new HashMap<>();
    private Map<String,Object> mapMenu = new HashMap<>();
    private List<Object> lObj = new ArrayList<>();
    private Map<String,Object> objectMapper = new HashMap<String,Object>();
    private TransformToDTO transformToDTO = new TransformToDTO();
    private Map<String,Object> mapComponent = new HashMap<String,Object>();
    private Map<String,Object> mapToken = new HashMap<String,Object>();
    private List<SearchParamDTO> listSearchParamDTO  = new ArrayList<>();
    /*
        end of object declaration
     */
    private List<AksesOptionDTO> ltAksesOptDTO = null;
    Map<String,Object> mapResult = null;
    private String authorizationCode = "15";
    List<UserDTO> listUserDTO = null;
    @Autowired
    public UserService(UserRepo userRepo, AksesRepo aksesRepo) {
        strExceptionArr[0] = "UserService";
        this.userRepo = userRepo;
        this.aksesRepo = aksesRepo;
    }

    private void mapColumn()
    {
        listSearchParamDTO.add(new SearchParamDTO("id","ID"));
        listSearchParamDTO.add(new SearchParamDTO("nama","NAMA"));
    }

    /*flow untuk registrasi STEP 1*/
    public ResponseEntity<Object> checkRegis(User user, HttpServletRequest request) {//RANGE RGS 001-010
        if(user==null)
        {
            return new ResponseHandler().generateResponse(
                    "Data tidak Valid",//message
                    HttpStatus.BAD_REQUEST,//httpstatus
                    null,//object
                    "FVRGS001",//errorCode Fail Validation modul-code RGS sequence 001 range 001 - 010
                    request
            );
        }
        int intVerification = new Random().nextInt(100000,999999);//TOKEN YANG AKAN DIKIRIM KE EMAIL
        Optional<User> opUserResult = userRepo.findByUsernameOrNoHPOrEmail(user.getUsername(),user.getNoHP(),user.getEmail());//INI VALIDASI USER IS EXISTS
        try{
            if(!opUserResult.isEmpty())//kondisi mengecek apakah user sudah terdaftar
            {
                User nextUser = opUserResult.get();
                if(nextUser.getActive())//sudah terdaftar dan sudah aktif
                {
                    //NOTIFIKASI SAAT REGISTRASI BAGIAN MANA YANG SUDAH TERDAFTAR (USERNAME, EMAIL ATAU NOHP)
                    //kasus nya bisa saja user ingin memiliki 2 akun , namun dari sistem tidak memperbolehkan untuk duplikasi username,email atau no hp
                    //jika user ingin memiliki 2 akun , maka dia harus menggunakan username,email dan nohp yang berbeda dan belum terdaftar di sistem
                    /*
                        ex : username : paul, noHP : 628888888, email:paul@gmail.com lalu ingin mendaftar lagi dengan format
                        username : paul123, noHP : 6283333333, email:paul@gmail.com ,di kasus ini user harus menggunakan email lain walau username dan noHp sudah yang baru
                     */
                    if(nextUser.getEmail().equals(user.getEmail()))
                    {
                        return new ResponseHandler().generateResponse(ConstantMessageUser.ERROR_EMAIL_ISEXIST,
                                HttpStatus.NOT_ACCEPTABLE,null,"FVRGS002",request);//EMAIL SUDAH TERDAFTAR DAN AKTIF
                    } else if (nextUser.getNoHP().equals(user.getNoHP())) {//FV = FAILED VALIDATION
                        return new ResponseHandler().generateResponse(ConstantMessageUser.ERROR_NOHP_ISEXIST,
                                HttpStatus.NOT_ACCEPTABLE,null,"FVRGS003",request);//NO HP SUDAH TERDAFTAR DAN AKTIF

                    } else if (nextUser.getUsername().equals(user.getUsername())) {
                        return new ResponseHandler().generateResponse(ConstantMessageUser.ERROR_USERNAME_ISEXIST,
                                HttpStatus.NOT_ACCEPTABLE,null,"FVRGS004",request);//USERNAME SUDAH TERDAFTAR DAN AKTIF
                    } else {
                        /*
                            seharusnya tidak akan pernah masuk kesini karena dari query hanya 3 saja autentikasi nya yaitu :
                            username , email dan no HP
                         */
                        return new ResponseHandler().generateResponse(ConstantMessageUser.ERROR_USER_ISACTIVE,
                                HttpStatus.NOT_ACCEPTABLE,null,"FVRGS005",request);//KARENA YANG DIAMBIL DATA YANG PERTAMA JADI ANGGAPAN NYA SUDAH TERDAFTAR SAJA
                    }
                } else {
                    /*
                        masuk kesini jika user sudah pernah melakukan registrasi (data sudah tersimpan ke tabel) namun tidak melanjutkan sampai selesai
                        flag isDelete = 0
                     */
                    nextUser.setPassword(BcryptImpl.hash(user.getPassword()+user.getUsername()));//ini trick nya agar tidak bisa di hash manual melalui database
                    nextUser.setToken(BcryptImpl.hash(String.valueOf(intVerification)));
                    nextUser.setTokenCounter(nextUser.getTokenCounter()+1);//setiap kali mencoba ditambah 1
                    nextUser.setModifiedBy(Integer.parseInt(nextUser.getIdUser().toString()));
                    nextUser.setModifiedDate(new Date());
                }
            } else {//user belum terdaftar sama sekali artinya user benar-benar baru menndaftar
                user.setPassword(BcryptImpl.hash(user.getPassword()+user.getUsername()));
                user.setToken(BcryptImpl.hash(String.valueOf(intVerification)));
                userRepo.save(user);
            }

            String[] strVerify = new String[3];
            strVerify[0] = "Verifikasi Email";
            strVerify[1] = user.getNamaLengkap();
            strVerify[2] = String.valueOf(intVerification);

            sBuild.setLength(0);
            /*
                method untuk kirim email
             */
            new ExecuteSMTP().sendSMTPToken(user.getEmail(),"TOKEN Verifikasi Email",strVerify,
                    "ver_regis.html");// \\data\\ver_regis
        } catch (Exception e)
        {
            strExceptionArr[1] = "checkRegis(User user, HttpServletRequest request)  --- LINE 130 \n ALL - REQUEST"+ RequestCapture.allRequest(request);
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            LogTable.inputLogRequest(strExceptionArr,e, OtherConfig.getFlagLogTable());
            return new ResponseHandler().generateResponse(ConstantMessageGlobal.ERROR_SAVE_FAILED,HttpStatus.INTERNAL_SERVER_ERROR,null,"FERGS001",request);
        }

        return new ResponseHandler().generateResponse(ConstantMessageGlobal.SUCCESS_SAVE,
                HttpStatus.CREATED,null,null,request);
    }

    /*flow untuk registrasi STEP 2*/
    public ResponseEntity<Object> confirmRegis(User userz, HttpServletRequest request) { // RGS 011 - 020
        Optional<User> opUserResult = userRepo.findByEmail(userz.getEmail());
        try
        {
            if(!opUserResult.isEmpty())
            {
                User nextUser = opUserResult.get();
                if(!BcryptImpl.verifyHash(userz.getToken(),nextUser.getToken()))
                {
//                    if(nextUser.getTokenCounter()!=Integer.parseInt(OtherConfig.getMaxCounterToken()))
//                    {
//                        nextUser.setTokenCounter(nextUser.getTokenCounter()+1);
//                    }
                    return new ResponseHandler().generateResponse(ConstantMessageUser.ERROR_INVALID_TOKEN,
                            HttpStatus.NOT_ACCEPTABLE,null,"FVRGS011",request);
                }
                nextUser.setActive(true);//SET REGISTRASI BERHASIL
                Akses akses = new Akses();
                akses.setIdAkses(1L);// di default 1 untuk new member
                nextUser.setModifiedBy(Integer.parseInt(nextUser.getIdUser().toString()));
                nextUser.setModifiedDate(new Date());
                nextUser.setAkses(akses);
            }
            else
            {
                return new ResponseHandler().generateResponse(ConstantMessageUser.ERROR_USER_NOT_EXISTS,
                        HttpStatus.NOT_FOUND,null,"FVRGS012",request);
            }
        }
        catch (Exception e)
        {
            strExceptionArr[1]="confirmRegis(Userz userz)  --- LINE 164";
            LoggingFile.exceptionStringz(strExceptionArr,e, OtherConfig.getFlagLoging());
            LogTable.inputLogRequest(strExceptionArr,e, OtherConfig.getFlagLogTable());
            return new ResponseHandler().generateResponse(ConstantMessageUser.ERROR_FLOW_INVALID,
                    HttpStatus.INTERNAL_SERVER_ERROR,null,"FERGS013",request);
        }

        return new ResponseHandler().generateResponse(ConstantMessageUser.SUCCESS_CHECK_REGIS,
                HttpStatus.OK,null,null,request);
    }

    /* FLOW UNTUK GANTI PASSWORD STEP 1*/
    public ResponseEntity<Object> sendMailForgetPwd(String email,HttpServletRequest request)
    {
        //CPW 001-010
        if(email==null || email.equals("") || email.equals(""))
        {
            return new ResponseHandler().generateResponse(ConstantMessageUser.ERROR_EMAIL_IS_EMPTY,
                    HttpStatus.BAD_REQUEST,null,"FVCPW001",request);
        }

        int intVerification =0;
        Optional<User> opUser = userRepo.findByEmail(email);
        User nextUser = null;
        try
        {
            if(opUser.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageUser.ERROR_USER_NOT_EXISTS,
                        HttpStatus.NOT_FOUND,null,"FVCPW002",request);
            }
            intVerification = new Random().nextInt(100000,999999);
            nextUser = opUser.get();
            nextUser.setToken(BcryptImpl.hash(String.valueOf(intVerification)));
            nextUser.setModifiedDate(new Date());
            nextUser.setModifiedBy(Integer.parseInt(nextUser.getIdUser().toString()));
        }
        catch (Exception e)
        {
            strExceptionArr[1]="sendMailForgetPwd(String email,HttpServletRequest request)  --- LINE 197";
            LoggingFile.exceptionStringz(strExceptionArr,e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageUser.ERROR_FLOW_INVALID,
                    HttpStatus.INTERNAL_SERVER_ERROR,null,"FECPW001",request);
        }

        strProfile[0]="TOKEN UNTUK VERIFIKASI LUPA PASSWORD";
        strProfile[1]=nextUser.getNamaLengkap();
        strProfile[2]=String.valueOf(intVerification);

        /*EMAIL NOTIFICATION*/
        if(OtherConfig.getFlagSmtpActive().equalsIgnoreCase("y") && !nextUser.getEmail().equals(""))
        {
            sBuild.setLength(0);
            new ExecuteSMTP().sendSMTPToken(nextUser.getEmail(),"VERIFIKASI TOKEN" ,strProfile,
                    "ver_lupa_pwd.html");
//            sBuild.setLength(0);
//            new ExecuteSMTP().sendSMTPToken(nextUser.getEmail(),"VERIFIKASI TOKEN" ,strProfile,
//                    sBuild.append(OtherConfig.getOsSplitting()).
//                            append("data").append(OtherConfig.getOsSplitting()).
//                            append("ver_lupa_pwd.html").toString());
        }
        return new ResponseHandler().generateResponse(ConstantMessageUser.SUCCESS_SENDING_NEW_TOKEN,
                HttpStatus.OK,null,null,request);
    }


    /* FLOW UNTUK GANTI PASSWORD STEP 2*/
    public ResponseEntity<Object> confirmTokenForgotPwd(ForgetPasswordDTO forgetPasswordDTO, HttpServletRequest request)
    {
        String emailz = forgetPasswordDTO.getEmail();
        String token = forgetPasswordDTO.getToken();
        User nextUserz = null;
        int intVerification = 0;
        String strNewToken = "";
        Optional<User> opUser = userRepo.findByEmail(emailz);
        try
        {
            if(opUser.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageUser.ERROR_USER_NOT_EXISTS,
                        HttpStatus.NOT_FOUND,null,"FVCPW011",request);
            }

            nextUserz = opUser.get();

            if(!BcryptImpl.verifyHash(token,nextUserz.getToken()))//VALIDASI TOKEN
            {
                return new ResponseHandler().generateResponse(ConstantMessageUser.ERROR_INVALID_TOKEN,
                        HttpStatus.NOT_FOUND,null,"FVCPW012",request);
            }

            strNewToken = BcryptImpl.hash(String.valueOf(intVerification));
            nextUserz.setToken(strNewToken);
            nextUserz.setModifiedDate(new Date());
            nextUserz.setModifiedBy(Integer.parseInt(nextUserz.getIdUser().toString()));
        }
        catch (Exception e)
        {
            strExceptionArr[1]="confirmTokenForgotPwd(ForgetPasswordDTO forgetPasswordDTO, WebRequest request)  --- LINE 243";
            LoggingFile.exceptionStringz(strExceptionArr,e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageUser.ERROR_FLOW_INVALID,
                    HttpStatus.INTERNAL_SERVER_ERROR,null,"FECPW011",request);
        }
        Map<String,Object> mapNewToken = new HashMap<String,Object>();
        mapNewToken.put("token",strNewToken);
        return new ResponseHandler().generateResponse(ConstantMessageUser.SUCCESS_TOKEN_MATCH,
                HttpStatus.OK,mapNewToken,null,request);
    }

    /* FLOW UNTUK GANTI PASSWORD STEP 3 / Terakhir*/
    public ResponseEntity<Object> confirmForgotPassword(ForgetPasswordDTO forgetPasswordDTO, HttpServletRequest request)
    {
        String emailz = forgetPasswordDTO.getEmail();
        String newPassword = forgetPasswordDTO.getPassword();
        String confirmPassword = forgetPasswordDTO.getNewPassword();
        String strToken = forgetPasswordDTO.getToken()==null?"":forgetPasswordDTO.getToken();

        Optional<User> optionalUser = userRepo.findByEmail(emailz);
        try
        {
            if(optionalUser.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageUser.ERROR_FLOW_INVALID,
                        HttpStatus.NOT_FOUND,null,"FVCPW021",request);
            }

            User nextUser = optionalUser.get();
            /*
                pengecekan dari flow sebelumnya
                jika token yang diberikan tidak sesuai dengan flow otentikasi yang sebelumnya
                maka tidak diperbolehkan untuk merubah password
             */
            if(confirmPassword.equals(newPassword) && strToken.equals(nextUser.getToken()))//PASSWORD BARU DENGAN PASSWORD KONFIRMASI TIDAK SAMA
            {
                nextUser.setPassword(BcryptImpl.hash(String.valueOf(newPassword+nextUser.getUsername())));
                nextUser.setActive(true);
                nextUser.setModifiedDate(new Date());
                nextUser.setModifiedBy(Integer.parseInt(nextUser.getIdUser().toString()));
            }
            else
            {
                return new ResponseHandler().generateResponse(ConstantMessageUser.ERROR_PASSWORD_CONFIRM_FAILED,
                        HttpStatus.FORBIDDEN,null,"FVCPW022",request);
            }
        }
        catch (Exception e)
        {
            strExceptionArr[1]="confirmForgotPassword(ForgetPasswordDTO forgetPasswordDTO, WebRequest request)  --- LINE 297";
            LoggingFile.exceptionStringz(strExceptionArr,e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageUser.ERROR_FLOW_INVALID,
                    HttpStatus.INTERNAL_SERVER_ERROR,null,"FECPW021",request);
        }
        return new ResponseHandler().generateResponse(ConstantMessageUser.SUCCESS_CHANGE_PWD,
                HttpStatus.OK,null,null,request);
    }


    /*flow untuk REQUEST TOKEN BARU UNTUK FLOW REGIS DAN GANTI PASSWORD STEP 1*/
    public ResponseEntity<Object> getNewToken(String emailz, HttpServletRequest request) {///AUTH 001-010
        Optional<User> optionalUser = userRepo.findByEmail(emailz);//DATANYA PASTI HANYA 1
        String emailForSMTP = "";
        int intVerification = 0;
        User userz = null;
        try
        {
            if(optionalUser.isEmpty())
            {
                return new ResponseHandler().generateResponse(ConstantMessageUser.ERROR_FLOW_INVALID,
                        HttpStatus.NOT_ACCEPTABLE,null,"FVAUTH009",request);
            }

            intVerification = new Random().nextInt(100000,999999);
            userz = optionalUser.get();
            userz.setToken(BcryptImpl.hash(String.valueOf(intVerification)));
            userz.setModifiedDate(new Date());
            userz.setModifiedBy(Integer.parseInt(userz.getIdUser().toString()));
            emailForSMTP = userz.getEmail();
        }
        catch (Exception e)
        {
            strExceptionArr[1]="getNewToken(String emailz, WebRequest request)  --- LINE 185";
            LoggingFile.exceptionStringz(strExceptionArr,e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageUser.ERROR_FLOW_INVALID,
                    HttpStatus.INTERNAL_SERVER_ERROR,null,"FE05004",request);
        }

        strProfile[0]="PERMINTAAN TOKEN BARU UNTUK VERIFIKASI";
        strProfile[1]=userz.getNamaLengkap();
        strProfile[2]=String.valueOf(intVerification);

        /*EMAIL NOTIFICATION*/
        if(OtherConfig.getFlagSmtpActive().equalsIgnoreCase("y") && !emailForSMTP.equals(""))
        {
            new ExecuteSMTP().sendSMTPToken(emailForSMTP,"PERMINTAAN TOKEN BARU" ,strProfile,"ver_token_baru.html");
//            sBuild.setLength(0);
//            new ExecuteSMTP().sendSMTPToken(emailForSMTP,"PERMINTAAN TOKEN BARU" ,strProfile,sBuild.append(OtherConfig.getOsSplitting()).
//                    append("data").append(OtherConfig.getOsSplitting()).append("ver_token_baru.html").toString());
        }


        return new ResponseHandler().generateResponse(ConstantMessageUser.SUCCESS_LOGIN,
                HttpStatus.OK,null,null,request);
    }

    public ResponseEntity<Object> doLogin(User userz, HttpServletRequest request) {
        userz.setEmail(userz.getUsername());
        userz.setNoHP(userz.getUsername());
        Optional<User> opUserResult = userRepo.findByUsernameOrNoHPOrEmailAndIsActive(userz.getEmail(),userz.getNoHP(),userz.getUsername(),true);//DATANYA PASTI HANYA 1
        User nextUser = null;
        try
        {
            if(!opUserResult.isEmpty())
            {
                nextUser = opUserResult.get();
                if(!BcryptImpl.verifyHash(userz.getPassword()+nextUser.getUsername(),nextUser.getPassword()))//dicombo dengan userName
                {
                    return new ResponseHandler().generateResponse(ConstantMessageUser.ERROR_LOGIN_FAILED,
                            HttpStatus.NOT_ACCEPTABLE,null,"FV05007",request);
                }
                Integer intUserId = Integer.parseInt(String.valueOf(nextUser.getIdUser()));
                nextUser.setLastLoginDate(new Date());
                nextUser.setTokenCounter(0);//SETIAP KALI LOGIN BERHASIL , BERAPA KALIPUN UJI COBA REQUEST TOKEN YANG SEBELUMNYA GAGAL AKAN SECARA OTOMATIS DIRESET MENJADI 0
                nextUser.setPasswordCounter(0);//SETIAP KALI LOGIN BERHASIL , BERAPA KALIPUN UJI COBA YANG SEBELUMNYA GAGAL AKAN SECARA OTOMATIS DIRESET MENJADI 0
                nextUser.setModifiedBy(intUserId);
                nextUser.setModifiedDate(new Date());
            }
            else
            {
                return new ResponseHandler().generateResponse(ConstantMessageUser.ERROR_USER_NOT_EXISTS,
                        HttpStatus.NOT_ACCEPTABLE,null,"FV05008",request);
            }
        }

        catch (Exception e)
        {
            strExceptionArr[1]="doLogin(Userz userz,WebRequest request)  --- LINE 132";
            LoggingFile.exceptionStringz(strExceptionArr,e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse(ConstantMessageUser.ERROR_LOGIN_FAILED,
                    HttpStatus.INTERNAL_SERVER_ERROR,null,"FE05003",request);
        }

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("token",authManager(nextUser,request));
        /*
            Transform dari List Menu Header
         */
        List<Menu> listMenu = nextUser.getAkses().getListMenuAkses();
        int intListMenuSize = listMenu.size();
        StringBuilder sBuild = new StringBuilder();
        String strAppend = "";
        String strInisial = "";
        String strDelimiterObj = "A20Y";
        String strDelimiterField = "X56C";
        String strArrObject  [] ;
        String strArrOField  [] ;
        String strNamaMenuHeader = "";
        mapTemp.clear();
        if(!listMenu.isEmpty())
        {
            for(int i=0;i<intListMenuSize;i++)
            {
                strAppend = "";
                strInisial = "";
                MenuHeader menuHeader = listMenu.get(i).getMenuHeader();
                strNamaMenuHeader = menuHeader.getNamaMenuHeader();
                if(mapTemp.get(strNamaMenuHeader)!=null)
                {
                    sBuild.setLength(0);
                    strInisial = sBuild.append(mapTemp.get(strNamaMenuHeader)).toString();
                }

                sBuild.setLength(0);
                strAppend = sBuild.append(strInisial).
                        append(listMenu.get(i).getNamaMenu()).
                        append(strDelimiterField).
                        append(listMenu.get(i).getPathMenu()).toString();
                if(!(i==(intListMenuSize-1)))
                {
                    sBuild.setLength(0);
                    strAppend = sBuild.append(strAppend).append(strDelimiterObj).
                            toString();
                }
                mapTemp.put(strNamaMenuHeader,strAppend);
            }

            List<MenuCSRDTO> lm = null;
            lObj.clear();
            for (Map.Entry<String,Object> strMap:
                    mapTemp.entrySet()) {
                lm = new ArrayList<>();
                strArrObject = strMap.getValue().toString().split(strDelimiterObj);
                for(int j=0;j<strArrObject.length;j++)
                {
                    strArrOField = strArrObject[j].split(strDelimiterField);
                    MenuCSRDTO m = new MenuCSRDTO();
                    m.setNamaMenu(strArrOField[0]);
                    m.setPathMenu(strArrOField[1]);
                    lm.add(m);
                }
                mapMenu = new HashMap<>();
                mapMenu.put("group",strMap.getKey());
                mapMenu.put("subMenu",lm);
                lObj.add(mapMenu);
            }
        }
        map.put("menu",lObj);

        return new ResponseHandler().generateResponse(ConstantMessageUser.SUCCESS_LOGIN,
                HttpStatus.OK,map,null,request);
    }

    @Override
    public ResponseEntity<Object> save(User user, HttpServletRequest request) {return null;}

    @Override
    public ResponseEntity<Object> saveBatch(List<User> lt, HttpServletRequest request) {return null;}

    @Override
    public ResponseEntity<Object> edit(Long id, User user, HttpServletRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<Object> delete(Long id, HttpServletRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<Object> findById(Long id, HttpServletRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<Object> find(Pageable pageable, String columFirst, String valueFirst, HttpServletRequest request) { return null;}


    @Override
    public ResponseEntity<Object> dataToExport(MultipartFile multipartFile, HttpServletRequest request) {
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        String [] strArr = userName.split(OtherConfig.getFlagSplitToken());
        if(strArr.length==2)
        {
            /*
                berlaku jika fungsi yang memanggil sebelumnya sudah ada pengecekan ke database
                sehingga tidak 2x proses ke database
                mengurangi trafic untuk koneksi ke database
             */
            return new org.springframework.security.core.userdetails.
                    User(strArr[0],strArr[1],new ArrayList<>());
        }


        /*
            WARNING !!
            username yang ada di parameter otomatis hanya username , bukan string yang di kombinasi dengan password atau informasi lainnya...
            userName yang ada di parameter belum tentu adalah username...
            karena sistem memperbolehkan login dengan email, nohp ataupun username
         */
        Optional<User> opUser = userRepo.findByUsernameOrNoHPOrEmailAndIsActive(userName,userName,userName,true);
        if(opUser.isEmpty())
        {
            return null;
        }
        User userNext = opUser.get();
         /*
            PARAMETER KE 3 TIDAK MENGGUNAKAN ROLE DARI SPRINGSECURITY CORE
            ROLE MODEL AKAN DITAMBAHKAN DI METHOD authManager DAN DIJADIKAN INFORMASI DI DALAM JWT
         */
        return new org.springframework.security.core.userdetails.
                User(userNext.getUsername(),userNext.getPassword(),new ArrayList<>());
    }

    public String authManager(User user, HttpServletRequest request)//RANGE 006-010
    {
        /* Untuk memasukkan user ke dalam */
        sBuild.setLength(0);
        String strUserDetails = sBuild.append(user.getUsername()).append(OtherConfig.getFlagSplitToken()).append(user.getPassword()).toString();
        UserDetails userDetails = loadUserByUsername(strUserDetails);
        if(userDetails==null)
        {
            return "FAILED";
        }

        List<Menu> lMenu = user.getAkses().getListMenuAkses();
        /* Isi apapun yang perlu diisi ke dalam object mapz !! */
        mapz.put("uid",user.getIdUser());
        mapz.put("ml",user.getEmail());//5-6-10
        mapz.put("pn",user.getNoHP());//5-6-10
        mapz.put("nl",user.getNamaLengkap());
        String strAppendMenu = "";
        sBuild.setLength(0);
        if(lMenu.size()!=0)
        {
            for(int i=0;i<lMenu.size();i++)
            {
                sBuild.append(lMenu.get(i).getIdMenu()).append("-");
            }
            strAppendMenu = sBuild.toString();
            strAppendMenu = strAppendMenu.substring(0,strAppendMenu.length()-1);
            mapz.put("ln",strAppendMenu);//5-6-10
        }else {
            mapz.put("ln","0");//default semisal akses belum di set apa2 ke menu
        }
//        Optional<User> usr = userRepo.findByUsername(user.getUsername());
//        User usrNext = usr.get();
        String token = jwtUtility.generateToken(userDetails,mapz);

        return token;
    }

    private Page<User> getDataByValue(Pageable pageable, String columnFirst, String valueFirst)
    {
        if(valueFirst.equals("") || valueFirst==null)
        {
            return userRepo.findByIsActive(pageable,true);
        }
        if(columnFirst.equals("id"))
        {
            return userRepo.findByIsActiveAndIdUser(pageable,true,Long.parseLong(valueFirst));
        } else if (columnFirst.equals("nama")) {
            return userRepo.findByIsActiveAndNamaLengkapContainsIgnoreCase(pageable,true,valueFirst);
        }
        return userRepo.findByIsActive(pageable,true);// ini default kalau parameter search nya tidak sesuai--- asumsi nya di hit bukan dari web
    }

}