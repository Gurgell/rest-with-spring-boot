package com.example.restwithspringboot;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;


@RestController
public class GreetingController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    //http://localhost:8080/greeting - Ao digitar isso no navegador, ele reconhece que essa é a função do endpoint
    @RequestMapping("/greeting") //Usado para mapear o endpoint digitado na URL
    public Greeting greeting(@RequestParam(value = "name", //O value se refere caso você queira passar algum parâmetro. Se passar, deverá ser: ?name="algum nome"
                    defaultValue = "World")  //Caso não passe o valor do nome por parâmetro ele seta como default o world
            String name){

        //Toda vez que chamar esse endpoint ele retornará um novo objeto greeting incrementando o ID e formatando o nome com o parâmetro passado na URL + o template (atributo da classe)
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }
}
