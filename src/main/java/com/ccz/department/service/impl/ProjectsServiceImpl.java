package com.ccz.department.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccz.department.common.BusinessException;
import com.ccz.department.dto.ProjectsDTO;
import com.ccz.department.entity.Department;
import com.ccz.department.entity.Employee;
import com.ccz.department.entity.Project;
import com.ccz.department.entity.ProjectMember;
import com.ccz.department.mapper.DepartmentMapper;
import com.ccz.department.mapper.EmployeeMapper;
import com.ccz.department.mapper.ProjectMemberMapper;
import com.ccz.department.mapper.ProjectsMapper;
import com.ccz.department.service.ProjectsService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : Chenchunze
 * @description :
 * @createDate : 2025/5/30 10:24
 */
@Service
public class ProjectsServiceImpl extends ServiceImpl<ProjectsMapper, Project> implements ProjectsService {

    @Resource
    private ProjectsMapper projectsMapper;

    @Resource
    private ProjectMemberMapper projectMemberMapper;

    @Resource
    private DepartmentMapper departmentMapper;

    @Resource
    private EmployeeMapper employeeMapper;

    @Override
    @Transactional
    public ProjectsDTO createProject(ProjectsDTO projectDTO) {
        // 验证部门和负责人是否存在
        validateDepartmentAndLeader(projectDTO);
        
        // 转换DTO为实体
        Project project = new Project();
        BeanUtils.copyProperties(projectDTO, project);
        
        // 保存项目
        save(project);
        
        // 返回创建后的项目信息
        return getProjectById(project.getId());
    }

    @Override
    @Transactional
    public ProjectsDTO updateProject(Long id, ProjectsDTO projectDTO) {
        // 检查项目是否存在
        Project existingProject = getById(id);
        if (existingProject == null) {
            throw new BusinessException("项目不存在");
        }
        
        // 验证部门和负责人
        validateDepartmentAndLeader(projectDTO);
        
        // 更新项目信息
        BeanUtils.copyProperties(projectDTO, existingProject);
        updateById(existingProject);
        
        // 返回更新后的项目信息
        return getProjectById(id);
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        // 检查项目是否存在
        if (!removeById(id)) {
            throw new BusinessException("项目不存在或删除失败");
        }
        
        // 删除相关的项目成员记录
        projectMemberMapper.delete(
            new LambdaQueryWrapper<ProjectMember>()
                .eq(ProjectMember::getProjectId, id)
        );
    }

    @Override
    public ProjectsDTO getProjectById(Long id) {
        Project project = getById(id);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }
        return convertToDTO(project);
    }

    @Override
    public List<ProjectsDTO> getAllProjects() {
        List<Project> projects = list();
        return projects.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectMember> getProjectMembers(Long projectId) {
        return projectMemberMapper.selectList(
            new LambdaQueryWrapper<ProjectMember>()
                .eq(ProjectMember::getProjectId, projectId)
        );
    }

    @Override
    @Transactional
    public void addProjectMember(ProjectMember member) {
        // 验证项目是否存在
        if (getById(member.getProjectId()) == null) {
            throw new BusinessException("项目不存在");
        }
        
        // 验证员工是否存在
        if (employeeMapper.selectById(member.getEmployeeId()) == null) {
            throw new BusinessException("员工不存在");
        }
        
        // 检查是否已经是项目成员
        if (projectMemberMapper.selectCount(
                new LambdaQueryWrapper<ProjectMember>()
                    .eq(ProjectMember::getProjectId, member.getProjectId())
                    .eq(ProjectMember::getEmployeeId, member.getEmployeeId())
            ) > 0) {
            throw new BusinessException("该员工已经是项目成员");
        }
        
        // 如果没有设置加入日期，使用当前日期
        if (member.getJoinedDate() == null) {
            member.setJoinedDate(java.time.LocalDate.now());
        }
        
        projectMemberMapper.insert(member);
    }

    @Override
    @Transactional
    public void removeProjectMember(Long projectId, Long employeeId) {
        if (projectMemberMapper.delete(
                new LambdaQueryWrapper<ProjectMember>()
                    .eq(ProjectMember::getProjectId, projectId)
                    .eq(ProjectMember::getEmployeeId, employeeId)
            ) == 0) {
            throw new BusinessException("项目成员不存在或删除失败");
        }
    }

    private void validateDepartmentAndLeader(ProjectsDTO projectDTO) {
        // 验证部门是否存在
        if (projectDTO.getDepartmentId() != null && 
            departmentMapper.selectById(projectDTO.getDepartmentId()) == null) {
            throw new BusinessException("所选部门不存在");
        }
        
        // 验证负责人是否存在
        if (projectDTO.getLeaderId() != null && 
            employeeMapper.selectById(projectDTO.getLeaderId()) == null) {
            throw new BusinessException("所选负责人不存在");
        }
    }

    private ProjectsDTO convertToDTO(Project project) {
        ProjectsDTO dto = new ProjectsDTO();
        BeanUtils.copyProperties(project, dto);
        
        // 设置额外的展示字段
        if (project.getDepartmentId() != null) {
            Department department = departmentMapper.selectById(project.getDepartmentId());
            if (department != null) {
                dto.setDepartmentName(department.getName());
            }
        }
        
        if (project.getLeaderId() != null) {
            Employee leader = employeeMapper.selectById(project.getLeaderId());
            if (leader != null) {
                dto.setLeaderName(leader.getName());
            }
        }
        
        return dto;
    }
}
