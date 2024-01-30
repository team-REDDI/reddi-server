package com.example.reddiserver.repository;

import com.example.reddiserver.entity.Keyword;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    @Query("SELECT k FROM Keyword k ORDER BY k.count DESC")
    List<Keyword> findTopNByOrderByCountDesc(Pageable pageable);

    Optional<Keyword> findByKeyword(String keyword);
}
