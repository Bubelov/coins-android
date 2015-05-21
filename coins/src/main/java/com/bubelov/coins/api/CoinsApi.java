package com.bubelov.coins.api;

import com.bubelov.coins.model.Currency;
import com.bubelov.coins.model.Merchant;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Author: Igor Bubelov
 * Date: 17/04/15 10:42
 */

public interface CoinsApi {
    @GET("/merchants")
    List<Merchant> getMerchants(@Query("currency") String currencyCode, @Query("since") String since, @Query("limit") int limit);

    @GET("/currencies")
    List<Currency> getCurrencies();
}