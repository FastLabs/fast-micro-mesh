package org.flabs.refdata.currency.model;

import lombok.Value;
import org.flabs.common.model.AbstractDataEntity;
import org.flabs.refdata.RefDataCodec;

@Value
public class Currency extends AbstractDataEntity {
    private final String code;

    public Currency(String code) {
        super(RefDataCodec.CURRENCY_CODEC);
        this.code = code;
    }
}
