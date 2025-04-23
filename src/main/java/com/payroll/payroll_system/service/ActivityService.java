package com.payroll.payroll_system.service;

import com.payroll.payroll_system.dto.ActivityCreateDTO;
import com.payroll.payroll_system.dto.ActivityDTO;
import com.payroll.payroll_system.entity.Activity;
import com.payroll.payroll_system.repository.ActivityRepository;
import com.payroll.payroll_system.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ActivityService {
    @Autowired
    private ActivityRepository activityRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public List<ActivityDTO> getActivitiesByEmployeeId(UUID employeeId) {
        return activityRepository.findByEmployeeId(employeeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<ActivityDTO> startActivity(UUID employeeId, ActivityCreateDTO activityDTO) {
        return employeeRepository.findById(employeeId)
                .map(employee -> {
                    Activity activity = new Activity();
                    activity.setEmployee(employee);
                    activity.setDescription(activityDTO.getDescription());
                    activity.setType(activityDTO.getType());
                    activity.setStartTime(LocalDateTime.now());
                    return convertToDTO(activityRepository.save(activity));
                });
    }

    @Transactional
    public Optional<ActivityDTO> endActivity(UUID activityId) {
        return activityRepository.findById(activityId)
                .map(activity -> {
                    activity.setEndTime(LocalDateTime.now());
                    return convertToDTO(activityRepository.save(activity));
                });
    }

    public List<ActivityDTO> getActivitiesByOrganization(UUID organizationId) {
        return activityRepository.findByEmployeeOrganizationId(organizationId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ActivityDTO> getActivitiesByEmployeeIdAndOrganization(UUID employeeId, UUID organizationId) {
        return activityRepository.findByEmployeeIdAndEmployeeOrganizationId(employeeId, organizationId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ActivityDTO> startActivityForOrganization(UUID employeeId, ActivityCreateDTO activityDTO, UUID organizationId) {
        return employeeRepository.findByIdAndOrganizationId(employeeId, organizationId)
                .map(employee -> {
                    Activity activity = createActivityFromDTO(activityDTO);
                    activity.setEmployee(employee);
                    activity.setStartTime(LocalDateTime.now());
                    Activity savedActivity = activityRepository.save(activity);
                    return convertToDTO(savedActivity);
                });
    }

    public Optional<ActivityDTO> endActivityForOrganization(UUID activityId, UUID organizationId) {
        return activityRepository.findByIdAndEmployeeOrganizationId(activityId, organizationId)
                .map(activity -> {
                    activity.setEndTime(LocalDateTime.now());
                    Activity updatedActivity = activityRepository.save(activity);
                    return convertToDTO(updatedActivity);
                });
    }
    
    private Activity createActivityFromDTO(ActivityCreateDTO dto) {
        Activity activity = new Activity();
        activity.setDescription(dto.getDescription());
        activity.setType(dto.getType());
        return activity;
    }

    private ActivityDTO convertToDTO(Activity activity) {
        ActivityDTO activityDTO = new ActivityDTO();
        activityDTO.setId(activity.getId());
        activityDTO.setEmployeeId(activity.getEmployee().getId());
        activityDTO.setDescription(activity.getDescription());
        activityDTO.setType(activity.getType());
        activityDTO.setStartTime(activity.getStartTime());
        activityDTO.setEndTime(activity.getEndTime());
        activityDTO.setDurationInMinutes(activity.getDurationInMinutes());

        return activityDTO;
    }
}
