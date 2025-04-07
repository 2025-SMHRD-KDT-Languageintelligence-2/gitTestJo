package com.smhrd.teamjo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smhrd.teamjo.entity.SidePreference;

@Repository
public interface SidePreferenceRepository extends JpaRepository<SidePreference, Long> {
    Optional<SidePreference> findByUserId(String userId);
}
