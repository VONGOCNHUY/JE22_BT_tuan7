package com.example.demo.controller;

import com.example.demo.model.Doctor;
import com.example.demo.service.DoctorService;
import com.example.demo.dto.DoctorDTO;
import com.example.demo.dto.DepartmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {
    
    @Autowired
    private DoctorService doctorService;
    
    @GetMapping("/home")
    public String home(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Doctor> doctors;
        if (search != null && !search.trim().isEmpty()) {
            doctors = doctorService.searchDoctorsByName(search);
            model.addAttribute("search", search);
        } else {
            doctors = doctorService.getAllDoctors();
        }
        model.addAttribute("doctors", doctors);
        return "home";
    }
    
    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }
    
    @GetMapping("/api/search-doctors")
    @ResponseBody
    public List<DoctorDTO> searchDoctorsAjax(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {
        List<Doctor> doctors;
        if (keyword.trim().isEmpty()) {
            doctors = doctorService.getAllDoctors();
        } else {
            doctors = doctorService.searchDoctorsByName(keyword);
        }
        
        // Convert to DTO
        return doctors.stream()
            .map(doc -> new DoctorDTO(
                doc.getId(),
                doc.getName(),
                doc.getSpecialty(),
                doc.getImage(),
                doc.getDepartment() != null ? 
                    new DepartmentDTO(doc.getDepartment().getId(), doc.getDepartment().getName()) : 
                    null
            ))
            .collect(Collectors.toList());
    }
}
