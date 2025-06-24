package com.ccz.department.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ccz.department.entity.Department;
import com.ccz.department.dto.DepartmentDTO;
import java.util.List;

public interface DepartmentService extends IService<Department> {
    DepartmentDTO createDepartment(DepartmentDTO departmentDTO);
    
    DepartmentDTO updateDepartment(Long id, DepartmentDTO departmentDTO);
    
    void deleteDepartment(Long id);
    
    DepartmentDTO getDepartment(Long id);
    
    List<DepartmentDTO> getAllDepartments();
    
    List<DepartmentDTO> getSubDepartments(Long parentId);
    
    void assignManager(Long departmentId, Long managerId);
} 