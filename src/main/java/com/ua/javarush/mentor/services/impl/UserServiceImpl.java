package com.ua.javarush.mentor.services.impl;

import com.ua.javarush.mentor.controller.user.UserRequest;
import com.ua.javarush.mentor.dto.UserDTO;
import com.ua.javarush.mentor.exceptions.Error;
import com.ua.javarush.mentor.exceptions.GeneralException;
import com.ua.javarush.mentor.mapper.UserMapper;
import com.ua.javarush.mentor.persist.model.Role;
import com.ua.javarush.mentor.persist.model.User;
import com.ua.javarush.mentor.persist.repository.RoleRepository;
import com.ua.javarush.mentor.persist.repository.UserRepository;
import com.ua.javarush.mentor.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.ua.javarush.mentor.exceptions.GeneralExceptionUtils.createGeneralException;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    public static final String LOG_CHANGE_PERMISSION_USER_TO = "Change permission user {} {} to {}";
    public static final String LOG_RESPONSE_USER = "Response user: {} {}";
    public static final String LOG_USER_WAS_CREATED = "User '{} {}' was created";
    public static final String LOG_REMOVE_USER_ID_NAME = "Remove user: id={}, name={} {}";
    public static final String NOT_FOUND_USER_ERROR = "Didn't found user";
    public static final String NOT_FOUND_ROLE_ERROR = "Didn't found role";
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    @Value("${default.pageSize}")
    private Integer pageSize;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.roleRepository = roleRepository;
    }

    @Transactional(rollbackFor = GeneralException.class)
    @Override
    public UserDTO createUser(UserRequest userRequest) {
        User newUser = userMapper.mapToEntity(userRequest);
        userRepository.save(newUser);
        log.info(LOG_USER_WAS_CREATED, newUser.getFirstName(), newUser.getLastName());
        return userMapper.mapToDto(newUser);
    }

    @Override
    public List<UserDTO> getAllUsers(Integer page) {
        if(page == 0) {
            return userRepository.findAll()
                    .stream()
                    .map(userMapper::mapToDto)
                    .collect(Collectors.toList());
        }
        return userRepository.findAll(Pageable.ofSize(pageSize).withPage(page))
                .stream()
                .map(userMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(Long id) throws GeneralException {
        User user = fetchUser(id);
        log.info(LOG_RESPONSE_USER, user.getFirstName(), user.getLastName());
        return userMapper.mapToDto(user);
    }

    @Transactional(rollbackFor = GeneralException.class)
    @Override
    public UserDTO removeUser(Long id) throws GeneralException {
        User user = fetchUser(id);
        log.info(LOG_REMOVE_USER_ID_NAME, id, user.getFirstName(), user.getLastName());
        userRepository.deleteById(id);
        return userMapper.mapToDto(user);
    }

    @Transactional(rollbackFor = GeneralException.class)
    @Override
    public UserDTO changePermission(Long id, Long roleId) throws GeneralException {
        User user = fetchUser(id);
        Role role = fetchRoleId(roleId);
        user.setRoleId(role);
        userRepository.save(user);
        log.info(LOG_CHANGE_PERMISSION_USER_TO, user.getFirstName(), user.getLastName(), role.getName());
        return userMapper.mapToDto(user);
    }

    @NotNull
    private User fetchUser(Long id) throws GeneralException {
        return userRepository.findById(id)
                .orElseThrow(() -> createGeneralException(NOT_FOUND_USER_ERROR, HttpStatus.NOT_FOUND, Error.USER_NOT_FOUND));
    }

    @NotNull
    private Role fetchRoleId(Long roleId) throws GeneralException {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> createGeneralException(NOT_FOUND_ROLE_ERROR, HttpStatus.NOT_FOUND, Error.ROLE_NOT_FOUND));
    }
}
