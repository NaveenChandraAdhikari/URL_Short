package com.urlShortner.URL_Short.Modal;


import lombok.Data;

import java.time.LocalDateTime;

//what are the dto we send to the user when dto generated...we send the original url and short link of the url and the expiry date of the url

/*
UrlDto is like an order form at a restaurant where you write down what you want (the long URL and maybe when it should expire).
UrlResponseDto is like the receipt you get back, showing what you ordered (original URL), what you got (short URL), and when it expires.
 */


@Data
public class UrlResponseDto {

    private String originalUrl;
    private String shortLink;

    private LocalDateTime expirationDate;

}
