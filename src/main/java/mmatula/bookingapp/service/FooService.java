package mmatula.bookingapp.service;

import mmatula.bookingapp.model.Foo;
import mmatula.bookingapp.repository.FooRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FooService {

    private final FooRepository fooRepository;

    @Autowired
    public FooService(FooRepository fooRepository) {
        this.fooRepository = fooRepository;
    }

    public List<Foo> getAllFoo(){
        return this.fooRepository.findAll();
    }

    public void addFoo(Foo foo){
        this.fooRepository.save(foo);
    }

    public void deleteFooById(int id){
        this.fooRepository.deleteById(id);
    }
}
