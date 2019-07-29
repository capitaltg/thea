package com.capitaltg.thea.util

import javax.persistence.AttributeConverter

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

class ListSerializer implements AttributeConverter<List, String> {

  @Override
  String convertToDatabaseColumn(List attribute) {
    return new JsonBuilder(attribute).toString()
  }

  @Override
  List convertToEntityAttribute(String string) {
    if (!string) {
      return []
    }
    return new JsonSlurper().parseText(string) as List
  }

}
