package com.babu.cloudbox.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name="metadata")
public class Metadata {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name="filename")
    private String filename;

    @Column(name="description")
    private String description;

    @Column(name="folder")
    private String folder;

    @Column(name="firstname")
    private String firstname;

    @Column(name="lastname")
    private String lastname;

    @Column(name="uploaddate")
    private Date uploaddate;

    private Date updateddate;
}
