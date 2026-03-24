package com.example.demo.repository;

import com.example.demo.model.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    List<Doctor> findAll();
    
    Page<Doctor> findAll(Pageable pageable);
    
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Doctor> searchByName(@Param("keyword") String keyword);
    
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Doctor> searchByName(@Param("keyword") String keyword, Pageable pageable);
}

