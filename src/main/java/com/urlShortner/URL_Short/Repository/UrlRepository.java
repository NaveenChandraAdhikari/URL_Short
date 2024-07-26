package com.urlShortner.URL_Short.Repository;

import com.urlShortner.URL_Short.Modal.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UrlRepository extends JpaRepository<Url,Long> {

    // we query our short link ince it is generated..shortLink is the part of the get api

    public Url findByShortLink(String shortLink);
    boolean existsByShortLink(String shortLink);

}
