package com.juaracoding.pcmspringbootcsr.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransformToDTO {
    private String sortBy = "";
    private String sort = "";
    public Map<String,Object> transformObject(Map<String,Object> mapz, List ls, Page page)
    {
        mapz.put("content",ls);
        mapz.put("currentPage",page.getNumber());
        mapz.put("totalItems",page.getTotalElements());
        mapz.put("totalPages",page.getTotalPages());
        mapz.put("sort",page.getSort());
        mapz.put("numberOfElements",page.getNumberOfElements());

        return mapz;
    }
    public Map<String,Object> transformObject(Map<String,Object> mapz, List ls, Page page,List searchParams,
                                              String columnFirst,String valueFirst,Map<String,Object> mapOption)//<PENAMBAHAN 01-01-2024>
    {
        sortBy = page.getSort().toString().split(":")[0];
        sortBy = sortBy.equals("UNSORTED")?"id":sortBy;
        sort   = sortBy.equals("UNSORTED")?"asc":page.getSort().toString().split(":")[1];
        mapz.put("content",ls);
        mapz.put("totalItems",page.getTotalElements());
        mapz.put("totalPages",page.getTotalPages());
        mapz.put("sort",sort.trim().toLowerCase());
        mapz.put("numberOfElements",page.getNumberOfElements());
        mapz.put("searchParam",searchParams);
        for (Map.Entry<String,Object> strMap:
                mapOption.entrySet()) {
            mapz.put(strMap.getKey(),strMap.getValue());
        }
        mapz.put("columnFirst",columnFirst);
        mapz.put("valueFirst",valueFirst);

        return mapz;
    }
    public Map<String,Object> transformObject(Map<String,Object> mapz, List ls, Page page,List searchParams,List lg
            ,String columnFirst,String valueFirst)//<PENAMBAHAN 21-12-2023>
    {
        sortBy = page.getSort().toString().split(":")[0];
        sortBy = sortBy.equals("UNSORTED")?"id":sortBy;
        sort   = sortBy.equals("UNSORTED")?"asc":page.getSort().toString().split(":")[1];
        mapz.put("content",ls);
        mapz.put("totalItems",page.getTotalElements());
        mapz.put("totalPages",page.getTotalPages());
        mapz.put("sort",sort.trim().toLowerCase());
        mapz.put("numberOfElements",page.getNumberOfElements());
        mapz.put("searchParam",searchParams);
        mapz.put("listGroup",lg);
        mapz.put("columnFirst",columnFirst);
        mapz.put("valueFirst",valueFirst);

        return mapz;
    }

    /*
        untuk sementara list nya dibuat static, akan tetapi nanti dibuat menjadi map lalu di looping di function ini
     */
    public Map<String,Object> transformObject(Map<String,Object> mapz, List ls, Page page,List searchParams,
                                              String columnFirst,String valueFirst,List lg,List lm)//<PENAMBAHAN 21-12-2023>
    {
        sortBy = page.getSort().toString().split(":")[0];
        sortBy = sortBy.equals("UNSORTED")?"id":sortBy;
        sort   = sortBy.equals("UNSORTED")?"asc":page.getSort().toString().split(":")[1];
        mapz.put("content",ls);
        mapz.put("totalItems",page.getTotalElements());
        mapz.put("totalPages",page.getTotalPages());
        mapz.put("sort",sort.trim().toLowerCase());
        mapz.put("numberOfElements",page.getNumberOfElements());
        mapz.put("searchParam",searchParams);
        mapz.put("listGroupDivisi",lg);
        mapz.put("listGroupMenu",lm);
        mapz.put("columnFirst",columnFirst);
        mapz.put("valueFirst",valueFirst);

        return mapz;
    }

    public Map<String,Object> transformObject(Map<String,Object> mapz, List ls, Page page,Map<String,String> searchParams)//<PENAMBAHAN 07-03-2023>
    {
        sortBy = page.getSort().toString().split(":")[0];
        sortBy = sortBy.equals("UNSORTED")?"id":sortBy;
        sort   = sortBy.equals("UNSORTED")?"asc":page.getSort().toString().split(":")[1];
        mapz.put("content",ls);
        mapz.put("totalItems",page.getTotalElements());
        mapz.put("totalPages",page.getTotalPages());
        mapz.put("sort",sort.trim().toLowerCase());
        mapz.put("numberOfElements",page.getNumberOfElements());
        mapz.put("searchParam",searchParams);

        return mapz;
    }

    public Map<String,Object> transformObjectDataEmpty(Map<String,Object> mapz, Pageable pageable, Map<String,String> searchParams)//<PENAMBAHAN 07-03-2023>
    {
        sortBy = pageable.getSort().toString().split(":")[0];
        sort   = sortBy.equals("UNSORTED")?"asc":pageable.getSort().toString().split(":")[1];

        mapz.put("content",new ArrayList<>());
        mapz.put("totalItems",0);
        mapz.put("totalPages",0);
        mapz.put("sort",sort.trim().toLowerCase());
        mapz.put("numberOfElements",0);
        mapz.put("searchParam",searchParams);

        return mapz;
    }

    public Map<String,Object> transformObjectDataEmpty(Map<String,Object> mapz, Map<String,String> searchParams)//<PENAMBAHAN 07-03-2023>
    {
        mapz.put("content",new Object());
        mapz.put("totalItems",0);
        mapz.put("totalPages",0);
        mapz.put("sort","asc");
        mapz.put("numberOfElements",0);
        mapz.put("searchParam",searchParams);

        return mapz;
    }
    public Map<String,Object> transformObjectDataSave(Map<String,Object> mapz,Long idDataSave, Map<String,String> searchParams)//<PENAMBAHAN 07-03-2023>
    {
        mapz.put("content",new Object());
        mapz.put("totalItems",0);
        mapz.put("totalPages",0);
        mapz.put("sort","asc");
        mapz.put("idDataSave",idDataSave);
        mapz.put("numberOfElements",0);
        mapz.put("searchParam",searchParams);

        return mapz;
    }


}