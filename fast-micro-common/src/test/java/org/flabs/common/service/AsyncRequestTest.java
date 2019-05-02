package org.flabs.common.service;

import io.reactivex.Observable;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import lombok.Value;
import org.flabs.common.codec.ValueClassCodec;
import org.flabs.common.model.AbstractDataEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class AsyncRequestTest {

    @Value
    static class Hero extends AbstractDataEntity {
        private final String name;

        Hero(String name) {
            super(ValueClassCodec.newCodec(Hero.class));
            this.name = name;
        }

    }

    class HeroProvider {
        Observable<Hero> someHeroes() {
            return Observable.fromArray(new Hero("IronMan"), new Hero("Batman"), new Hero("Cyborg"));
        }
    }

    @Test
    public void testPrimitiveListRequest(Vertx vertx, VertxTestContext tc) {
        var eventBus = vertx.eventBus();
        eventBus.registerCodec(ValueClassCodec.newCodec(Hero.class));
        eventBus.registerCodec(ValueClassCodec.newListCodec(Hero.class));
        var heroProvider = new HeroProvider();

        eventBus.<Hero>consumer("my-heroes")
                .toObservable()
                .map(msg -> new ServiceListRequest<>(msg, heroProvider::someHeroes, ValueClassCodec.newListCodec(Hero.class)))
                .flatMapCompletable(AsyncServiceRequest::execute)
                .subscribe();

        eventBus.<List<String>>rxSend("my-heroes", null)
                .subscribe(
                        msg -> {
                            var heroes = msg.body();
                            assertEquals(3, heroes.size());
                            assertEquals(new Hero("IronMan"), heroes.get(0));
                            tc.completeNow();
                        }
                        , tc::failNow
                );
    }
}
