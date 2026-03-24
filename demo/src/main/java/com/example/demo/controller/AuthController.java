package com.example.demo.controller;

import com.example.demo.model.Patient;
import com.example.demo.model.Role;
import com.example.demo.repository.RoleRepository;
import com.example.demo.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Controller
public class AuthController {
    
    @Autowired
    private PatientService patientService;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "register";
    }
    
    @PostMapping("/register")
    public String register(@ModelAttribute Patient patient) {
        // Check if user already exists
        if (patientService.findByUsername(patient.getUsername()).isPresent()) {
            return "redirect:/register?error=Username already exists";
        }
        
        // Assign PATIENT role
        Optional<Role> patientRole = roleRepository.findByName("PATIENT");
        Set<Role> roles = new HashSet<>();
        
        if (patientRole.isPresent()) {
            roles.add(patientRole.get());
        } else {
            Role newRole = new Role();
            newRole.setName("PATIENT");
            Role savedRole = roleRepository.save(newRole);
            roles.add(savedRole);
        }
        
        patient.setRoles(roles);
        patientService.savePatient(patient);
        
        return "redirect:/home";
    }
    
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }
}
