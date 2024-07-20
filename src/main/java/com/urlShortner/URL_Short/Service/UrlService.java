package com.urlShortner.URL_Short.Service;


import com.urlShortner.URL_Short.Modal.Url;
import com.urlShortner.URL_Short.Modal.UrlDto;
import org.springframework.stereotype.Service;


public interface UrlService
{

    public Url generateShortLink(UrlDto urlDto);
//    Saves a Url object,
    public Url persistShortLink(Url url);

    // retrieve url
    public Url getEncodedUrl(String url);

//    /remove url

    public void deleteShortLink(Url url);


}
