package com.eweware.phabrik.api;

import com.google.gson.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Dave on 9/4/2016.
 */
public class gsonUTCDateAdapter implements JsonSerializer<DateTime>,JsonDeserializer<DateTime> {

    private final DateFormat dateFormat;

    public gsonUTCDateAdapter() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);      //This is the format I need
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));                               //This is the key line which converts the date to UTC which cannot be accessed with the default serializer
    }

    @Override
    public synchronized JsonElement serialize(DateTime date, Type type, JsonSerializationContext jsonSerializationContext) {
        final DateTimeFormatter fmt = ISODateTimeFormat.dateTime();

        return new JsonPrimitive(fmt.print(date.toDateTime(DateTimeZone.UTC)));
    }

    @Override
    public synchronized DateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        // Do not try to deserialize null or empty values
        if (jsonElement.getAsString() == null || jsonElement.getAsString().isEmpty())
        {
            return null;
        }

        final DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        return fmt.parseDateTime(jsonElement.getAsString());
    }
}
