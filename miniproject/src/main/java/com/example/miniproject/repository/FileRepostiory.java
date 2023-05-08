package com.example.miniproject.repository;

import com.example.miniproject.dto.FileDto;
import com.example.miniproject.entity.Files;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepostiory extends JpaRepository<Files, Long> {
}
