package org.flabs.web;

import com.google.gson.Gson;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.flabs.common.service.AbstractServiceVerticle;
import org.flabs.refdata.currency.service.CurrencyDataQuery;
import org.flabs.refdata.currency.service.CurrencyReferenceDataService;

public class ApiHandlers {

    private AbstractServiceVerticle srv;
    private final Gson gson = new Gson();

    public ApiHandlers(AbstractServiceVerticle srv) {
        this.srv = srv;
    }

    public void currencyPairHandler(RoutingContext rc) {
        //TODO: access direct to service, discovery should be dynamic
        //TODO: understand more about release service reference
        srv.getServiceProvider(CurrencyReferenceDataService.SERVICE_NAME)
                .flatMap(serviceProvider -> {
                    var currencyPairSp = serviceProvider.getAs(CurrencyReferenceDataService.class);
                    if (currencyPairSp.isPresent()) {
                        return currencyPairSp.get()
                                .getCurrencyPairs(new CurrencyDataQuery("usd"));
                        //TODO: understand more about release service reference
                        //.doAfterTerminate(serviceProvider::release);
                    }
                    throw new RuntimeException("Service not available exception"); //TODO: start thinking about exception hierarchy
                })
                .subscribe(currencies -> {
                            var response = rc.response();
                            response.end(gson.toJson(currencies));
                        }
                        , err -> {
                            System.out.println(err);
                            rc.response().setStatusCode(500).setStatusMessage("Application error " + err.getMessage()).end();
                        }
                );

    }
}
