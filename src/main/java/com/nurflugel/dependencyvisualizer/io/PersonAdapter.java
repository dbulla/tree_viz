package com.nurflugel.dependencyvisualizer.io;

import com.google.gson.*;
import com.nurflugel.dependencyvisualizer.data.pojos.Person;
import java.lang.reflect.Type;

/** Created by douglas_bullard on 1/18/16. */
@SuppressWarnings("Duplicates")
public class PersonAdapter                // implements JsonSerializer<Person>, JsonDeserializer<Person>
{
  // @Override
  public JsonElement serialize(Person src, Type typeOfSrc, JsonSerializationContext context)
  {
    JsonObject result = new JsonObject();

    result.add("type", new JsonPrimitive(src.getClass().getSimpleName()));
    result.add("properties", context.serialize(src, src.getClass()));

    return result;
  }

  // @Override
  public Person deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
  {
    JsonObject  jsonObject = json.getAsJsonObject();
    JsonElement element    = jsonObject.get("type");

    if (element != null)
    {
      element = jsonObject.get("properties");
    }

    String className = element.getAsString();

    try
    {
      String theClassName = "com.nurflugel.dependencyvisualizer.data.pojos." + className;

      System.out.println("theClassName = " + theClassName);

      Class<?> classInstance = Class.forName(theClassName);

      return context.deserialize(element, classInstance);
    }
    catch (ClassNotFoundException cnfe)
    {
      throw new JsonParseException("Unknown element type: " + className, cnfe);
    }
  }
}
