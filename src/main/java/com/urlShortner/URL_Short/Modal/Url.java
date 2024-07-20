package com.urlShortner.URL_Short.Modal;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;

import java.time.LocalDateTime;

//table that you will persist in your database
@Entity
@Data


public class Url {

    @Id
    @GeneratedValue
    private int id;


    // long url
    //The @Lob annotation is used to specify that the currently annotated entity attribute represents a large object type. LOB or Large OBject refers to a variable-length datatype for storing large objects

    @Lob
    private String originalUrl;



    private String shortLink;


    private LocalDateTime creationDate;
    private LocalDateTime expirationDate;




}
