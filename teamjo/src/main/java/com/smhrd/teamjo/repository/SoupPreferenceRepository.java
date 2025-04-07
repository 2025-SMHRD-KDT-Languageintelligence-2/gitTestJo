package com.smhrd.teamjo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smhrd.teamjo.entity.SoupPreference;

@Repository
public interface SoupPreferenceRepository extends JpaRepository<SoupPreference, Long> {
    Optional<SoupPreference> findByUserId(String userId);
}
