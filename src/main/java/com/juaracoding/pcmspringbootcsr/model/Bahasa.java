package com.juaracoding.pcmspringbootcsr.model;/*
Created by IntelliJ IDEA 2022.2.3 (Community Edition)
Build #IC-232.10072.27, built on October 11, 2023
@Author LENOVO a.k.a. Virdha Haniva Pratiwie Ishak
Java Developer
Created on 02/01/2024 13:22
@Last Modified 02/01/2024 13:22
Version 1.0
*/

import javax.persistence.*;
import java.util.Date;

/*
    Modul Code = 11
 */

@Entity
@Table(name = "MstBahasa")
public class Bahasa{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDBahasa")
    private Long idBahasa;

    @Column(name = "NamaBahasa")
    private String namaBahasa;

    @Column(name = "KodeBahasa", unique = true)
    private String kodeBahasa;

    /*
         start audit trails
      */
    @Column(name ="CreatedDate" , nullable = false)
    private Date createdDate = new Date();

    @Column(name = "CreatedBy", nullable = false)
    private Integer createdBy=1;

    @Column(name = "ModifiedDate")
    private Date modifiedDate;
    @Column(name = "ModifiedBy")
    private Integer modifiedBy;

    @Column(name = "IsActive", nullable = false)
    private Boolean isActive = true;
    /*
        end audit trails
     */

    public Long getIdBahasa() {
        return idBahasa;
    }

    public void setIdBahasa(Long idBahasa) {
        this.idBahasa = idBahasa;
    }

    public String getNamaBahasa() {
        return namaBahasa;
    }

    public void setNamaBahasa(String namaBahasa) {
        this.namaBahasa = namaBahasa;
    }

    public String getKodeBahasa() {
        return kodeBahasa;
    }

    public void setKodeBahasa(String kodeBahasa) {
        this.kodeBahasa = kodeBahasa;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Integer getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Integer modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}

