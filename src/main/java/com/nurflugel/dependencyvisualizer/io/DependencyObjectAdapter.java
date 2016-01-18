package com.nurflugel.dependencyvisualizer.io;

import com.google.gson.*;
import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject;
import java.lang.reflect.Type;

/** Created by douglas_bullard on 1/18/16. */
@SuppressWarnings("Duplicates")
public class DependencyObjectAdapter implements JsonSerializer<BaseDependencyObject>, JsonDeserializer<BaseDependencyObject>
{
  @Override
  public JsonElement serialize(BaseDependencyObject src, Type typeOfSrc, JsonSerializationContext context)
  {
    JsonObject result = new JsonObject();

    result.add("type", new JsonPrimitive(src.getClass().getSimpleName()));
    result.add("properties", context.serialize(src, src.getClass()));

    return result;
  }

  @Override
  public BaseDependencyObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
  {
    JsonObject  jsonObject = json.getAsJsonObject();
    JsonElement element    = jsonObject.get("type");

    // if (element != null)
    // {
    // element = jsonObject.get("properties");
    // }
    String className = element.getAsString();

    try
    {
      String   thePackage    = "com.nurflugel.dependencyvisualizer.data.pojos.";
      Class<?> classInstance = Class.forName(thePackage + className);

      return context.deserialize(element, classInstance);
    }
    catch (ClassNotFoundException cnfe)
    {
      throw new JsonParseException("Unknown element type: " + className, cnfe);
    }
  }
}
