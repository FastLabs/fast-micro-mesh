package org.flabs.refdata.currency.model;

import com.google.gson.reflect.TypeToken;
import lombok.Value;
import org.flabs.common.model.AbstractDataEntity;
import org.flabs.refdata.RefDataCodec;

@Value
public class CurrencyPair extends AbstractDataEntity {
    public  static final TypeToken<CurrencyPair> typeToken = TypeToken.get(CurrencyPair.class);

    private final Currency ccy1;
    private final Currency ccy2;
    private final String currencyPair;

    public CurrencyPair(Currency ccy1, Currency ccy2, String currencyPair) {
        super(RefDataCodec.CURRENCY_PAIR_CODEC);
        this.ccy1 = ccy1;
        this.ccy2 = ccy2;
        this.currencyPair = currencyPair;
    }

    public CurrencyPair(String ccy1, String ccy2) {
        this(new Currency(ccy1), new Currency(ccy2), ccy1 + ccy2);
    }
}
