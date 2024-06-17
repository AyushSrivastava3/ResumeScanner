package com.example.job_desc_backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Skill {
    private String skill;
    private int experience;
    private List<String> subSkills;

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public List<String> getSubSkills() {
        return subSkills;
    }

    public void setSubSkills(List<String> subSkills) {
        this.subSkills = subSkills;
    }
}
