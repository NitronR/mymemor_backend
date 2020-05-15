package com.mymemor.mymemor.controller;

import com.mymemor.mymemor.repository.MemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dev/memory")
public class MemoryController {
    @Autowired
    MemoryRepository memoryRepository;

    @GetMapping("/delete-all")
    public String deleteAllMemories() {
        memoryRepository.deleteAll();
        return "All memories deleted.";
    }
}
