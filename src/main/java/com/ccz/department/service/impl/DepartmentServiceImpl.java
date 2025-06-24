package com.ccz.department.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccz.department.common.BusinessException;
import com.ccz.department.dto.DepartmentDTO;
import com.ccz.department.entity.Department;
import com.ccz.department.entity.Employee;
import com.ccz.department.mapper.DepartmentMapper;
import com.ccz.department.mapper.EmployeeMapper;
import com.ccz.department.service.DepartmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    @Resource
    private EmployeeMapper employeeMapper;

    @Override
    @Transactional
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        // 检查部门名称是否已存在
        if (checkDepartmentNameExists(departmentDTO.getName())) {
            throw new BusinessException("部门名称已存在");
        }

        // 检查父部门是否存在
        if (departmentDTO.getParentId() != null) {
            Department parent = getById(departmentDTO.getParentId());
            if (parent == null) {
                throw new BusinessException("父部门不存在");
            }
        }

        Department department = new Department();
        BeanUtils.copyProperties(departmentDTO, department);
        save(department);

        departmentDTO.setId(department.getId());
        return departmentDTO;
    }

    @Override
    @Transactional
    public DepartmentDTO updateDepartment(Long id, DepartmentDTO departmentDTO) {
        Department department = getById(id);
        if (department == null) {
            throw new BusinessException("部门不存在");
        }

        // 检查部门名称是否已存在（排除自身）
        if (!department.getName().equals(departmentDTO.getName()) 
            && checkDepartmentNameExists(departmentDTO.getName())) {
            throw new BusinessException("部门名称已存在");
        }

        BeanUtils.copyProperties(departmentDTO, department);
        department.setId(id);
        updateById(department);

        return departmentDTO;
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        Department department = getById(id);
        if (department == null) {
            throw new BusinessException("部门不存在");
        }

        // 检查是否有子部门
        if (hasSubDepartments(id)) {
            throw new BusinessException("请先删除子部门");
        }

        // 检查是否有员工
        if (hasEmployees(id)) {
            throw new BusinessException("请先转移或删除部门下的员工");
        }

        removeById(id);
    }

    @Override
    public DepartmentDTO getDepartment(Long id) {
        Department department = getById(id);
        if (department == null) {
            throw new BusinessException("部门不存在");
        }

        DepartmentDTO departmentDTO = new DepartmentDTO();
        BeanUtils.copyProperties(department, departmentDTO);
        return departmentDTO;
    }

    @Override
    public List<DepartmentDTO> getAllDepartments() {
        List<Department> departments = list();
        return departments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentDTO> getSubDepartments(Long parentId) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getParentId, parentId);
        List<Department> departments = list(wrapper);
        return departments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignManager(Long departmentId, Long managerId) {
        Department department = getById(departmentId);
        if (department == null) {
            throw new BusinessException("部门不存在");
        }

        Employee manager = employeeMapper.selectById(managerId);
        if (manager == null) {
            throw new BusinessException("员工不存在");
        }

        department.setManagerId(managerId);
        updateById(department);
    }

    private boolean checkDepartmentNameExists(String name) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getName, name);
        return count(wrapper) > 0;
    }

    private boolean hasSubDepartments(Long departmentId) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getParentId, departmentId);
        return count(wrapper) > 0;
    }

    private boolean hasEmployees(Long departmentId) {
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getDepartmentId, departmentId);
        return employeeMapper.selectCount(wrapper) > 0;
    }

    private DepartmentDTO convertToDTO(Department department) {
        DepartmentDTO dto = new DepartmentDTO();
        BeanUtils.copyProperties(department, dto);
        return dto;
    }
} 