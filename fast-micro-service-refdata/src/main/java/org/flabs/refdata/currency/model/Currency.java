package org.flabs.refdata.currency.model;

import com.google.gson.reflect.TypeToken;
import lombok.Builder;
import lombok.Value;
import org.flabs.common.model.AbstractDataEntity;

@Value
public class Currency extends AbstractDataEntity<Currency> {
    public static final TypeToken<Currency> typeToken = TypeToken.get(Currency.class);

    private final String code;

    public Currency(String code) {
        super(typeToken);
        this.code = code;
    }
}
