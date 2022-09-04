package com.javaet.secondhand;

import com.javaet.secondhand.user.dto.UserDto;
import com.javaet.secondhand.user.model.UserInformation;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestSupport {

    public static List<UserInformation> generateUsers() {
        return IntStream.range(0, 5).mapToObj(i ->
           new UserInformation(
                    (long) i,
                    i + "@javaet.net",
                    "firstName" + i,
                    "lastName" + i,
                    "",
                    new Random(2).nextBoolean()
            )
        ).collect(Collectors.toList());
    }

    public static List<UserDto> generateUserDtoList(List<UserInformation> userList) {
        return userList.stream().map(from ->new UserDto(from.getMail(),from.getFirstName(),from.getLastName(),from.getMiddleName()))
                .collect(Collectors.toList());
    }

}

