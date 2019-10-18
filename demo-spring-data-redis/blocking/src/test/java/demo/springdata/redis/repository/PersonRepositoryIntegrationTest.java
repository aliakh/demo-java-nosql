package demo.springdata.redis.repository;

import demo.springdata.redis.model.Person;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static demo.springdata.redis.model.Person.Gender.FEMALE;
import static demo.springdata.redis.model.Person.Gender.MALE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RedisConfiguration.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class PersonRepositoryIntegrationTest {

    private static RedisServer redisServer;

    @Autowired
    PersonRepository personRepository;

    @BeforeClass
    public static void beforeClass() throws IOException {
        redisServer = new RedisServer(6379);
        redisServer.start();
    }

    @AfterClass
    public static void afterClass() {
        redisServer.stop();
    }

    @Test
    public void testSaveFindById() {
        Person person = new Person("1", "John Doe", MALE, 1999);
        personRepository.save(person);
        Person actualPerson = personRepository.findById(person.getId()).get();
        assertEquals(person.getId(), actualPerson.getId());
    }

    @Test
    public void testSaveSaveFindById() {
        Person person = new Person("1", "John Doe", MALE, 1999);
        personRepository.save(person);
        person.setName("Richard Roe");
        personRepository.save(person);
        Person actualPerson = personRepository.findById(person.getId()).get();
        assertEquals(person.getName(), actualPerson.getName());
    }

    @Test
    public void testSaveSaveFindAll() {
        Person person1 = new Person("1", "John Doe", MALE, 1999);
        Person person2 = new Person("2", "Janie Doe", FEMALE, 2001);
        personRepository.save(person1);
        personRepository.save(person2);
        List<Person> persons = new ArrayList<>();
        personRepository.findAll().forEach(persons::add);
        assertEquals(persons.size(), 2);
    }

    @Test
    public void testSaveDeleteByIdFindById() throws Exception {
        Person person = new Person("1", "John Doe", MALE, 1960);
        personRepository.save(person);
        personRepository.deleteById(person.getId());
        Person actualPerson = personRepository.findById(person.getId()).orElse(null);
        assertNull(actualPerson);
    }
}