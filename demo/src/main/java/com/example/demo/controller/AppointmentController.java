package com.example.demo.controller;

import com.example.demo.model.Appointment;
import com.example.demo.model.Doctor;
import com.example.demo.model.Patient;
import com.example.demo.service.AppointmentService;
import com.example.demo.service.DoctorService;
import com.example.demo.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {
    
    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);
    
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private DoctorService doctorService;
    
    @Autowired
    private PatientService patientService;
    
    @GetMapping("/book/{doctorId}")
    public String bookAppointmentForm(@PathVariable int doctorId, Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        Optional<Doctor> doctor = doctorService.getDoctorById(doctorId);
        if (doctor.isPresent()) {
            model.addAttribute("doctor", doctor.get());
            model.addAttribute("appointment", new Appointment());
            return "appointment-form";
        }
        return "redirect:/home";
    }
    
    @PostMapping("/book/{doctorId}")
    public String saveAppointment(@PathVariable int doctorId, 
                                  @ModelAttribute Appointment appointment, 
                                  Authentication authentication) {
        try {
            logger.info("POST /appointments/book/{} - Starting save", doctorId);
            
            String username = authentication.getName();
            logger.info("User: {}", username);
            
            Optional<Patient> patient = patientService.findByUsername(username);
            Optional<Doctor> doctor = doctorService.getDoctorById(doctorId);
            
            logger.info("Patient found: {}, Doctor found: {}", patient.isPresent(), doctor.isPresent());
            
            if (patient.isPresent() && doctor.isPresent()) {
                logger.info("Setting patient and doctor on appointment");
                appointment.setPatient(patient.get());
                appointment.setDoctor(doctor.get());
                
                logger.info("Appointment date: {}", appointment.getAppointmentDate());
                
                Appointment saved = appointmentService.saveAppointment(appointment);
                logger.info("Appointment saved with ID: {}", saved.getId());
                
                return "redirect:/appointments/my-appointments";
            } else {
                logger.warn("Patient or Doctor not found");
                return "redirect:/home";
            }
        } catch (Exception e) {
            logger.error("Error saving appointment", e);
            return "redirect:/home";
        }
    }
    
    @GetMapping("/my-appointments")
    public String myAppointments(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        String username = authentication.getName();
        Optional<Patient> patient = patientService.findByUsername(username);
        
        if (patient.isPresent()) {
            List<Appointment> appointments = appointmentService.getAppointmentsByPatient(patient.get());
            model.addAttribute("appointments", appointments);
            return "my-appointments";
        }
        return "redirect:/login";
    }
}
