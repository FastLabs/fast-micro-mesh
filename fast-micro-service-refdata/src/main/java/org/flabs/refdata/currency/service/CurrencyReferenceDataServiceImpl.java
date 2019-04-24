package org.flabs.refdata.currency.service;

import io.reactivex.Single;
import io.vertx.reactivex.core.eventbus.EventBus;
import org.flabs.common.service.AbstractDataService;
import org.flabs.refdata.currency.model.CurrencyPair;

import java.util.List;

public class CurrencyReferenceDataServiceImpl implements CurrencyReferenceDataService.CurrencyServiceType {


     static CurrencyReferenceDataService newServiceInstance(EventBus eventBus) {
        return new CurrencyDataService(eventBus);
    }

    static class CurrencyDataService extends AbstractDataService implements CurrencyReferenceDataService {

        CurrencyDataService(EventBus eventBus) {
            super(eventBus);
        }

        @Override
        public Single<List<CurrencyPair>> getCurrencyPairs(CurrencyDataQuery dataQuery) {
            System.out.println("Requested currency pairs");
             return queryList(CurrencyReferenceDataService.PROVIDER_ADDRESS, dataQuery);
            //return getList(CurrencyReferenceDataService.PROVIDER_ADDRESS);
        }
    }


}
