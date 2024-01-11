package com.juaracoding.pcmspringbootcsr.core.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Component
public class ModulAuthority {

    @Autowired
    private JwtUtility jwtUtility;
    private String strToken = "";

    public Map<String , Object> checkAuthorization(HttpServletRequest request){
        strToken = request.getHeader("Authorization").substring(7);
        return jwtUtility.getApiAuthorization(strToken,new HashMap<String,Object>());

    }
    public Map<String , Object> checkAuthorization(HttpServletRequest request,String modulCode){
        strToken = request.getHeader("Authorization").substring(7);
        return jwtUtility.getApiAuthorization(strToken,new HashMap<String,Object>(),modulCode);

    }
}