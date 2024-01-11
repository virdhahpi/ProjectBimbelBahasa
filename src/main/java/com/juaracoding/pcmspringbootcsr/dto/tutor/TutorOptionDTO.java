package com.juaracoding.pcmspringbootcsr.dto.tutor;/*
Created by IntelliJ IDEA 2022.2.3 (Community Edition)
Build #IC-232.10072.27, built on October 11, 2023
@Author LENOVO a.k.a. Virdha Haniva Pratiwie Ishak
Java Developer
Created on 03/01/2024 23:12
@Last Modified 03/01/2024 23:12
Version 1.0
*/

public class TutorOptionDTO {
    private Long idTutor;
    private String namaTutor;

    public Long getIdTutor() {
        return idTutor;
    }

    public void setIdTutor(Long idTutor) {
        this.idTutor = idTutor;
    }

    public String getNamaTutor() {
        return namaTutor;
    }

    public void setNamaTutor(String namaTutor) {
        this.namaTutor = namaTutor;
    }
}

