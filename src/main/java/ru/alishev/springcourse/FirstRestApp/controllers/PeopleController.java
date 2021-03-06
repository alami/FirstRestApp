package ru.alishev.springcourse.FirstRestApp.controllers;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.alishev.springcourse.FirstRestApp.models.Person;
import ru.alishev.springcourse.FirstRestApp.services.PeopleService;
import ru.alishev.springcourse.FirstRestApp.util.PersonErrorResponse;
import ru.alishev.springcourse.FirstRestApp.util.PersonNotCeatedFoundException;
import ru.alishev.springcourse.FirstRestApp.util.PersonNotFoundException;

import javax.validation.Valid;
import java.util.List;

@RestController // @Controller + @ResponseBody над каждым методом
@RequestMapping("/people")
public class PeopleController {
    private final PeopleService peopleService;

    @Autowired
    public PeopleController(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @GetMapping()
    public List<Person> getPeople() {
        return peopleService.findAll(); // Jackson конвертирует эти объекты в JSON
    }

    @GetMapping("/{id}")
    public Person getPerson(@PathVariable("id") int id) {
        return peopleService.findOne(id); // Jackson конвертирует в JSON
    }
    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException e) {
        PersonErrorResponse response = new PersonErrorResponse(
                "Person with this ID wasn't found!",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    @PostMapping
    public ResponseEntity<HttpStatus> create (@RequestBody @Valid Person person,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error: errors)
                errorMsg.append(error.getField())
                        .append(" - ").append(error.getDefaultMessage()).append(";");
            throw new PersonNotCeatedFoundException(errorMsg.toString());
        }
        peopleService.save(person);
        return ResponseEntity.ok(HttpStatus.OK);
    }
    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCeatedFoundException e) {
        PersonErrorResponse response = new PersonErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
