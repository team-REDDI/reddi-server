package com.example.reddiserver.repository;

import com.example.reddiserver.entity.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromptRepository extends JpaRepository<Prompt, Long> {
    List<Prompt> findByMemberId(Long memberId);
}
