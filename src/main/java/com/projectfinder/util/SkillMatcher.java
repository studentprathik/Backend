package com.projectfinder.util;

import com.projectfinder.dto.SkillMatchResult;

import java.util.*;

public class SkillMatcher {

    public static SkillMatchResult calculate(List<String> requiredSkills, List<String> studentSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            return new SkillMatchResult(100, Collections.emptyList(), Collections.emptyList());
        }

        Set<String> normalizedStudentSkills = new HashSet<>();
        if (studentSkills != null) {
            for (String s : studentSkills) {
                if (s != null && !s.trim().isEmpty()) {
                    normalizedStudentSkills.add(s.trim().toLowerCase());
                }
            }
        }

        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String req : requiredSkills) {
            if (req == null || req.trim().isEmpty()) continue;
            String normalizedReq = req.trim().toLowerCase();
            if (normalizedStudentSkills.contains(normalizedReq)) {
                matched.add(req.trim());
            } else {
                missing.add(req.trim());
            }
        }

        int totalRequired = requiredSkills.size();
        int matchedCount = matched.size();
        int percentage = totalRequired > 0 ? (int) Math.round(((double) matchedCount / totalRequired) * 100.0) : 0;

        return new SkillMatchResult(percentage, matched, missing);
    }
}
