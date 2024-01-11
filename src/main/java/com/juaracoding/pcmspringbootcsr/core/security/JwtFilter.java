package com.juaracoding.pcmspringbootcsr.core.security;

import com.juaracoding.pcmspringbootcsr.configuration.MyHttpServletRequestWrapper;
import com.juaracoding.pcmspringbootcsr.configuration.OtherConfig;
import com.juaracoding.pcmspringbootcsr.handler.RequestCapture;
import com.juaracoding.pcmspringbootcsr.service.UserService;
import com.juaracoding.pcmspringbootcsr.util.LoggingFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtility jwtUtility;

    @Autowired
    private UserService userService;

    private String [] strExceptionArr = new String[2];
    public JwtFilter() {
        strExceptionArr[0] = "JwtFilter";
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        authorization = authorization == null ? "": authorization;
        String token = null;
        String userName = null;
        try{

            /*
                validasi khusus memilah request dengan content type application/json
             */
            String strContentType = request.getContentType()==null?"":request.getContentType();
            if(!strContentType.startsWith("multipart/form-data") || "".equals(strContentType)){
                request = new MyHttpServletRequestWrapper(request);
            }
            /*
                Langkah pertama otentikasi token
            */
            if(!"".equals(authorization) && authorization.startsWith("Bearer ") && authorization.length()>7)
            {
                token = authorization.substring(7);//memotong setelah kata Bearer+spasi = 7 digit
//                Crypto.performDecrypt(token);
                userName = jwtUtility.getUsernameFromToken(token);

                if(userName != null && SecurityContextHolder.getContext().getAuthentication()==null)
                {
                    if(jwtUtility.validateToken(token))
                    {
                        final UserDetails userDetails = new User(userName,"",new ArrayList<>());
                        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                                userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            strExceptionArr[1] = "oFilterInternal(HttpServletRequest request, HttpServletResponse response,FilterChain filterChain) "+ RequestCapture.allRequest(request);
            LoggingFile.exceptionStringz(strExceptionArr, ex, OtherConfig.getFlagLoging());
        }
        filterChain.doFilter(request,response);
    }
}