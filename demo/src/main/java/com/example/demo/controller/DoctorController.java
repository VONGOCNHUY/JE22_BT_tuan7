package com.example.demo.controller;

import com.example.demo.model.Department;
import com.example.demo.model.Doctor;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class DoctorController {
    
    @Autowired
    private DoctorService doctorService;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @GetMapping("/doctors")
    public String listDoctors(Model model) {
        List<Doctor> doctors = doctorService.getAllDoctors();
        model.addAttribute("doctors", doctors);
        return "admin/doctors-list";
    }
    
    @GetMapping("/doctors/new")
    public String createDoctorForm(Model model) {
        List<Department> departments = departmentRepository.findAll();
        model.addAttribute("doctor", new Doctor());
        model.addAttribute("departments", departments);
        return "admin/doctor-form";
    }
    
    @PostMapping("/doctors")
    public String saveDoctor(@ModelAttribute Doctor doctor, @RequestParam(required = false) Integer departmentId) {
        System.out.println("💾 POST /admin/doctors - Creating doctor: " + doctor.getName());
        System.out.println("   Department ID: " + departmentId);
        
        if (departmentId != null) {
            Optional<Department> dept = departmentRepository.findById(departmentId);
            if (dept.isPresent()) {
                doctor.setDepartment(dept.get());
                System.out.println("   Department set: " + dept.get().getName());
            } else {
                System.out.println("   ❌ Department not found!");
                return "redirect:/admin/doctors/new?error=Department not found";
            }
        } else {
            System.out.println("   ⚠️ No department selected!");
            return "redirect:/admin/doctors/new?error=Please select a department";
        }
        
        Doctor saved = doctorService.saveDoctor(doctor);
        System.out.println("   ✓ Doctor saved with ID: " + saved.getId());
        return "redirect:/admin/doctors?success";
    }
    
    @GetMapping("/doctors/{id}")
    public String editDoctorForm(@PathVariable("id") int id, Model model) {
        Optional<Doctor> doctor = doctorService.getDoctorById(id);
        List<Department> departments = departmentRepository.findAll();
        
        if (doctor.isPresent()) {
            model.addAttribute("doctor", doctor.get());
            model.addAttribute("departments", departments);
            return "admin/doctor-form";
        }
        return "redirect:/admin/doctors";
    }
    
    @PostMapping("/doctors/{id}")
    public String updateDoctor(@PathVariable("id") int id, @ModelAttribute Doctor doctor, @RequestParam(required = false) Integer departmentId) {
        doctor.setId(id);
        if (departmentId != null) {
            Optional<Department> dept = departmentRepository.findById(departmentId);
            if (dept.isPresent()) {
                doctor.setDepartment(dept.get());
            }
        }
        doctorService.saveDoctor(doctor);
        return "redirect:/admin/doctors?success";
    }
    
    @GetMapping("/doctors/{id}/delete")
    public String deleteDoctor(@PathVariable("id") int id) {
        doctorService.deleteDoctor(id);
        return "redirect:/admin/doctors?success";
    }
}

