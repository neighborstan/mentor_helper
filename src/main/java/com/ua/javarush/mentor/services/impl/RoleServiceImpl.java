package com.ua.javarush.mentor.services.impl;

import com.ua.javarush.mentor.command.RoleCommand;
import com.ua.javarush.mentor.dto.RoleDTO;
import com.ua.javarush.mentor.exceptions.Error;
import com.ua.javarush.mentor.exceptions.GeneralException;
import com.ua.javarush.mentor.mapper.RoleMapper;
import com.ua.javarush.mentor.persist.model.Role;
import com.ua.javarush.mentor.persist.repository.RoleRepository;
import com.ua.javarush.mentor.persist.repository.RoleToPermissionRepository;
import com.ua.javarush.mentor.services.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.ua.javarush.mentor.exceptions.GeneralExceptionUtils.createGeneralException;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {
    public static final String NOT_FOUND_ROLE_ERROR = "Didn't found role";
    public static final String LOG_RESPONSE_ROLE = "Response role: {}";

    private final RoleRepository roleRepository;
    private final RoleToPermissionRepository roleToPermissionRepository;
    private final RoleMapper roleMapper;

    public RoleServiceImpl(RoleRepository roleRepository, RoleToPermissionRepository roleToPermissionRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleToPermissionRepository = roleToPermissionRepository;
        this.roleMapper = roleMapper;
    }


    @Override
    @Transactional(rollbackFor = GeneralException.class)
    public RoleDTO createRole(RoleCommand roleCommand) {
        Role newRole = roleMapper.mapToEntity(roleCommand);
        roleRepository.save(newRole);
        log.info("Create new role with name {}" + newRole.getName());
        return roleMapper.mapToDto(roleRepository.save(newRole));
    }

    @Override
    public RoleDTO getRoleById(Long id) throws GeneralException {
        Role role = fetchRole(id);
        log.info(LOG_RESPONSE_ROLE, role.getName());
        return roleMapper.mapToDto(role);
    }

    @Override
    public Map<String, Object> getAllRoles(int page, int size, String sortBy) {
        return null;
    }

    @Override
    public RoleDTO getRolePermissionById(Long roleId) throws GeneralException {
        return null;
    }

    @Override
    public RoleDTO addPermissionToRole(Long roleId) throws GeneralException {
        return null;
    }

    @Override
    public RoleDTO removeRole(Long roleId) throws GeneralException {
        return null;
    }

    @Override
    public RoleDTO removePermissionInRole(Long roleId) throws GeneralException {
        return null;
    }

    @NotNull
    @Override
    public Role fetchRole(Long roleId) throws GeneralException {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> createGeneralException(NOT_FOUND_ROLE_ERROR, HttpStatus.NOT_FOUND, Error.ROLE_NOT_FOUND));
    }
}
