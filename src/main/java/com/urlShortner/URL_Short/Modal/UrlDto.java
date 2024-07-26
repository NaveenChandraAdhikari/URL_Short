package com.urlShortner.URL_Short.Modal;
/*
A Data Transfer Object is a design pattern that allows for the exchange of data between software application subsystems or layers, often between the business logic layer and the presentation or persistence layer. The primary goal of DTOs is to reduce the number of method calls between these layers by aggregating data into a single object.

In a Spring Boot application, DTOs are commonly used to encapsulate data transferred between the controller and service layers, or between the service layer and the persistence layer. This segregation helps in maintaining a clear separation of concerns and improves the overall modularity of the codebase.

DTOs are used to transfer data between different parts of a program, often between the service layer and the controller layer, or between your application and the client (like a web browser).
In your case, UrlDTO is likely used to transfer the URL data from the user's request to your service layer where the URL shortening happens.

So, to put it simply:

UrlDTO is like a container or a envelope that carries the URL information from one part of your program to another.
It's called a "transfer" object because its job is to transfer data, not to process it or do anything with it.



 */

/*
UrlDto class:
This is like a form or a container for information about a URL that a user wants to shorten.

url: This is where you store the long URL that the user wants to make shorter.
expirationDatte: This is meant to be the date when the short URL should stop working
 */

import lombok.Data;

@Data
public class UrlDto {

    private String url;
    private String expirationDate; //optional
    private String customShortLink;


}
