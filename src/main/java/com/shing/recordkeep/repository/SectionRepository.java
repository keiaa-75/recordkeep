package com.shing.recordkeep.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shing.recordkeep.model.Section;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
}
