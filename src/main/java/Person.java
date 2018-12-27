@XmlObject
public class Person {

    @XmlTag(name = "fullname")
    private final String name;

    @XmlAttribute(tag = "fullname")
    private final String lang;

    private final int age;

    Person(String name, String lang, int age) {
        this.name = name;
        this.lang = lang;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    @XmlTag
    private int getAge() {
        return age;
    }
}
