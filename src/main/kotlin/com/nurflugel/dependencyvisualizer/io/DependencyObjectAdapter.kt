package com.nurflugel.dependencyvisualizer.io

import com.google.gson.*
import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject
import java.lang.reflect.Type

/** Created by douglas_bullard on 1/18/16.
 * todo delete me
 *
 * */
class DependencyObjectAdapter : JsonSerializer<BaseDependencyObject>, JsonDeserializer<BaseDependencyObject> {
    override fun serialize(src: BaseDependencyObject, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val result = JsonObject()

        result.add("type", JsonPrimitive(src.javaClass.simpleName))
        result.add("properties", context.serialize(src, src.javaClass))

        return result
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): BaseDependencyObject {
        val jsonObject = json.asJsonObject
        val element = jsonObject["type"]

        // if (element != null)
        // {
        // element = jsonObject.get("properties");
        // }
        val className = element.asString

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
