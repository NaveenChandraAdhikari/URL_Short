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

            String encodedUrl=encodeUrl(urlDto.getUrl());
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
            urlToPersist.setOriginalUrl(urlDto.getUrl());
            urlToPersist.setShortLink(encodedUrl);

            //default expiration data..ucz user can provide or cnnpt provide

            urlToPersist.setExpirationDate(getExpirationDate(urlDto.getExpirationDate(),urlToPersist.getCreationDate()));

            //tp persiste we make use of persiste method  for generated short link
            Url urlToRet =persistShortLink(urlToPersist);

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
            return creationDate.plusSeconds(60);
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

        Url urlToRet =urlRepository.findByShortLink(url);

        //returning the url /object back to the method
        return urlToRet;
    }

    @Override
    public void deleteShortLink(Url url) {
urlRepository.delete(url);
    }
}
