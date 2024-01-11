package com.juaracoding.pcmspringbootcsr.core;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
public interface IService<T> {

    public ResponseEntity<Object> save(T t, HttpServletRequest request);//001-010
    public ResponseEntity<Object> saveBatch(List<T> lt, HttpServletRequest request);//011-020
    public ResponseEntity<Object> edit(Long id,T t,HttpServletRequest request);//021-030
    public ResponseEntity<Object> delete(Long id,HttpServletRequest request);//031-040//del
    public ResponseEntity<Object> findById(Long id, HttpServletRequest request);//041-050
    public ResponseEntity<Object> find(Pageable pageable, String columFirst, String valueFirst, HttpServletRequest request);//051-060
    public ResponseEntity<Object> dataToExport(MultipartFile multipartFile, HttpServletRequest request);//061-070
}