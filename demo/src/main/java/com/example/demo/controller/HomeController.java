package com.example.demo.controller;

import com.example.demo.model.Doctor;
import com.example.demo.service.DoctorService;
import com.example.demo.dto.DoctorDTO;
import com.example.demo.dto.DepartmentDTO;
import com.example.demo.dto.PageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public PageDTO<DoctorDTO> searchDoctorsAjax(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page) {
        
        int pageSize = 5;
        Pageable pageable = PageRequest.of(page, pageSize);
        
        Page<Doctor> doctorsPage;
        if (keyword.trim().isEmpty()) {
            doctorsPage = doctorService.getAllDoctorsPaged(pageable);
        } else {
            doctorsPage = doctorService.searchDoctorsByNamePaged(keyword, pageable);
        }
        
        // Convert to DTO
        List<DoctorDTO> dtoList = doctorsPage.getContent().stream()
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
        
        PageDTO<DoctorDTO> pageDTO = new PageDTO<>();
        pageDTO.setContent(dtoList);
        pageDTO.setCurrentPage(page);
        pageDTO.setTotalPages(doctorsPage.getTotalPages());
        pageDTO.setTotalElements(doctorsPage.getTotalElements());
        pageDTO.setPageSize(pageSize);
        
        return pageDTO;
    }
}
