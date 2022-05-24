package com.wushiyii.server.api.facade;

import com.wushiyii.server.api.dto.UserDTO;


public interface IUserFacade {

    UserDTO getUserById(Long id);

}
