package org.flabs.refdata.currency.service;

import lombok.Value;
import org.flabs.common.model.AbstractDataEntity;
import org.flabs.refdata.RefDataCodec;


@Value
public class CurrencyDataQuery extends AbstractDataEntity {

    private final String currency;

    public CurrencyDataQuery( String currency) {
        super(RefDataCodec.CURRENCY_CODEC);
        this.currency = currency;
    }
}
