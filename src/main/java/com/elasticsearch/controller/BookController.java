package com.elasticsearch.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.elasticsearch.dao.domain.Book;
import com.elasticsearch.service.impl.BookServiceImpl;

@RestController
@RequestMapping("/api")
public class BookController {

    @Autowired
    private BookServiceImpl service;

    @ResponseBody
    @GetMapping(value = "/books")
    public ResponseEntity<Iterable<Book>> getAll() {
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping(value = "/book")
    public ResponseEntity<Book> insertGreeting(@RequestBody Book book) {
        return new ResponseEntity<>(service.save(book), HttpStatus.CREATED);
    }

    @ResponseBody
    @PutMapping(value = "/book")
    public ResponseEntity<Book> updateGreeting(@RequestBody Book book) {
        return new ResponseEntity<>(service.save(book), HttpStatus.OK);
    }

    @ResponseBody
    @DeleteMapping(value = "/book/{id}")
    public ResponseEntity<Book> deleteGreeting(@PathVariable("id") String id) {
        Book book = service.findOne(id);
        service.delete(book);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ResponseBody
    @GetMapping(value = "/book/{id}")
    public ResponseEntity<Book> getOne(@PathVariable("id") String id) {
        return new ResponseEntity<>(service.findOne(id), HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/book/name/{name}")
    public ResponseEntity<List<Book>> getByUserName(@PathVariable("name") String name) {
        return new ResponseEntity<>(service.findByTitle(name), HttpStatus.OK);
    }
}