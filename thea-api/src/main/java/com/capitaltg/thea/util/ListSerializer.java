package com.capitaltg.thea.util;

import jakarta.persistence.AttributeConverter;

import java.util.Arrays;
import java.util.List;
import com.google.gson.Gson;

public class ListSerializer implements AttributeConverter<List, String> {

  private final Gson gson = new Gson();

  @Override
  public String convertToDatabaseColumn(List attribute) {
    String jsonString = gson.toJson(attribute);
    return jsonString;     
  }

  @Override
  public List convertToEntityAttribute(String string) {
    if (string == null || string.trim().isEmpty()) {
      return Arrays.asList();
    }
    return gson.fromJson(string, List.class);
  }
}
