package com.wushiyii.client.controller;

import com.wushiyii.core.annotation.Consumer;
import com.wushiyii.server.api.dto.UserDTO;
import com.wushiyii.server.api.facade.IUserFacade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Consumer
    private IUserFacade userFacade;

    @GetMapping("getUserById")
    public UserDTO getUserById(Long userId) {
        UserDTO userDTO = userFacade.getUserById(userId);
        return Objects.nonNull(userDTO) ? userDTO : new UserDTO("aaa", 123, userId);
    }

}
