// src/main/java/com/smhrd/teamjo/repository/RicePreferenceRepository.java

package com.smhrd.teamjo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smhrd.teamjo.entity.RicePreference;

@Repository
public interface RicePreferenceRepository extends JpaRepository<RicePreference, Long> {
    Optional<RicePreference> findByUserId(String userId);
}
