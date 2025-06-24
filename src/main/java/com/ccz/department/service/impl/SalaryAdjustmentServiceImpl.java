package com.ccz.department.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccz.department.common.BusinessException;
import com.ccz.department.dto.SalaryAdjustmentDTO;
import com.ccz.department.entity.SalaryAdjustment;
import com.ccz.department.entity.Employee;
import com.ccz.department.mapper.SalaryAdjustmentMapper;
import com.ccz.department.mapper.EmployeeMapper;
import com.ccz.department.service.SalaryAdjustmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : Chenchunze
 * @description : 薪资调整服务实现类
 * @createDate : 2025/6/11
 */
@Service
public class SalaryAdjustmentServiceImpl extends ServiceImpl<SalaryAdjustmentMapper, SalaryAdjustment> 
        implements SalaryAdjustmentService {

    @Resource
    private SalaryAdjustmentMapper salaryAdjustmentMapper;

    @Resource
    private EmployeeMapper employeeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SalaryAdjustmentDTO createAdjustment(SalaryAdjustmentDTO adjustmentDTO) {
        // 检查员工是否存在
        Employee employee = employeeMapper.selectById(adjustmentDTO.getEmployeeId());
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }

        // 检查审批人是否存在
        Employee approver = employeeMapper.selectById(adjustmentDTO.getApproverId());
        if (approver == null) {
            throw new BusinessException("审批人不存在");
        }

        // 设置部门ID
        adjustmentDTO.setDepartmentId(employee.getDepartmentId());

        // 转换并保存
        SalaryAdjustment adjustment = new SalaryAdjustment();
        BeanUtils.copyProperties(adjustmentDTO, adjustment);
        
        salaryAdjustmentMapper.insert(adjustment);
        
        // 返回创建的记录
        return getAdjustmentById(adjustment.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SalaryAdjustmentDTO updateAdjustment(Long id, SalaryAdjustmentDTO adjustmentDTO) {
        SalaryAdjustment adjustment = salaryAdjustmentMapper.selectById(id);
        if (adjustment == null) {
            throw new BusinessException("薪资调整记录不存在");
        }

        // 如果生效日期已过，不允许修改
        if (adjustment.getEffectiveDate().isBefore(LocalDate.now())) {
            throw new BusinessException("调整已生效，不能修改");
        }

        // 更新记录
        BeanUtils.copyProperties(adjustmentDTO, adjustment);
        salaryAdjustmentMapper.updateById(adjustment);

        return getAdjustmentById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAdjustment(Long id) {
        SalaryAdjustment adjustment = salaryAdjustmentMapper.selectById(id);
        if (adjustment == null) {
            throw new BusinessException("薪资调整记录不存在");
        }

        // 如果生效日期已过，不允许删除
        if (adjustment.getEffectiveDate().isBefore(LocalDate.now())) {
            throw new BusinessException("调整已生效，不能删除");
        }

        salaryAdjustmentMapper.deleteById(id);
    }

    @Override
    public SalaryAdjustmentDTO getAdjustmentById(Long id) {
        SalaryAdjustment adjustment = salaryAdjustmentMapper.selectById(id);
        if (adjustment == null) {
            throw new BusinessException("薪资调整记录不存在");
        }

        return convertToDTO(adjustment);
    }

    @Override
    public List<SalaryAdjustmentDTO> getAdjustmentsByEmployeeId(Long employeeId) {
        List<SalaryAdjustment> adjustments = salaryAdjustmentMapper.selectByEmployeeId(employeeId);
        return adjustments.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<SalaryAdjustmentDTO> getAdjustmentsByDepartmentId(Long departmentId) {
        List<SalaryAdjustment> adjustments = salaryAdjustmentMapper.selectByDepartmentId(departmentId);
        return adjustments.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<SalaryAdjustmentDTO> getAdjustmentsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<SalaryAdjustment> adjustments = salaryAdjustmentMapper.selectByDateRange(startDate, endDate);
        return adjustments.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<SalaryAdjustmentDTO> getAllAdjustments() {
        List<SalaryAdjustment> adjustments = salaryAdjustmentMapper.selectAll();
        return adjustments.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private SalaryAdjustmentDTO convertToDTO(SalaryAdjustment adjustment) {
        if (adjustment == null) {
            return null;
        }

        SalaryAdjustmentDTO dto = new SalaryAdjustmentDTO();
        BeanUtils.copyProperties(adjustment, dto);
        
        // 直接映射额外字段（MyBatis Plus 会自动将别名映射到实体类对应字段）
        if (adjustment.getEmployeeName() != null) {
            dto.setEmployeeName(adjustment.getEmployeeName());
        }
        if (adjustment.getDepartmentName() != null) {
            dto.setDepartmentName(adjustment.getDepartmentName());
        }
        if (adjustment.getPosition() != null) {
            dto.setPosition(adjustment.getPosition());
        }
        if (adjustment.getApproverName() != null) {
            dto.setApproverName(adjustment.getApproverName());
        }
        
        return dto;
    }
} 