package com.juaracoding.pcmspringbootcsr.util;

import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LogTable {

    public static void inputLogRequest(String[] datax,Exception e, String flag)
    {
        if(flag.equalsIgnoreCase("y"))
        {
            Map<String , Object> mapz = new HashMap<String,Object>();
            mapz.put("className",datax[0]);
            mapz.put("dataRequest",datax[1]);
            mapz.put("createdBy",1L);
            mapz.put("errorMessagez",e.getMessage());

            try{
                URL url = new URL ("http://localhost:8082/api/log/v1/yes");
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);
                String jsonInputString = new JSONObject(mapz).toString();
                try(OutputStream os = con.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                }
            }catch (Exception ex)
            {
                System.out.println(ex.getMessage());
            }
        }
    }
}