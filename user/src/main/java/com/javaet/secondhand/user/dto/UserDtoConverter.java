package com.javaet.secondhand.user.dto;

import com.javaet.secondhand.user.model.User;
import com.javaet.secondhand.user.model.UserInformation;
import org.springframework.stereotype.Component;

@Component
public class UserDtoConverter {

    public UserDto convert(UserInformation from){
        return new UserDto(from.getMail(),from.getFirstName(),from.getLastName(),from.getMiddleName());
    }

    /*ApplicationContext'e yolluyabiliyorum Component anatasyonu ile.*/
}
