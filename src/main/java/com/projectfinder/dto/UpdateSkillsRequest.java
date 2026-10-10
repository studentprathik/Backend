package com.projectfinder.dto;

import java.util.ArrayList;
import java.util.List;

public class UpdateSkillsRequest {
    private List<String> skills = new ArrayList<>();

    public UpdateSkillsRequest() {
    }

    public UpdateSkillsRequest(List<String> skills) {
        this.skills = skills;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }
}
