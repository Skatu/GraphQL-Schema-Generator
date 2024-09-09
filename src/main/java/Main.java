import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final String template ="src/main/resources/velocity/template.vm";

    public static void main(String[] args) throws IOException {
        d223(Book.class);
    }

    private static void d223(Class<?> clazz) throws IOException {
        var fields = getFields(clazz);
        generateFile(clazz.getSimpleName(), fields, "output.graphqls");
    }

    private static void generateFile(String className, Entry[] fields, String outputFileName) throws IOException {
        VelocityEngine ve = new VelocityEngine();
        ve.init();

        VelocityContext context = new VelocityContext();
        context.put("className", className);
        context.put("fields", fields);

        Writer writer = new FileWriter(outputFileName);
        Velocity.mergeTemplate(template,"UTF-8", context, writer);
        writer.flush();
        writer.close();
    }

    private static Entry[] getFields(Class<?> clazz) {
        var fields =clazz.getDeclaredFields();
        var entries = new Entry[fields.length];
        for (int i = 0; i < fields.length; i++) {
            entries[i] = Entry.create(fields[i]);
        }
        return entries;
    }
}
