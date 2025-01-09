package com.nurflugel.dependencyvisualizer.io

import com.google.gson.*
import com.nurflugel.dependencyvisualizer.data.pojos.Person
import java.lang.reflect.Type

/** Created by douglas_bullard on 1/18/16.
 *
 * todo delete me
 * */
class PersonAdapter // implements JsonSerializer<Person>, JsonDeserializer<Person>

{
    // @Override
    fun serialize(src: Person, typeOfSrc: Type?, context: JsonSerializationContext): JsonElement {
        val result = JsonObject()

        result.add("type", JsonPrimitive(src.javaClass.simpleName))
        result.add("properties", context.serialize(src, src.javaClass))

        return result
    }

    // @Override
    @Throws(JsonParseException::class)
    fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): Person {
        val jsonObject = json.asJsonObject
        var element = jsonObject["type"]

        if (element != null) {
            element = jsonObject["properties"]
        }

        val className = element!!.asString

        try {
            val theClassName = "com.nurflugel.dependencyvisualizer.data.pojos.$className"

            println("theClassName = $theClassName")

            val classInstance = Class.forName(theClassName)

            return context.deserialize(element, classInstance)
        } catch (cnfe: ClassNotFoundException) {
            throw JsonParseException("Unknown element type: $className", cnfe)
        }
    }
}
