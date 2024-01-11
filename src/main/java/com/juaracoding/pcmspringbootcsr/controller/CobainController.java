package com.juaracoding.pcmspringbootcsr.controller;


import com.juaracoding.pcmspringbootcsr.util.FileUtility;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/cobain")
public class CobainController {
    @PostMapping("/uploadimage")
    public Object submitFile(@RequestParam(value = "kiriman") MultipartFile file,
                             HttpServletResponse response) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String uploadDir = "C:/";
        FileUtility.saveFile(uploadDir, fileName, file);

//        response.setContentType("image/jpeg");
//        response.setHeader("Content-Disposition", "attachment; filename=\"test.pdf\"");
//        InputStreamResource resource = new InputStreamResource(new FileInputStream("C:\\test-file.pdf"));
//        return resource;

        return "";

//        return "Berhasil mengunggah file " + fileName;
    }

    @GetMapping("/welcome")
    public String hello(){
//        StringBuilder sBuild = new StringBuilder();
//        try {
//            ReadTextFileSB rtfSB = new ReadTextFileSB(sBuild.append(OtherConfig.getOsSplitting()).append("data").
//                    append(OtherConfig.getOsSplitting()).append("ver_lupa_pwd.html").toString());
//            System.out.println("STRING => "+rtfSB.getContentFile());
//            System.out.println("BYTE FILE => "+rtfSB.getByteOfFile());
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
        return "Hello";
    }



}