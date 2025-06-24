package com.ccz.department.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccz.department.common.BusinessException;
import com.ccz.department.dto.SalaryDTO;
import com.ccz.department.entity.Salary;
import com.ccz.department.entity.Employee;
import com.ccz.department.mapper.SalaryMapper;
import com.ccz.department.mapper.EmployeeMapper;
import com.ccz.department.service.SalaryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : Chenchunze
 * @description : 薪资服务实现类
 * @createDate : 2025/6/11
 */
@Service
public class SalaryServiceImpl extends ServiceImpl<SalaryMapper, Salary> implements SalaryService {

    @Resource
    private SalaryMapper salaryMapper;

    @Resource
    private EmployeeMapper employeeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SalaryDTO createSalary(SalaryDTO salaryDTO) {
        // 检查员工是否存在
        Employee employee = employeeMapper.selectById(salaryDTO.getEmployeeId());
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }

        // 检查是否已存在该月份的工资记录
        Salary existingSalary = salaryMapper.selectByEmployeeAndMonth(
                salaryDTO.getEmployeeId(), salaryDTO.getSalaryMonth());
        if (existingSalary != null) {
            throw new BusinessException("该月份的工资记录已存在");
        }

        // 设置部门ID
        salaryDTO.setDepartmentId(employee.getDepartmentId());

        // 计算实发工资
        calculateNetSalary(salaryDTO);

        // 转换并保存
        Salary salary = new Salary();
        BeanUtils.copyProperties(salaryDTO, salary);
        salary.setPaymentStatus(0); // 默认未发放
        
        salaryMapper.insert(salary);
        
        // 返回创建的记录
        return getSalaryById(salary.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SalaryDTO updateSalary(Long id, SalaryDTO salaryDTO) {
        Salary salary = salaryMapper.selectById(id);
        if (salary == null) {
            throw new BusinessException("薪资记录不存在");
        }

        // 如果已发放，不允许修改
        if (salary.getPaymentStatus() == 1) {
            throw new BusinessException("薪资已发放，不能修改");
        }

        // 计算实发工资
        calculateNetSalary(salaryDTO);

        // 更新记录
        BeanUtils.copyProperties(salaryDTO, salary);
        salaryMapper.updateById(salary);

        return getSalaryById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSalary(Long id) {
        Salary salary = salaryMapper.selectById(id);
        if (salary == null) {
            throw new BusinessException("薪资记录不存在");
        }

        // 如果已发放，不允许删除
        if (salary.getPaymentStatus() == 1) {
            throw new BusinessException("薪资已发放，不能删除");
        }

        salaryMapper.deleteById(id);
    }

    @Override
    public SalaryDTO getSalaryById(Long id) {
        Salary salary = salaryMapper.selectById(id);
        if (salary == null) {
            throw new BusinessException("薪资记录不存在");
        }

        return convertToDTO(salary);
    }

    @Override
    public SalaryDTO getSalaryByEmployeeAndMonth(Long employeeId, LocalDate month) {
        Salary salary;
        if (month != null) {
            salary = salaryMapper.selectByEmployeeAndMonth(employeeId, month);
        } else {
            // 如果没有指定月份，获取该员工最新的薪资记录
            QueryWrapper<Salary> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("employee_id", employeeId)
                       .orderByDesc("salary_month")
                       .last("LIMIT 1");
            salary = salaryMapper.selectOne(queryWrapper);
        }
        
        if (salary == null) {
            throw new BusinessException("未找到薪资记录");
        }
        
        return convertToDTO(salary);
    }

    @Override
    public List<SalaryDTO> getSalariesByDepartmentAndMonth(Long departmentId, LocalDate month) {
        List<Salary> salaries;
        if (month != null) {
            salaries = salaryMapper.selectByDepartmentAndMonth(departmentId, month);
        } else {
            // 如果没有指定月份，使用自定义查询获取部门所有薪资记录
            salaries = salaryMapper.selectByDepartmentAndMonth(departmentId, null);
        }
        return salaries.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<SalaryDTO> getSalariesByMonth(LocalDate month) {
        List<Salary> salaries;
        if (month != null) {
            salaries = salaryMapper.selectByMonth(month);
        } else {
            // 如果没有指定月份，使用自定义查询获取所有薪资记录
            salaries = salaryMapper.selectByMonth(null);
        }
        return salaries.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void paySalary(Long id) {
        Salary salary = salaryMapper.selectById(id);
        if (salary == null) {
            throw new BusinessException("薪资记录不存在");
        }

        if (salary.getPaymentStatus() == 1) {
            throw new BusinessException("薪资已发放");
        }

        salary.setPaymentStatus(1);
        salary.setPaymentDate(LocalDateTime.now());
        salaryMapper.updateById(salary);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchPaySalaries(List<Long> ids) {
        for (Long id : ids) {
            paySalary(id);
        }
    }

    @Override
    public void calculateNetSalary(SalaryDTO salaryDTO) {
        // 实发工资 = 基本工资 + 奖金 + 加班费 - 社保扣除 - 个税扣除
        BigDecimal netSalary = salaryDTO.getBaseSalary()
                .add(salaryDTO.getBonus() != null ? salaryDTO.getBonus() : BigDecimal.ZERO)
                .add(salaryDTO.getOvertimePay() != null ? salaryDTO.getOvertimePay() : BigDecimal.ZERO)
                .subtract(salaryDTO.getSocialSecurity() != null ? salaryDTO.getSocialSecurity() : BigDecimal.ZERO)
                .subtract(salaryDTO.getTax() != null ? salaryDTO.getTax() : BigDecimal.ZERO);
        
        salaryDTO.setNetSalary(netSalary);
    }

    private SalaryDTO convertToDTO(Salary salary) {
        if (salary == null) {
            return null;
        }

        SalaryDTO dto = new SalaryDTO();
        BeanUtils.copyProperties(salary, dto);
        
        // 直接映射额外字段（MyBatis Plus 会自动将别名映射到实体类对应字段）
        if (salary.getEmployeeName() != null) {
            dto.setEmployeeName(salary.getEmployeeName());
        }
        if (salary.getDepartmentName() != null) {
            dto.setDepartmentName(salary.getDepartmentName());
        }
        if (salary.getPosition() != null) {
            dto.setPosition(salary.getPosition());
        }
        
        return dto;
    }
} 