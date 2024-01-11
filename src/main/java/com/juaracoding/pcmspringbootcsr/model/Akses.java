package com.juaracoding.pcmspringbootcsr.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/*
    Modul Code = 04
 */
@Entity
@Table(name = "MstAkses")
public class Akses implements Serializable {

    private static final long serialversionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDAkses")
    private Long idAkses;

    @Column(name = "NamaAkses")
    private String namaAkses;

    @ManyToMany
    @JoinTable(name = "MapAksesMenu",
            joinColumns = {
                    @JoinColumn(name = "IDAkses",referencedColumnName = "IDAkses")}, inverseJoinColumns = {
            @JoinColumn (name = "IDMenu",referencedColumnName = "IDMenu")}, uniqueConstraints = @UniqueConstraint(columnNames = {
            "IDAkses", "IDMenu" }))
    private List<Menu> listMenuAkses;

    @ManyToOne
    @JoinColumn(name = "IDDivisi")
    private Divisi divisi;

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

    public Long getIdAkses() {
        return idAkses;
    }

    public void setIdAkses(Long idAkses) {
        this.idAkses = idAkses;
    }

    public String getNamaAkses() {
        return namaAkses;
    }

    public void setNamaAkses(String namaAkses) {
        this.namaAkses = namaAkses;
    }

    public List<Menu> getListMenuAkses() {
        return listMenuAkses;
    }

    public void setListMenuAkses(List<Menu> listMenuAkses) {
        this.listMenuAkses = listMenuAkses;
    }

    public Divisi getDivisi() {
        return divisi;
    }

    public void setDivisi(Divisi divisi) {
        this.divisi = divisi;
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
