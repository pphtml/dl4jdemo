package org.superbiz.util;

import org.nd4j.shade.jackson.core.JsonGenerator;
import org.nd4j.shade.jackson.core.JsonProcessingException;
import org.nd4j.shade.jackson.databind.SerializerProvider;
import org.nd4j.shade.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateSerializer extends StdSerializer<LocalDate> {

    private static final long serialVersionUID = 1L;

    public LocalDateSerializer(){
        super(LocalDate.class);
    }

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider sp) throws IOException, JsonProcessingException {
        gen.writeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }
}
