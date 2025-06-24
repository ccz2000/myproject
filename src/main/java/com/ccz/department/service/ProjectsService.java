package com.ccz.department.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ccz.department.dto.ProjectsDTO;
import com.ccz.department.entity.Project;
import com.ccz.department.entity.ProjectMember;

import java.util.List;

/**
 * @author : Chenchunze
 * @description :
 * @createDate : 2025/5/30 10:19
 */
public interface ProjectsService extends IService<Project> {
    // 基础项目管理
    ProjectsDTO createProject(ProjectsDTO projectDTO);
    ProjectsDTO updateProject(Long id, ProjectsDTO projectDTO);
    void deleteProject(Long id);
    ProjectsDTO getProjectById(Long id);
    List<ProjectsDTO> getAllProjects();
    
    // 项目成员管理
    List<ProjectMember> getProjectMembers(Long projectId);
    void addProjectMember(ProjectMember member);
    void removeProjectMember(Long projectId, Long employeeId);
}
