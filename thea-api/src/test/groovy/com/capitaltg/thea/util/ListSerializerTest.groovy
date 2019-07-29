package com.capitaltg.thea.util

import static org.junit.Assert.fail

import org.junit.Test

import groovy.json.JsonException

class ListSerializerTest {

  private final ListSerializer listSerializer = new ListSerializer()

  @Test
  void convertToDatabaseColumn() {
    def list = ['a', 'b', 'c']
    assert listSerializer.convertToDatabaseColumn(list) == '["a","b","c"]'
  }

  @Test
  void convertToEntityAttribute() {
    def list = listSerializer.convertToEntityAttribute('["a","b","c"]')
    assert list == ['a', 'b', 'c']
  }

  @Test
  void convertToEntityAttributeEmpty() {
    def list = listSerializer.convertToEntityAttribute('')
    assert list == []
  }

  @Test
  void convertToEntityAttributeNull() {
    def list = listSerializer.convertToEntityAttribute(null)
    assert list == []
  }

  @Test
  void convertToEntityAttributeException() {
    try {
      listSerializer.convertToEntityAttribute('[')
      fail('Should throw JsonException')
    } catch (Exception e) {
      assert e instanceof JsonException
    }
  }

}
