package com.scb.s2bx.common.model;

import com.google.gson.reflect.TypeToken;

public interface DataEntity <T> {
  TypeToken<T> getTypeToken();
}
