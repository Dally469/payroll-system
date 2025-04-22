package com.payroll.payroll_system.dto;


import com.payroll.payroll_system.constant.ActivityType;
import lombok.*;

@Data
public class ActivityCreateDTO {
    private String description;
    private ActivityType type;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ActivityType getType() {
        return type;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }
}
