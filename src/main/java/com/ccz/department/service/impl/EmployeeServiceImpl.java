package com.ccz.department.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccz.department.common.BusinessException;
import com.ccz.department.dto.EmployeeDTO;
import com.ccz.department.entity.Department;
import com.ccz.department.entity.Employee;
import com.ccz.department.mapper.DepartmentMapper;
import com.ccz.department.mapper.EmployeeMapper;
import com.ccz.department.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Resource
    private DepartmentMapper departmentMapper;

    @Override
    @Transactional
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        // 检查邮箱是否已存在
        if (checkEmailExists(employeeDTO.getEmail())) {
            throw new BusinessException("邮箱已存在");
        }

        // 检查部门是否存在
        checkDepartmentExists(employeeDTO.getDepartmentId());

        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        save(employee);

        employeeDTO.setId(employee.getId());
        return employeeDTO;
    }

    @Override
    @Transactional
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {
        Employee employee = getById(id);
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }

        // 检查邮箱是否已存在（排除自身）
        if (!employee.getEmail().equals(employeeDTO.getEmail()) 
            && checkEmailExists(employeeDTO.getEmail())) {
            throw new BusinessException("邮箱已存在");
        }

        // 检查部门是否存在
        checkDepartmentExists(employeeDTO.getDepartmentId());

        BeanUtils.copyProperties(employeeDTO, employee);
        employee.setId(id);
        updateById(employee);

        return employeeDTO;
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = getById(id);
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }

        // 检查是否是部门经理
        if (isManager(id)) {
            throw new BusinessException("该员工是部门经理，请先解除管理职务");
        }

        removeById(id);
    }

    @Override
    public EmployeeDTO getEmployee(Long id) {
        Employee employee = getById(id);
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }

        return convertToDTO(employee);
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        List<Employee> employees = list();
        return employees.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDTO> getEmployeesByDepartment(Long departmentId) {
        checkDepartmentExists(departmentId);

        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getDepartmentId, departmentId);
        List<Employee> employees = list(wrapper);
        return employees.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void transferEmployee(Long employeeId, Long newDepartmentId) {
        Employee employee = getById(employeeId);
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }

        checkDepartmentExists(newDepartmentId);

        employee.setDepartmentId(newDepartmentId);
        updateById(employee);
    }

    private boolean checkEmailExists(String email) {
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getEmail, email);
        return count(wrapper) > 0;
    }

    private void checkDepartmentExists(Long departmentId) {
        Department department = departmentMapper.selectById(departmentId);
        if (department == null) {
            throw new BusinessException("部门不存在");
        }
    }

    private boolean isManager(Long employeeId) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getManagerId, employeeId);
        return departmentMapper.selectCount(wrapper) > 0;
    }

    private EmployeeDTO convertToDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        BeanUtils.copyProperties(employee, dto);
        return dto;
    }
} 