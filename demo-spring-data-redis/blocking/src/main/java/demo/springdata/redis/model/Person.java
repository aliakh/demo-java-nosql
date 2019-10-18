package demo.springdata.redis.model;

import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.StringJoiner;

@RedisHash("Person")
public class Person implements Serializable {

    public enum Gender {
        MALE, FEMALE
    }

    private String id;
    private String name;
    private Gender gender;
    private int birthyear;

    public Person(String id, String name, Gender gender, int birthyear) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.birthyear = birthyear;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public int getBirthyear() {
        return birthyear;
    }

    public void setBirthyear(int birthyear) {
        this.birthyear = birthyear;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Person.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("name='" + name + "'")
                .add("gender=" + gender)
                .add("birthyear=" + birthyear)
                .toString();
    }
}