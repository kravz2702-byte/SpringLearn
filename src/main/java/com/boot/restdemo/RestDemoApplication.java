package com.boot.restdemo;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
public class RestDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestDemoApplication.class, args);
    }

}

@Component
class DataLoader{
    private final CoffeRepository coffeeRepository;
    public DataLoader(CoffeRepository coffeeRepository) {
        this.coffeeRepository = coffeeRepository;
    }

    @PostConstruct
    public void loadData(){
        coffeeRepository.saveAll(List.of(
                new Coffee("Café Cereza"),
                new Coffee("Café Ganador"),
                new Coffee("Café Lareño"),
                new Coffee("Café Três Pontas")
        ));

    }
}

@RestController
@RequestMapping("/coffees")
class RestApiDemoController {
    private final CoffeRepository coffeRepository;

    public RestApiDemoController(CoffeRepository coffeRepository) {
        this.coffeRepository = coffeRepository;

    }

    @GetMapping
    Iterable<Coffee> getCoffees() {
        return coffeRepository.findAll();
    }

    @GetMapping("/{id}")
    Optional<Coffee> getCoffeeById(@PathVariable String id) {
        return coffeRepository.findById(id);
    }

    @PostMapping
    Coffee postCoffee(@RequestBody Coffee coffee) {
        return coffeRepository.save(coffee);
    }

    @PutMapping("/{id}")
    ResponseEntity<Coffee> putCoffee(@PathVariable String id, @RequestBody Coffee coffee) {
        return (!coffeRepository.existsById(id))
                ? new ResponseEntity<>(coffeRepository.save(coffee), HttpStatus.CREATED)
                : new ResponseEntity<>(coffeRepository.save(coffee), HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    void deleteCoffee(@PathVariable String id) {
        coffeRepository.deleteById(id);
    }

}

interface CoffeRepository extends JpaRepository<Coffee, String> {}

@Entity
class Coffee {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "name")
    private String name;


    public Coffee() {}

//    @JsonCreator
    public Coffee(String id, String name) {
        this.name = name;
        this.id = id;
    }


    public Coffee( String name) {
        this(UUID.randomUUID().toString(), name);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}