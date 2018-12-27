import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        Person person = new Person("Sergey", "RUS", 32);
        Student student = new Student("Vasya", "RUS", 23, "ITMO");
        File file = new File("foo.xml");
        Serializer.serialize(person, file);
        file = new File("boo.xml");
        Serializer.serialize(student, file);
    }
}
