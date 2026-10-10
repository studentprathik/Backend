package com.projectfinder.config;

import com.projectfinder.entity.*;
import com.projectfinder.repository.*;
import com.projectfinder.service.SkillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SkillService skillService;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRequestRepository teamRequestRepository;

    public DataInitializer(UserRepository userRepository,
                           SkillRepository skillRepository,
                           SkillService skillService,
                           ProjectRepository projectRepository,
                           TeamMemberRepository teamMemberRepository,
                           TeamRequestRepository teamRequestRepository) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.skillService = skillService;
        this.projectRepository = projectRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.teamRequestRepository = teamRequestRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already initialized with demo data.");
            return;
        }

        log.info("Initializing demo data for Project Team Finder...");

        // 1. Create Skills
        List<String> skillNames = Arrays.asList(
                "Java", "React", "Spring Boot", "MySQL", "Python",
                "Machine Learning", "HTML", "CSS", "JavaScript",
                "Node.js", "Docker", "Git"
        );
        for (String name : skillNames) {
            skillService.getOrCreateSkill(name);
        }

        // 2. Create Students
        User prathik = new User("Prathik", "prathik@college.edu", "password123", "Computer Science", "3rd Year");
        prathik.setBio("Passionate Full Stack Developer aiming to build scalable web applications.");
        prathik.setInterests("Web Architecture, Cloud Computing, Distributed Systems");
        prathik.setPreviousProjects("College Portal, Portfolio Website");
        userRepository.save(prathik);
        skillService.updateUserSkills(prathik.getId(), Arrays.asList("Java", "React", "Spring Boot", "MySQL", "Git"));

        User rahul = new User("Rahul", "rahul@college.edu", "password123", "Information Technology", "3rd Year");
        rahul.setBio("Frontend enthusiast and database enthusiast looking for exciting team projects.");
        rahul.setInterests("UI/UX Design, Relational Databases, API Integration");
        rahul.setPreviousProjects("Library Management System, Quiz App");
        userRepository.save(rahul);
        skillService.updateUserSkills(rahul.getId(), Arrays.asList("Java", "React", "MySQL", "Python", "HTML", "CSS"));

        User sai = new User("Sai", "sai@college.edu", "password123", "Computer Science", "4th Year");
        sai.setBio("AI and Data Science student specializing in deep learning and computer vision.");
        sai.setInterests("Artificial Intelligence, Neural Networks, Computer Vision");
        sai.setPreviousProjects("Object Detection Model, Handwritten Digit Recognizer");
        userRepository.save(sai);
        skillService.updateUserSkills(sai.getId(), Arrays.asList("Python", "Machine Learning", "MySQL", "Git"));

        User pavan = new User("Pavan", "pavan@college.edu", "password123", "Electronics & Communication", "3rd Year");
        pavan.setBio("Exploring IoT and Full Stack Development. Enthusiastic about smart campus devices.");
        pavan.setInterests("Embedded Systems, IoT, Web Dashboards");
        pavan.setPreviousProjects("IoT Temperature Monitor, Smart Dustbin");
        userRepository.save(pavan);
        skillService.updateUserSkills(pavan.getId(), Arrays.asList("Java", "Spring Boot", "HTML", "CSS", "MySQL"));

        User varun = new User("Varun", "varun@college.edu", "password123", "Data Science", "2nd Year");
        varun.setBio("Frontend developer focusing on responsive user experiences and productivity tools.");
        varun.setInterests("Frontend Engineering, UI Animations, Mobile Web");
        varun.setPreviousProjects("Personal Task Tracker, Markdown Note Taker");
        userRepository.save(varun);
        skillService.updateUserSkills(varun.getId(), Arrays.asList("React", "JavaScript", "HTML", "CSS", "Python"));

        // 3. Create Sample Projects
        // Project 1: Smart Campus Assistant (Owner: Prathik)
        Project p1 = new Project(
                "Smart Campus Assistant",
                "A unified web application for managing campus services, navigation, lost-and-found, and student event notifications.",
                "Web Development",
                4,
                prathik
        );
        projectRepository.save(p1);
        skillService.setProjectSkills(p1, Arrays.asList("React", "Java", "Spring Boot", "MySQL"));
        teamMemberRepository.save(new TeamMember(p1, prathik, "OWNER"));

        // Project 2: Student Expense Tracker (Owner: Rahul)
        Project p2 = new Project(
                "Student Expense Tracker",
                "A budget management and shared apartment expense splitting application designed specifically for college roommates.",
                "Web Development",
                3,
                rahul
        );
        projectRepository.save(p2);
        skillService.setProjectSkills(p2, Arrays.asList("React", "JavaScript", "MySQL", "HTML"));
        teamMemberRepository.save(new TeamMember(p2, rahul, "OWNER"));
        // Varun joined Rahul's project
        teamMemberRepository.save(new TeamMember(p2, varun, "MEMBER"));

        // Project 3: AI Waste Classification (Owner: Sai)
        Project p3 = new Project(
                "AI Waste Classification",
                "An intelligent recycling system that detects and sorts recyclable, organic, and hazardous waste using camera feeds.",
                "Artificial Intelligence",
                4,
                sai
        );
        projectRepository.save(p3);
        skillService.setProjectSkills(p3, Arrays.asList("Python", "Machine Learning", "React", "MySQL"));
        teamMemberRepository.save(new TeamMember(p3, sai, "OWNER"));

        // Project 4: College Event Management System (Owner: Pavan)
        Project p4 = new Project(
                "College Event Management System",
                "A comprehensive portal for departmental fests, workshop registrations, ticket generation, and team competitions.",
                "Full Stack Development",
                4,
                pavan
        );
        projectRepository.save(p4);
        skillService.setProjectSkills(p4, Arrays.asList("Java", "Spring Boot", "HTML", "CSS", "MySQL"));
        teamMemberRepository.save(new TeamMember(p4, pavan, "OWNER"));

        // Project 5: Student Study Planner (Owner: Varun)
        Project p5 = new Project(
                "Student Study Planner",
                "A collaborative revision schedule and pomodoro study planner with exam syllabus progress trackers.",
                "Productivity & EdTech",
                3,
                varun
        );
        projectRepository.save(p5);
        skillService.setProjectSkills(p5, Arrays.asList("React", "JavaScript", "CSS"));
        teamMemberRepository.save(new TeamMember(p5, varun, "OWNER"));

        // 4. Create Sample Requests
        // Rahul requested to join Prathik's Smart Campus Assistant (PENDING) -> 75% match (React, Java, MySQL)
        TeamRequest req1 = new TeamRequest(p1, rahul);
        req1.setStatus(RequestStatus.PENDING);
        teamRequestRepository.save(req1);

        // Pavan requested to join Prathik's Smart Campus Assistant (PENDING) -> 75% match (Java, Spring Boot, MySQL)
        TeamRequest req2 = new TeamRequest(p1, pavan);
        req2.setStatus(RequestStatus.PENDING);
        teamRequestRepository.save(req2);

        // Varun requested to join Rahul's Expense Tracker and was ACCEPTED
        TeamRequest req3 = new TeamRequest(p2, varun);
        req3.setStatus(RequestStatus.ACCEPTED);
        teamRequestRepository.save(req3);

        log.info("Demo data successfully created with 5 users, 12 skills, 5 projects, and initial requests!");
    }
}
