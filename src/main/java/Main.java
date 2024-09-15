import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static final String TEMPLATE ="src/main/resources/velocity/template.vm";
    private static final String FILE_NAME ="output.graphqls";

    public static void main(String[] args) throws IOException {
        var loader = ClassLoader.getSystemClassLoader();
        var streamReader = loader.getResourceAsStream("model");
        assert streamReader != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(streamReader));
        List<Class> classes = reader.lines().filter(line -> line.endsWith(".class")).map(line-> getClass("model",line)).collect(Collectors.toList());
        generateFile(classes);
    }

    private static void generateFile(List<Class> classes) throws IOException {
        VelocityEngine ve = new VelocityEngine();
        ve.init();
        Writer writer = new FileWriter(FILE_NAME);
        VelocityContext context = new VelocityContext();
        context.put("classes", classes.stream().map(Class::getSimpleName).collect(Collectors.toList()));
        for(var clazz: classes){
            var fields = getFields(clazz);
            context.put("className", clazz.getSimpleName());
            context.put("fields", fields);
            Velocity.mergeTemplate(TEMPLATE,"UTF-8", context, writer);
            writer.flush();
        }

        writer.close();
    }

    private static void writeToFile(Class<?> clazz) throws IOException {
        var fields = getFields(clazz);
        generateFile(clazz.getSimpleName(), fields, FILE_NAME);
    }

    private static void generateFile(String className, Entry[] fields, String outputFileName) throws IOException {
        VelocityEngine ve = new VelocityEngine();
        ve.init();

        VelocityContext context = new VelocityContext();
        context.put("className", className);
        context.put("fields", fields);

        Writer writer = new FileWriter(outputFileName);
        Velocity.mergeTemplate(TEMPLATE,"UTF-8", context, writer);
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

    private static Class<?> getClass(String packageName, String className){
        try {
            return Class.forName(packageName+"."+className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
