package com.wushiyii.server.facade.impl;

import com.wushiyii.core.annotation.Provider;
import com.wushiyii.server.api.dto.UserDTO;
import com.wushiyii.server.api.facade.IUserFacade;


@Provider
public class UserFacadeImpl implements IUserFacade {

    @Override
    public UserDTO getUserById(Long id) {

        UserDTO dto = new UserDTO();
        dto.setUsername("小明");
        dto.setAge(20);
        dto.setId(id);
        return dto;
    }

}
