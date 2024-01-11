package com.juaracoding.pcmspringbootcsr.util;


import com.juaracoding.pcmspringbootcsr.model.Divisi;
import com.juaracoding.pcmspringbootcsr.model.MenuHeader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvReader {


    public static boolean isCsv(MultipartFile multipartFile)
    {
        if(!"text/csv".equals(multipartFile.getContentType()))
        {
            return false;
        }
        return true;
    }

    public List<Divisi> csvToDivisiData(InputStream inputStream) throws Exception {
        /*
            ketentuan pertama
         */
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        CSVParser csvParser = new CSVParser(bufferedReader,
                CSVFormat.DEFAULT.//delimiter nya harus comma, untuke pengecekan buka file csv dengan notepad
                        withFirstRecordAsHeader().
                        withIgnoreHeaderCase().//mengabaikan huruf besar & kecil di header kolom
                        withTrim()
        );
        List<Divisi> listDivisi = new ArrayList<Divisi>();
        try {

            Iterable<CSVRecord> iterRecords = csvParser.getRecords();
            Divisi divisi;
            for (CSVRecord record : iterRecords) {
                divisi = new Divisi();
                divisi.setNamaDivisi(record.get("NamaDivisi"));
                divisi.setDeskripsiDivisi(record.get("DeskripsiDivisi"));
                divisi.setKodeDivisi(record.get("KodeDivisi"));
                listDivisi.add(divisi);
            }

        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        } finally {
            if (!csvParser.isClosed()) {
                csvParser.close();
            }
            return listDivisi;
        }
    }
    public List<MenuHeader> csvToMenuHeaderData(InputStream inputStream) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        CSVParser csvParser = new CSVParser(bufferedReader,
                CSVFormat.DEFAULT.withFirstRecordAsHeader().
                        withIgnoreHeaderCase().
                        withTrim()
        );
        List<MenuHeader> listMenuHeader = new ArrayList<MenuHeader>();
        try {

            Iterable<CSVRecord> iterRecords = csvParser.getRecords();
            MenuHeader menuHeader;
            for (CSVRecord record : iterRecords) {
                menuHeader = new MenuHeader();
                menuHeader.setNamaMenuHeader(record.get("NamaMenuHeader"));
                menuHeader.setDeskripsiMenuHeader(record.get("DeskripsiMenuHeader"));
                listMenuHeader.add(menuHeader);
            }
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        } finally {

            if (!csvParser.isClosed()) {
                csvParser.close();
            }
            return listMenuHeader;
        }
    }
}