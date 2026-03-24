package com.example.demo.config;

import com.example.demo.model.Department;
import com.example.demo.model.Doctor;
import com.example.demo.model.Patient;
import com.example.demo.model.Role;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.PatientRepository;
import com.example.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        try {
            // Tạo Roles
            Role adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> {
                Role role = new Role();
                role.setName("ADMIN");
                return roleRepository.save(role);
            });
            
            Role patientRole = roleRepository.findByName("PATIENT").orElseGet(() -> {
                Role role = new Role();
                role.setName("PATIENT");
                return roleRepository.save(role);
            });
            
            System.out.println("✓ Roles initialized");
            
            // Tạo Departments
            Department deptNgoai = departmentRepository.findAll().stream()
                .filter(d -> d.getName().equals("Khoa Ngoại")).findFirst().orElseGet(() -> {
                    Department d = new Department();
                    d.setName("Khoa Ngoại");
                    return departmentRepository.save(d);
                });
            
            Department deptTim = departmentRepository.findAll().stream()
                .filter(d -> d.getName().equals("Khoa Tim mạch")).findFirst().orElseGet(() -> {
                    Department d = new Department();
                    d.setName("Khoa Tim mạch");
                    return departmentRepository.save(d);
                });
            
            Department deptNha = departmentRepository.findAll().stream()
                .filter(d -> d.getName().equals("Khoa Nha khoa")).findFirst().orElseGet(() -> {
                    Department d = new Department();
                    d.setName("Khoa Nha khoa");
                    return departmentRepository.save(d);
                });
            
            System.out.println("✓ Departments initialized");
            
            // Tạo Admin account
            if (!patientRepository.findByUsername("admin").isPresent()) {
                Patient admin = new Patient();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEmail("admin@clinic.com");
                
                Set<Role> adminRoles = new HashSet<>();
                adminRoles.add(adminRole);
                admin.setRoles(adminRoles);
                
                patientRepository.save(admin);
                System.out.println("✓ Tạo admin account: admin / admin123");
            }
            
            // Tạo User account
            if (!patientRepository.findByUsername("user").isPresent()) {
                Patient user = new Patient();
                user.setUsername("user");
                user.setPassword(passwordEncoder.encode("user123"));
                user.setEmail("user@clinic.com");
                
                Set<Role> userRoles = new HashSet<>();
                userRoles.add(patientRole);
                user.setRoles(userRoles);
                
                patientRepository.save(user);
                System.out.println("✓ Tạo user account: user / user123");
            }
            
            // Tạo sample Doctors
            if (doctorRepository.count() < 3) {
                // Doctor 1
                if (doctorRepository.findAll().stream()
                    .noneMatch(d -> d.getName().equals("Nguyễn Văn A"))) {
                    Doctor doctor1 = new Doctor();
                    doctor1.setName("Nguyễn Văn A");
                    doctor1.setSpecialty("Bác sĩ Ngoại khoa");
                    doctor1.setImage("https://i.pravatar.cc/150?img=1");
                    doctor1.setDepartment(deptNgoai);
                    doctorRepository.save(doctor1);
                    System.out.println("✓ Tạo bác sĩ: Nguyễn Văn A");
                }
                
                // Doctor 2
                if (doctorRepository.findAll().stream()
                    .noneMatch(d -> d.getName().equals("Trần Thị B"))) {
                    Doctor doctor2 = new Doctor();
                    doctor2.setName("Trần Thị B");
                    doctor2.setSpecialty("Chuyên gia Tim mạch");
                    doctor2.setImage("https://i.pravatar.cc/150?img=2");
                    doctor2.setDepartment(deptTim);
                    doctorRepository.save(doctor2);
                    System.out.println("✓ Tạo bác sĩ: Trần Thị B");
                }
                
                // Doctor 3
                if (doctorRepository.findAll().stream()
                    .noneMatch(d -> d.getName().equals("Lê Văn C"))) {
                    Doctor doctor3 = new Doctor();
                    doctor3.setName("Lê Văn C");
                    doctor3.setSpecialty("Nha sĩ chuyên khoa");
                    doctor3.setImage("https://i.pravatar.cc/150?img=3");
                    doctor3.setDepartment(deptNha);
                    doctorRepository.save(doctor3);
                    System.out.println("✓ Tạo bác sĩ: Lê Văn C");
                }
            }
            
            System.out.println("\n========== DATABASE INITIALIZED ==========");
            System.out.println("Admin: admin / admin123");
            System.out.println("User:  user / user123");
            System.out.println("==========================================\n");
        } catch (Exception e) {
            System.out.println("❌ Lỗi DataInitializer: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
