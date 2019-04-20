package org.flabs.common.model;

import com.google.gson.reflect.TypeToken;

public interface DataEntity <T> {
  TypeToken<T> getTypeToken();
}
