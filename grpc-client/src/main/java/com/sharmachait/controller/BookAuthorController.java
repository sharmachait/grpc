package com.sharmachait.controller;

import com.google.protobuf.Descriptors;
import com.sharmachait.service.BookAuthorClientService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class BookAuthorController {
    BookAuthorClientService bookAuthorClientService;

    @GetMapping("/author/{id}")
    public Map<Descriptors.FieldDescriptor, Object> getBookAuthor(@PathVariable int id) {
        return bookAuthorClientService.getAuthor(id);
    }
    @GetMapping("/books/by/author/{id}")
    public List<Map<Descriptors.FieldDescriptor, Object>> getBooksByAuthor(@PathVariable int id) {
        return bookAuthorClientService.getBooksByAuthor(id);
    }

    @GetMapping("/books/expensive")
    public Map<Descriptors.FieldDescriptor, Object> getBooksByAuthor() {
        return bookAuthorClientService.getExpensiveBook();
    }

    @GetMapping("/books/{gender}")
    public List<Map<Descriptors.FieldDescriptor, Object>> getBooksByAuthorGender(@PathVariable String gender) {
        return bookAuthorClientService.getBooksByAuthorGender(gender);
    }
}
