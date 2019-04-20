package org.flabs.common.codec;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.netty.util.CharsetUtil;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

import java.lang.reflect.Type;
import java.util.ArrayList;

public interface ValueClassCodec<T> extends MessageCodec<T, T> {
    Gson gson = new Gson();

    static <T> ValueClassCodec<T> newCodec(Class<T> clz) {
        return newCodec(TypeToken.get(clz));
    }

    static <T> ValueClassCodec<T> newListCodec(Class<T> clz) {
        return newCodec(TypeToken.getParameterized(ArrayList.class, clz));
    }

    static <T> ValueClassCodec<T> newCodec(TypeToken<?> t) {
        return t::getType;
    }


    @Override
    default void encodeToWire(Buffer buffer, T t) {
        final byte[] bytes = gson.toJson(t).getBytes();
        buffer.appendInt(bytes.length);
        buffer.appendBytes(bytes);

    }

    Type getType();

    @Override
    default T decodeFromWire(int pos, Buffer buffer) {
        var length = buffer.getInt(pos);
        var newPos = pos + 4;
        var bytes = buffer.getBytes(newPos, newPos + length);
        return gson.fromJson(new String(bytes, CharsetUtil.UTF_8), getType());
    }

    @Override
    default T transform(T t) {
        return t;
    }

    @Override
    default String name() {
        return getType().getTypeName();
    }

    @Override
    default byte systemCodecID() {
        return -1;
    }
}
