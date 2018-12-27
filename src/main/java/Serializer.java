import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

class Serializer {
    static <T> void serialize(T t, File file) throws Exception {
        Document document = DocumentHelper.createDocument();
        Class<?> clazz = t.getClass();

        XmlObject[] xmlObjects = clazz.getAnnotationsByType(XmlObject.class);
        Element root = null;
        if (xmlObjects.length > 0) {
            String name = xmlObjects[0].name();
            if (name.equals("default")) {
                name = clazz.getCanonicalName().toLowerCase();
            }
            root = document.addElement(name);
        } else {
            System.exit(0);
        }

        while (clazz != null) {
            xmlObjects = clazz.getAnnotationsByType(XmlObject.class);
            if (xmlObjects.length == 0) {
                break;
            }

            Method[] methods = clazz.getDeclaredMethods();
            Field[] fields = clazz.getDeclaredFields();

            Map<String, Element> properties = new HashMap<>();
            Map<String, XmlTag> xmlTags = new HashMap<>();

            //mapping fields
            for (Field field : fields) {
                XmlTag[] fieldXmlTags = field.getAnnotationsByType(XmlTag.class);
                for (XmlTag xmlTag : fieldXmlTags) {
                    String name = xmlTag.name();
                    if (name.equals("default")) {
                        name = field.getName();
                    }
                    if (xmlTags.containsKey(name)) {
                        throw new Exception();
                    }
                    xmlTags.put(name, xmlTag);
                    field.setAccessible(true);
                    properties.put(name, root.addElement(name).addText(field.get(t).toString()));
                }
            }

            //mapping methods
            for (Method method : methods) {
                XmlTag[] methodXmlTags = method.getAnnotationsByType(XmlTag.class);
                for (XmlTag xmlTag : methodXmlTags) {
                    Parameter[] parameters = method.getParameters();
                    if (parameters.length > 0 || method.getReturnType().equals(void.class)) {
                        throw new Exception();
                    }
                    String name = xmlTag.name();
                    if (name.equals("default")) {
                        name = method.getName();
                        if (name.startsWith("get")) {
                            name = name.substring(3).toLowerCase();
                        }
                    }
                    if (xmlTags.containsKey(name)) {
                        throw new Exception();
                    }
                    xmlTags.put(name, xmlTag);
                    method.setAccessible(true);
                    properties.put(name, root.addElement(name).addText(method.invoke(t).toString()));
                }
            }

            //adding attributes
            for (Field field : fields) {
                XmlAttribute[] xmlAttributes = field.getAnnotationsByType(XmlAttribute.class);
                for (XmlAttribute xmlAttribute : xmlAttributes) {
                    for (Map.Entry<String, XmlTag> xmlTag : xmlTags.entrySet()) {
                        String tagName = xmlTag.getKey();

                        if (tagName.equals(xmlAttribute.tag())) {
                            String attributeName = xmlAttribute.name();
                            if (attributeName.equals("default")) {
                                attributeName = field.getName();
                            }
                            field.setAccessible(true);
                            String attributeValue = field.get(t).toString();
                            properties.put(tagName, properties.get(tagName).addAttribute(attributeName, attributeValue));
                        }
                    }
                }
            }

            for (Method method : methods) {
                XmlAttribute[] xmlAttributes = method.getAnnotationsByType(XmlAttribute.class);
                for (XmlAttribute xmlAttribute : xmlAttributes) {
                    for (Map.Entry<String, XmlTag> xmlTag : xmlTags.entrySet()) {
                        String tagName = xmlTag.getKey();

                        if (tagName.equals(xmlAttribute.tag())) {
                            Parameter[] parameters = method.getParameters();
                            if (parameters.length > 0 || method.getReturnType().equals(void.class)) {
                                throw new IllegalAccessException();
                            }
                            String attributeName = xmlAttribute.name();
                            if (attributeName.equals("default")) {
                                attributeName = method.getName();
                                if (attributeName.startsWith("get")) {
                                    attributeName = attributeName.substring(3).toLowerCase();
                                }
                            }
                            method.setAccessible(true);
                            String attributeValue = method.invoke(t).toString();
                            properties.put(tagName, properties.get(tagName).addAttribute(attributeName, attributeValue));
                        }
                    }
                }
            }
            clazz = clazz.getSuperclass();

            FileWriter out = new FileWriter(file);
            document.write(out);
            out.close();
        }
    }
}
