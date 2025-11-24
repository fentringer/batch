package com.example.demo.controller;

import com.example.demo.model.Person;
import com.example.demo.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/person")
@CrossOrigin(origins = "*")
public class PersonController {

    private static final Logger logger = LoggerFactory.getLogger(PersonController.class);

    @Autowired
    private PersonService personService;

    @PostMapping("/create")
    public ResponseEntity<Person> create(@RequestParam String name) {
        logger.info("POST /person/create - name: {}", name);
        Person person = personService.createPerson(name);
        return ResponseEntity.ok(person);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Person>> getAll() {
        logger.info("GET /person/all");
        List<Person> persons = personService.getAllPersons();
        return ResponseEntity.ok(persons);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Person> getById(@PathVariable Long id) {
        logger.info("GET /person/id/{}", id);
        return personService.getPersonById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Person> update(@PathVariable Long id, @RequestParam String name) {
        logger.info("PUT /person/{} - name: {}", id, name);
        return personService.updatePerson(id, name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("DELETE /person/{}", id);
        boolean deleted = personService.deletePerson(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAll() {
        logger.info("DELETE /person/all");
        personService.deleteAllPersons();
        return ResponseEntity.ok().build();
    }
}




