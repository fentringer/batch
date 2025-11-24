package com.example.demo.service;

import com.example.demo.model.Person;
import com.example.demo.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    private static final Logger logger = LoggerFactory.getLogger(PersonService.class);

    @Autowired
    private PersonRepository personRepository;

    @Transactional
    public Person createPerson(String name) {
        logger.info("Creating person with name: {}", name);
        Person person = new Person();
        person.setName(name);
        Person saved = personRepository.save(person);
        logger.info("Person created with ID: {}", saved.getId());
        return saved;
    }

    public List<Person> getAllPersons() {
        logger.info("Fetching all persons");
        List<Person> persons = personRepository.findAll();
        logger.info("Found {} persons", persons.size());
        return persons;
    }

    public Optional<Person> getPersonById(Long id) {
        logger.info("Fetching person with ID: {}", id);
        Optional<Person> person = personRepository.findById(id);
        if (person.isPresent()) {
            logger.info("Person found: {}", person.get().getName());
        } else {
            logger.warn("Person with ID {} not found", id);
        }
        return person;
    }

    @Transactional
    public Optional<Person> updatePerson(Long id, String name) {
        logger.info("Updating person with ID: {} to name: {}", id, name);
        Optional<Person> personOptional = personRepository.findById(id);
        if (personOptional.isPresent()) {
            Person person = personOptional.get();
            person.setName(name);
            Person updated = personRepository.save(person);
            logger.info("Person updated: ID={}, Name='{}'", updated.getId(), updated.getName());
            return Optional.of(updated);
        }
        logger.warn("Person with ID {} not found for update", id);
        return Optional.empty();
    }

    @Transactional
    public boolean deletePerson(Long id) {
        logger.info("Deleting person with ID: {}", id);
        if (personRepository.existsById(id)) {
            personRepository.deleteById(id);
            logger.info("Person with ID {} deleted successfully", id);
            return true;
        }
        logger.warn("Person with ID {} not found for deletion", id);
        return false;
    }

    @Transactional
    public void deleteAllPersons() {
        logger.info("Deleting all persons");
        long count = personRepository.count();
        personRepository.deleteAll();
        logger.info("Deleted {} persons", count);
    }
}




