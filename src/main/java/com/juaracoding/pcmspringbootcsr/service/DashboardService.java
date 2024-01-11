package com.juaracoding.pcmspringbootcsr.service;/*
Created by IntelliJ IDEA 2022.2.3 (Community Edition)
Build #IC-232.10072.27, built on October 11, 2023
@Author LENOVO a.k.a. Virdha Haniva Pratiwie Ishak
Java Developer
Created on 05/01/2024 19:25
@Last Modified 05/01/2024 19:25
Version 1.0
*/

import com.juaracoding.pcmspringbootcsr.configuration.OtherConfig;
import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageGlobal;
import com.juaracoding.pcmspringbootcsr.core.security.ModulAuthority;
import com.juaracoding.pcmspringbootcsr.dto.SearchParamDTO;
import com.juaracoding.pcmspringbootcsr.dto.matapelajaran.MataPelajaranDTO;
import com.juaracoding.pcmspringbootcsr.dto.matapelajaran.MataPelajaranOptionDTO;
import com.juaracoding.pcmspringbootcsr.handler.ResponseHandler;
import com.juaracoding.pcmspringbootcsr.model.MataPelajaran;
import com.juaracoding.pcmspringbootcsr.model.Siswa;
import com.juaracoding.pcmspringbootcsr.repo.MataPelajaranRepo;
import com.juaracoding.pcmspringbootcsr.repo.SiswaRepo;
import com.juaracoding.pcmspringbootcsr.util.PdfGeneratorLibre;
import com.juaracoding.pcmspringbootcsr.util.TransformToDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/*
    modul code = 23
 */
@Service
@Transactional
public class DashboardService {

    private SiswaRepo siswaRepo;
    private MataPelajaranRepo mataPelajaranRepo;

    private String[] strExceptionArr = new String[2];
    @Autowired
    private ModelMapper modelMapper;
    private Map<String,Object> objectMapper = new HashMap<String,Object>();
    private TransformToDTO transformToDTO = new TransformToDTO();
    private List<SearchParamDTO> listSearchParamDTO  = new ArrayList<>();
    private Map<String,Object> mapToken = new HashMap<String,Object>();
    private List<MataPelajaranOptionDTO> ltMataPelajaranOptDTO = null;
    @Autowired
    private ModulAuthority modulAuthority;
    List<MataPelajaranDTO> listMataPelajaranDTO = null;
    Map<String,Object> mapResult = null;
    private String authorizationCode = "21";
    @Autowired
    public DashboardService(SiswaRepo siswaRepo, MataPelajaranRepo mataPelajaranRepo) {
        mapColumn();
        this.siswaRepo = siswaRepo;
        this.mataPelajaranRepo = mataPelajaranRepo;
    }

    private void mapColumn()
    {
        listSearchParamDTO.add(new SearchParamDTO("id","ID"));
        listSearchParamDTO.add(new SearchParamDTO("mapel","MAPEL"));
    }

    public ResponseEntity<Object> getDashboard(HttpServletRequest request)
    {
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
        Object objNoHp = mapToken.get("pn");
        String noHp = objNoHp==null?"":objNoHp.toString();
        String strAkses = "Siswa";
        List<MataPelajaran> listMapel = null;
        Optional<Siswa> optionalSiswa = siswaRepo.findByIsActiveAndNoHp(true,noHp);
        if(optionalSiswa.isEmpty()){
            listMapel = mataPelajaranRepo.findByNoHpTutor(noHp,true);
            strAkses = "Pengajar";
            if(listMapel.size()==0){
                return new ResponseHandler().
                        generateResponse(ConstantMessageGlobal.WARNING_DATA_EMPTY,
                                HttpStatus.NOT_FOUND,
                                null,
                                "FV23052",
                                request);
            }
        }
        else {
            listMapel = optionalSiswa.get().getListMataPelajaran();
        }
        listMataPelajaranDTO = modelMapper.map(listMapel, new TypeToken<List<MataPelajaranDTO>>() {}.getType());
        objectMapper.put("content",listMataPelajaranDTO);
        objectMapper.put("namaxx",mapToken.get("nl").toString());
        objectMapper.put("akzez",strAkses);
        objectMapper.put("totalItems",100);
        objectMapper.put("totalPages",1);
        objectMapper.put("sort","id");
        objectMapper.put("numberOfElements",100);
        objectMapper.put("searchParam",listSearchParamDTO);
        objectMapper.put("columnFirst","id");
        objectMapper.put("valueFirst","");

        return new ResponseHandler().
                generateResponse(ConstantMessageGlobal.SUCCESS_FIND_BY,
                        HttpStatus.OK,
                        objectMapper,
                        null,
                        request);
    }

    public String generateReport(HttpServletRequest request, HttpServletResponse response)
    {
        mapToken = modulAuthority.checkAuthorization(request,authorizationCode);//ambil userid dari token
        Object objNoHp = mapToken.get("pn");
        String noHp = objNoHp==null?"":objNoHp.toString();
        String strAkses = "Siswa";
        List<MataPelajaran> listMapel = null;
        Optional<Siswa> optionalSiswa = siswaRepo.findByIsActiveAndNoHp(true,noHp);
        if(optionalSiswa.isEmpty()){
            listMapel = mataPelajaranRepo.findByNoHpTutor(noHp,true);
            strAkses = "Pengajar";
            if(listMapel.size()==0){
                return "Data tidak Ada !!";
            }
        }
        else {
            listMapel = optionalSiswa.get().getListMataPelajaran();
        }
/*
                START PROCESSING REPORT
             */
        response.setContentType("application/pdf");
        DateFormat dateFormat = new SimpleDateFormat("YYYYMMDDHHMMSSsss");
        String currentDateTime = dateFormat.format(new Date());
        String headerkey = "Content-Disposition";
        StringBuilder sBuild = new StringBuilder();
        sBuild.setLength(0);
        String headervalue = sBuild.append(OtherConfig.getHowToDownloadReport()).append("; filename=dashboardList-").
                append(currentDateTime).append(".pdf").toString();
        response.setHeader(headerkey, headervalue);

        Object objMapel = "";
        Object objKode = "";
        Object objKelas = "";
        Object objBahasa = "";
        Object objTutor = "";
        String [][] strBody = null;
        PdfGeneratorLibre generator = null;
        generator = new PdfGeneratorLibre();
        int intStrHeader=6;// INI YANG DIRUBAH SESUAIKAN DENGAN JUMLAH KOLOM
        String[] strKolom = new String[intStrHeader];
        /*
            DEFINISIKAN KOLOM NYA DISINI HARUS SESUAI JUMLAH NYA DENGAN HEADER YANG DI SET DI VARIABEL intStrHeader
         */
        strKolom[0] ="ID";
        strKolom[1] ="MAPEL";
        strKolom[2] ="KODE";
        strKolom[3] ="KELAS";
        strKolom[3] ="BAHASA";
        strKolom[4] ="TUTOR";
        int intlistMapel = listMapel.size();
        strBody = new String[intlistMapel][intStrHeader];

        for(int i=0;i<listMapel.size();i++)
        {
            /*
                INI KALIAN MAPPING TAPI HATI2 DENGAN OBJECT, HARUS DI HANDLE NULL NYA
             */
            objMapel = listMapel.get(i).getNamaMataPelajaran();
            objKode = listMapel.get(i).getKodeMataPelajaran();
            objKelas = listMapel.get(i).getKodeKelas();
            objBahasa = listMapel.get(i).getBahasa().getNamaBahasa();
            objTutor = listMapel.get(i).getTutor().getNamaTutor();
            strBody[i][0] = String.valueOf(listMapel.get(i).getIdMataPelajaran());
            strBody[i][1] = objMapel==null?"-":objMapel.toString();
            strBody[i][2] = objKode==null?"-":objKode.toString();
            strBody[i][3] = objKelas==null?"-":objKelas.toString();
            strBody[i][4] = objBahasa==null?"-":objBahasa.toString();
            strBody[i][5] = objTutor==null?"-":objTutor.toString();
        }

        sBuild.setLength(0);
        generator.generate(sBuild.
                append("LIST DASHBOARD \n").//JUDUL REPORT
                        append("total data : ").append(intlistMapel).//VARIABEL TOTAL DATA
                        toString(),strKolom,strBody, response,"1");
            /*
                END PROCESSING REPORT
            */


        return"OK";
    }




}

