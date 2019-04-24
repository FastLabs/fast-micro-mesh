package org.flabs.refdata.currency.model;


import com.google.gson.Gson;
import org.flabs.refdata.RefDataCodec;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CurrencyModelSerializationTest {

    private final Gson gson = new Gson();

    @Test
    void testCurrencySerialization() {
        final var expected = "{\"code\":\"usd\"}";
        final String usd = gson.toJson(new Currency("usd"));
        assertEquals(expected, usd);
        assertEquals(new Currency("usd"), gson.fromJson(expected, RefDataCodec.CURRENCY_CODEC.getType()));
    }

    @Test
    void testCurrencyPairSerialization() {
        var expected = "{\"ccy1\":{\"code\":\"usd\"},\"ccy2\":{\"code\":\"eur\"},\"currencyPair\":\"usdeur\"}";
        var currencyPair = new CurrencyPair("usd", "eur");
        assertEquals(expected, gson.toJson(currencyPair));
        assertEquals(new CurrencyPair("usd", "eur"), gson.fromJson(expected, CurrencyPair.typeToken.getType()));

    }
}
