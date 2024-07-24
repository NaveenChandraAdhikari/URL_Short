package com.urlShortner.URL_Short.Controller;


import com.urlShortner.URL_Short.Modal.Url;
import com.urlShortner.URL_Short.Modal.UrlDto;
import com.urlShortner.URL_Short.Service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

@Controller
public class UrlShorteningController {


    @Autowired
    private UrlService urlService;


    @GetMapping("/")
    public String home() {
        return "generate";
    }

    @PostMapping("/generate")
    public String generateShortLink(
            @RequestParam("url") String url,
            @RequestParam(value = "expirationDate", required = false) String expirationDate,
            RedirectAttributes redirectAttributes)
    {

        if (url.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "URL CANNOT BE EMPTY!!");
            return "redirect:/result";
        }

        UrlDto urlDto = new UrlDto();
        urlDto.setUrl(url);
        urlDto.setExpirationDate(expirationDate);

        Url urlToRet = urlService.generateShortLink(urlDto);

        if (urlToRet != null) {
            // To dynamically generate short link without hardcoding 'localhost:8080'
            // or whatever the port number is set to
            String shortLink = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/{shortLink}")
                    .buildAndExpand(urlToRet.getShortLink())
                    .toUriString();
//            redirectAttributes.addFlashAttribute("shortLink", "http://localhost:8080/" + urlToRet.getShortLink());
            redirectAttributes.addFlashAttribute("shortLink", shortLink);
            return "redirect:/result";
        } else {
            redirectAttributes.addFlashAttribute("error", "ERROR PROCESSING YOUR REQUEST!! PLEASE TRY AGAIN");
            return "redirect:/result";
        }
    }

    @GetMapping("/result")
    public String result(Model model) {
        return "generate";
    }

    @GetMapping("/{shortLink}")
    public String redirectToOriginalUrl(
            @PathVariable String shortLink,
            Model model)
    {

        if (shortLink == null || shortLink.isEmpty()) {
            model.addAttribute("error", "Invalid URL");
            return "error";
        }

        Url urlToRet = urlService.getEncodedUrl(shortLink);
        if (urlToRet == null) {
            model.addAttribute("error", "URL does not exist or might have expired");
            return "error";
        }

        LocalDateTime now = LocalDateTime.now();
        if (urlToRet.getExpirationDate().isBefore(now)) {
            urlService.deleteShortLink(urlToRet);
            model.addAttribute("error", "URL expired. Please try generating a fresh one");
            return "error";
        }

        return "redirect:" + urlToRet.getOriginalUrl();
    }

    /* @PostMapping("/generate")
    public ResponseEntity<?> generateShortLink(@RequestBody UrlDto urlDto){


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
    } */

    /* @GetMapping("/{shortLink}")
    public ResponseEntity<?> redirectToOriginalUrl(@PathVariable String shortLink){

        // with the help of shortlINK WE REDIRECT THE USER TO ORG UTL


        //verify the shortlink in database or not

        if(StringUtils.isEmpty(shortLink)){

            UrlErrorResponseDto urlErrorResponseDto=new UrlErrorResponseDto();
            urlErrorResponseDto.setError("Invalid URL");
            urlErrorResponseDto.setStatus("400");
            return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto,HttpStatus.BAD_REQUEST);

        }

        //get the hashed url form database
        Url urlToRet =urlService.getEncodedUrl(shortLink);
        if(urlToRet==null){
            UrlErrorResponseDto urlErrorResponseDto=new UrlErrorResponseDto();
            urlErrorResponseDto.setError("URL doesnot exist or might have expired");
            urlErrorResponseDto.setStatus("400");
            return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto,HttpStatus.BAD_REQUEST);
        }

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

        System.out.println("Expiration Date: " + urlToRet.getExpirationDate());
        System.out.println("Current Time: " + LocalDateTime.now());

        //if shortlink not expired then set it to header

//redirect to original url
//        response.sendRedirect(urlToRet.getOriginalLink());
        //ALTERNATE METHOD with restful practices with spring responseENtity.It allows the cliend typically a web browser to handle the redirect
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(urlToRet.getOriginalUrl()))
                .build();
//        return null;


    } */


}
