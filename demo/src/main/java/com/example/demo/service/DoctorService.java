package com.example.demo.service;

import com.example.demo.model.Doctor;
import com.example.demo.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }
    
    public Optional<Doctor> getDoctorById(Integer id) {
        return doctorRepository.findById(id);
    }
    
    public Doctor saveDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }
    
    public void deleteDoctor(Integer id) {
        doctorRepository.deleteById(id);
    }
    
    public List<Doctor> searchDoctorsByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllDoctors();
        }
        return doctorRepository.searchByName(keyword);
    }
    
    public Page<Doctor> getAllDoctorsPaged(Pageable pageable) {
        return doctorRepository.findAll(pageable);
    }
    
    public Page<Doctor> searchDoctorsByNamePaged(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllDoctorsPaged(pageable);
        }
        return doctorRepository.searchByName(keyword, pageable);
    }
}
