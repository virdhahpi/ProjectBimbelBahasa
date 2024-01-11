package com.juaracoding.pcmspringbootcsr.controller;

import com.juaracoding.pcmspringbootcsr.dto.regis.ForgetPasswordDTO;
import com.juaracoding.pcmspringbootcsr.dto.regis.LoginDTO;
import com.juaracoding.pcmspringbootcsr.dto.regis.RegisDTO;
import com.juaracoding.pcmspringbootcsr.dto.regis.VerifyTokenDTO;
import com.juaracoding.pcmspringbootcsr.model.User;
import com.juaracoding.pcmspringbootcsr.service.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth01")
public class AuthController {
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;
    private String [] strExceptionArr = new String[2];

    @Autowired
    public AuthController(UserService userService) {
        strExceptionArr[0]="AuthController";
        this.userService = userService;
    }

    /*
        VALIDASI FORM REGISTRASI
        {
            "username" : "allysazahra12",
            "email" : "allysazramadhina@gmail.com",
            "noHp" : "628331616416",
            "tanggalLahir" : "1997-02-02",
            "password" : "Alysazahra@1234",
            "alamat" : "Jakarta"
        }
     */
    @PostMapping("/register/v1")
    public ResponseEntity<Object> doRegis(@Valid @RequestBody RegisDTO regisDTO
            , HttpServletRequest request
    )
    {
        User user = modelMapper.map(regisDTO,new TypeToken<User>(){}.getType());

            return userService.checkRegis(user,request);
    }

    /*
        {
            "email" : "allysazramadhina@gmail.com",
            "token" : "210539"
        }
     */
    @PostMapping("/register/v1/verify")
    public ResponseEntity<Object> verifyRegis(@RequestBody VerifyTokenDTO verifyTokenDTO,
                                              HttpServletRequest request)
    {
        User user = modelMapper.map(verifyTokenDTO,new TypeToken<User>(){}.getType());
        return userService.confirmRegis(user,request);
    }

    @PostMapping("/register/v1/newtoken")
    public ResponseEntity<Object> newTokenRegis(@RequestParam(value = "email") String strEmail,
                                                HttpServletRequest request)
    {
        return userService.getNewToken(strEmail,request);
    }

    /*
        {
            "username" : "628331616416",
            "password" : "Alysazahra@1234"
        }
     */
    @PostMapping("/login/v1")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginDTO loginDTO,
                                              HttpServletRequest request)
    {
        User user = modelMapper.map(loginDTO,new TypeToken<User>(){}.getType());
        return userService.doLogin(user,request);
    }
    @PostMapping("/forgotpwd/v1")
    public ResponseEntity<Object> forgotPassword(@RequestParam(value = "email") String strEmail,
                                                 HttpServletRequest request)
    {
        return userService.sendMailForgetPwd(strEmail,request);
    }
    @PostMapping("/forgotpwd/v1/verify")
    public ResponseEntity<Object> verifyForgotPassword(@Valid @RequestBody ForgetPasswordDTO forgetPasswordDTO,
                                                       HttpServletRequest request)
    {
        return userService.confirmTokenForgotPwd(forgetPasswordDTO,request);
    }
    @PostMapping("/forgotpwd/v1/confirm")
    public ResponseEntity<Object> confirmForgotPassword(@Valid @RequestBody ForgetPasswordDTO forgetPasswordDTO,
                                                        HttpServletRequest request)
    {
        return userService.confirmForgotPassword(forgetPasswordDTO,request);
    }
}