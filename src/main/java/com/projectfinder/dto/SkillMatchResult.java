package com.projectfinder.dto;

import java.util.ArrayList;
import java.util.List;

public class SkillMatchResult {
    private int percentage;
    private List<String> matched = new ArrayList<>();
    private List<String> missing = new ArrayList<>();

    public SkillMatchResult() {
    }

    public SkillMatchResult(int percentage, List<String> matched, List<String> missing) {
        this.percentage = percentage;
        this.matched = matched;
        this.missing = missing;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public List<String> getMatched() {
        return matched;
    }

    public void setMatched(List<String> matched) {
        this.matched = matched;
    }

    public List<String> getMissing() {
        return missing;
    }

    public void setMissing(List<String> missing) {
        this.missing = missing;
    }
}
