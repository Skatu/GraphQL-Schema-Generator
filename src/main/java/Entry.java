import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

public class Entry {
    private final String type;
    private final String name;

    public static Entry create(Field field) {
        String type = translateType(field);
        return new Entry(type, field.getName());
    }

    private Entry(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    private static String translateType(Field field){
        if(StringUtils.endsWithIgnoreCase(field.getName(),"id")){
            return "ID";
        }
        return switch (field.getType().getSimpleName()) {
            case "boolean", "Boolean" -> "Boolean";
            case "int", "Integer" -> "Int";
            case "float", "Float", "double", "Double" -> "Float";
            default -> field.getType().getSimpleName();
        };
    }
}
