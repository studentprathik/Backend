package com.projectfinder.controller;

import com.projectfinder.dto.ApiResponse;
import com.projectfinder.entity.Skill;
import com.projectfinder.service.SkillService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Skill>>> getAllSkills() {
        List<Skill> skills = skillService.getAllSkills();
        return ResponseEntity.ok(ApiResponse.ok("Skills retrieved successfully", skills));
    }
}
