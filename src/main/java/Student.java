@XmlObject
public class Student extends Person {

    @XmlTag
    private final String university;

    Student(String name, String lang, int age, String university) {
        super(name, lang, age);
        this.university = university;
    }

    @XmlAttribute(name = "name", tag = "university")
    public String getUniversity() {
        return this.university;
    }
}
