package com.urlShortner.URL_Short.Service;


import com.google.common.hash.Hashing;
import com.urlShortner.URL_Short.Modal.Url;
import com.urlShortner.URL_Short.Modal.UrlDto;
import com.urlShortner.URL_Short.Repository.UrlRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Component
public class UrlServiceImplementation implements UrlService
{


    @Autowired
    private UrlRepository urlRepository;
    @Override
    public Url generateShortLink(UrlDto urlDto) {

        if(StringUtils.isNotEmpty(urlDto.getUrl())){


            // this is where i got the bug and still not fixed
            String originalUrl =urlDto.getUrl();
            if(!originalUrl.startsWith("http://")&& !originalUrl.startsWith("https://")){
                originalUrl="https://"+originalUrl;
            }


            String shortLink;
            if (StringUtils.isNotEmpty(urlDto.getCustomShortLink())) {
                // Check if custom short link is available
                if (urlRepository.findByShortLink(urlDto.getCustomShortLink()) != null) {
                    throw new RuntimeException("Custom short link is already in use");
                }
                shortLink = urlDto.getCustomShortLink();
            } else {
                shortLink = encodeUrl(originalUrl);
            }



//            String encodedUrl=encodeUrl(originalUrl);

            //persist that short link to database
            //urlToPersist. Think of this as a container for all the information about the shortened URL.
            //It fills this container (urlToPersist) with information:
            //
            //When it was created
            //The original long URL
            //The new short link
            //When it will expire

            Url urlToPersist =new Url();
            urlToPersist.setCreationDate(LocalDateTime.now());
            urlToPersist.setOriginalUrl(originalUrl);
            urlToPersist.setShortLink(shortLink);

            //default expiration data..ucz user can provide or cnnpt provide

            urlToPersist.setExpirationDate(getExpirationDate(urlDto.getExpirationDate(),urlToPersist.getCreationDate()));

            //tp persiste we make use of persiste method  for generated short link
            Url urlToRet = persistShortLink(urlToPersist);
            System.out.println("Generated short link: " + urlToRet.getShortLink() + " for URL: " + urlToRet.getOriginalUrl());

if(urlToRet!=null){
    return urlToRet;

}

return null;
        }

        return null;
    }

    private LocalDateTime getExpirationDate(String expirationDate,LocalDateTime creationDate) {

        if(StringUtils.isBlank(expirationDate)){

            //after 1 min it expires
            return creationDate.plusDays(60);
        }

        // if the user hass provided
        LocalDateTime expirationDateToRet= LocalDateTime.parse(expirationDate);
        return expirationDateToRet;

    }

    //long to short link
    private String encodeUrl(String url) {
        String encodedUrl="";
        // even if you are getting the same url request you have to generate the unique short link for that pupose we are using localdate time
// and we will appending this to  orig url then we will bee hashinng the url to short link

        LocalDateTime time =LocalDateTime.now();
        encodedUrl= Hashing.murmur3_32_fixed().hashString(url.concat(time.toString()), StandardCharsets.UTF_8).toString();


                return encodedUrl;

    }

    @Override
    public Url persistShortLink(Url url) {

        //saving to database

        Url urlToRet=urlRepository.save(url);
        return urlToRet;
    }

    @Override
    public Url getEncodedUrl(String url) {

        //getting back the url....

        System.out.println("Searching for short link: " + url);
        Url urlToRet = urlRepository.findByShortLink(url);
        System.out.println("Database search result: " + (urlToRet != null ? "Found: " + urlToRet.getOriginalUrl() : "Not found"));
        return urlToRet;
    }

    @Override
    public void deleteShortLink(Url url) {
urlRepository.delete(url);
    }
}
