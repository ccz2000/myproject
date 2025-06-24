package com.ccz.department.controller;

import com.ccz.department.annotation.RequirePermission;
import com.ccz.department.common.Result;
import com.ccz.department.dto.ProjectsDTO;
import com.ccz.department.entity.ProjectMember;
import com.ccz.department.service.ProjectsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author : Chenchunze
 * @description :
 * @createDate : 2025/5/30 10:50
 */
@RestController
@RequestMapping("/projects")
public class ProjectsController {

    @Resource
    private ProjectsService projectsService;

    @GetMapping
    public Result<List<ProjectsDTO>> list() {
        return Result.success(projectsService.getAllProjects());
    }

    @GetMapping("/{id}")
    public Result<ProjectsDTO> getById(@PathVariable Long id) {
        return Result.success(projectsService.getProjectById(id));
    }

    @PostMapping
    @RequirePermission(manageProjects = true)
    public Result<ProjectsDTO> create(@Valid @RequestBody ProjectsDTO projectDTO) {
        return Result.success(projectsService.createProject(projectDTO));
    }

    @PutMapping("/{id}")
    @RequirePermission(manageProjects = true)
    public Result<ProjectsDTO> update(@PathVariable Long id, @Valid @RequestBody ProjectsDTO projectDTO) {
        return Result.success(projectsService.updateProject(id, projectDTO));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(manageProjects = true)
    public Result<Boolean> delete(@PathVariable Long id) {
        projectsService.deleteProject(id);
        return Result.success(true);
    }

    @GetMapping("/{projectId}/members")
    public Result<List<ProjectMember>> getMembers(@PathVariable Long projectId) {
        return Result.success(projectsService.getProjectMembers(projectId));
    }

    @PostMapping("/{projectId}/members")
    @RequirePermission(manageProjects = true)
    public Result<Boolean> addMember(@PathVariable Long projectId, @Valid @RequestBody ProjectMember member) {
        member.setProjectId(projectId);
        projectsService.addProjectMember(member);
        return Result.success(true);
    }

    @DeleteMapping("/{projectId}/members/{employeeId}")
    @RequirePermission(manageProjects = true)
    public Result<Boolean> removeMember(@PathVariable Long projectId, @PathVariable Long employeeId) {
        projectsService.removeProjectMember(projectId, employeeId);
        return Result.success(true);
    }
}
