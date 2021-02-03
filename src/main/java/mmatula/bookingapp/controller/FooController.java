package mmatula.bookingapp.controller;

import mmatula.bookingapp.model.Foo;
import mmatula.bookingapp.service.FooService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class FooController {

    private final FooService fooService;

    @Autowired
    public FooController(FooService fooService) {
        this.fooService = fooService;
    }

    @GetMapping("/testFoo")
    public Foo getTestFoo(){
        return new Foo(1,"testName");
    }

    @GetMapping("/foo")
    public List<Foo> getAllFoo(){
        return this.fooService.getAllFoo();
    }

    @PostMapping("/foo")
    public void addFoo(@RequestBody Foo foo){
        this.fooService.addFoo(foo);
    }

    @DeleteMapping("/foo/{id}")
    public void deleteFoo(@PathVariable int id){
        this.fooService.deleteFooById(id);
    }
}
