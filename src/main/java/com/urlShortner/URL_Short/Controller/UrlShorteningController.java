package com.urlShortner.URL_Short.Controller;


import com.urlShortner.URL_Short.Modal.Url;
import com.urlShortner.URL_Short.Modal.UrlDto;
import com.urlShortner.URL_Short.Modal.UrlErrorResponseDto;
import com.urlShortner.URL_Short.Modal.UrlResponseDto;
import com.urlShortner.URL_Short.Service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;

@RestController
public class UrlShorteningController {



    @Autowired
    private UrlService urlService;
//    private HttpServletResponse response;


    @PostMapping("/generate")
    public ResponseEntity<?> generateShortLink(@RequestBody UrlDto urlDto){

try{
        //either return a null object or return created a shortlink
        Url urlToRet =urlService.generateShortLink(urlDto);

        if(urlToRet!=null){
            //Create response and return to user

            UrlResponseDto urlResponseDto =new UrlResponseDto();
            urlResponseDto.setOriginalUrl(urlToRet.getOriginalUrl());
            urlResponseDto.setExpirationDate(urlToRet.getExpirationDate());
            urlResponseDto.setShortLink(urlToRet.getShortLink());
            return new ResponseEntity<UrlResponseDto>(urlResponseDto, HttpStatus.OK);

        }
        //if it is null then set urlerrorresponsedto

        UrlErrorResponseDto urlErrorResponseDto=new UrlErrorResponseDto();
        urlErrorResponseDto.setStatus("404");
        urlErrorResponseDto.setError("ERROR PROCESSING YOUR REQUEST!!PLEASE TRY AGAIN");

        return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto,HttpStatus.NOT_FOUND);
    }catch (RuntimeException e) {
    // This is the new part to handle custom short link conflicts
    UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
    urlErrorResponseDto.setStatus("400");
    urlErrorResponseDto.setError(e.getMessage());
    return new ResponseEntity<>(urlErrorResponseDto, HttpStatus.BAD_REQUEST);
}
    }




    @GetMapping("/{shortLink}")
    public ResponseEntity<?> redirectToOriginalUrl(@PathVariable String shortLink) {


        System.out.println("Received request for short link: " + shortLink);


        if (!shortLink.matches("^[a-zA-Z0-9]{1,50}$")) {
            UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
            urlErrorResponseDto.setError("Invalid short link format");
            urlErrorResponseDto.setStatus("400");
            return new ResponseEntity<>(urlErrorResponseDto, HttpStatus.BAD_REQUEST);
        }

        // with the help of shortlINK WE REDIRECT THE USER TO ORG UTL


        //verify the shortlink in database or not

        if(StringUtils.isEmpty(shortLink)){

            UrlErrorResponseDto urlErrorResponseDto=new UrlErrorResponseDto();
            urlErrorResponseDto.setError("Invalid URL");
            urlErrorResponseDto.setStatus("400");
            return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto,HttpStatus.BAD_REQUEST);

        }
//        System.out.println("one");

        //get the hashed url form database
        Url urlToRet =urlService.getEncodedUrl(shortLink);
        if(urlToRet==null){
            UrlErrorResponseDto urlErrorResponseDto=new UrlErrorResponseDto();
            urlErrorResponseDto.setError("URL doesnot exist or might have expired");
            urlErrorResponseDto.setStatus("404");
            return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto,HttpStatus.NOT_FOUND);
        }
//        System.out.println("two");





        //if url exist in database and also look out for the expiration link ...comapre wiht UTC time
        LocalDateTime now = LocalDateTime.now();
        if(urlToRet.getExpirationDate().isBefore(now)){
            //url is expired and user will go ahead and generate new fresh url

// cleanUp ..also delete from database if expired
            urlService.deleteShortLink(urlToRet);


            UrlErrorResponseDto urlErrorResponseDto=new UrlErrorResponseDto();
            urlErrorResponseDto.setError("URL expired. Please try generating fresh one  ");
            urlErrorResponseDto.setStatus("410");
            return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto,HttpStatus.GONE);

        }

//        System.out.println("Expiration Date: " + urlToRet.getExpirationDate());
//        System.out.println("Current Time: " + LocalDateTime.now());

        //if shortlink not expired then set it to header

//redirect to original url
//        response.sendRedirect(urlToRet.getOriginalUrl());
        //ALTERNATE METHOD with restful practices with spring responseENtity.It allows the cliend typically a web browser to handle the redirect
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(urlToRet.getOriginalUrl()))
                .build();



//        return null;


    }


}
