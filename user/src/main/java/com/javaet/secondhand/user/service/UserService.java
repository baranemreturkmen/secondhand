package com.javaet.secondhand.user.service;

import com.javaet.secondhand.user.dto.CreateUserRequest;
import com.javaet.secondhand.user.dto.UpdateUserRequest;
import com.javaet.secondhand.user.dto.UserDto;
import com.javaet.secondhand.user.dto.UserDtoConverter;
import com.javaet.secondhand.user.exception.UserIsNotActiveException;
import com.javaet.secondhand.user.exception.UserNotFoundException;
import com.javaet.secondhand.user.model.UserInformation;
import com.javaet.secondhand.user.repository.UserInformationRepository;
import com.javaet.secondhand.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    /*Autowired ile injection yapmak yerine constructor injection yapıyoruz. private final yaparak nesneleri immutable
    * yapıyoruz. Spring'de autowired yerine bunu öneriyor. Nesneyi immutable yapınca sınıfın içerisinde değiştiremiyoruz.
    * En büyük artısıda nesnenin kolay test edilebilirliği oluyor.*/
    private final UserDtoConverter userDtoConverter;
    private final UserInformationRepository userInformationRepository;

    public UserService(UserInformationRepository userInformationRepository,UserDtoConverter userDtoConverter) {
        this.userDtoConverter = userDtoConverter;
        this.userInformationRepository = userInformationRepository;
    }

    public List<UserDto> getAllUser() {
        /*return userInformationRepository.findAll()
                .stream()
                .map(userDtoConverter::convert)
                .collect(Collectors.toList());*/
        return userDtoConverter.convert(userInformationRepository.findAll());
    }

    /*
    * throw -> hatayı fırlatır.
    * throws -> hata fırlatmaz ama geliştiriciyi uyarır. Bu metodu çağıran diğer yerlere şunu declare
    * edersin getUserById çağırıyorsun ama buradan böyle bir hata gelebilir ona göre kendi try catch metodunu
    * yaz. Method signature'ına bunu yazarak uyarıyorsun.
    * */

    public UserDto getUserByMail(String mail){
        UserInformation userInformation = findUserByMail(mail);
        return userDtoConverter.convert(userInformation);
    }
    /*
    * Kotlinde ki immutable muhabbetinden dolayı setle null oluşan objeyi initalize edemiyorum.
    * Constructor geçmek zorundayım. Bu seferde tüm elemanları constructor'da istiyor. Id'yi mecburen
    * null vermek zorunda kaldık. Normalde Entity nesnelerde bu tarz durumlarda hibernate'den faydalanıyorduk.
    * Ama kotlinde id için böyle bir durum söz konusu değil. Id özelinde de hata çıkmamasının sebebi
    * Id'nin @field:GeneratedValue(strategy = GenerationType.IDENTITY) anatasyonu ile mysql tarafında
    * oluşturulması.
    * */
    public UserDto createUser(CreateUserRequest createUserRequest) {
        UserInformation userInformation = new UserInformation(createUserRequest.getMail(),createUserRequest.getFirstName()
                ,createUserRequest.getLastName(),createUserRequest.getMiddleName(),true);

        return userDtoConverter.convert(userInformationRepository.save(userInformation));
    }

    public UserDto updateUser(String mail,UpdateUserRequest updateUserRequest) {
        UserInformation userInformation = findUserByMail(mail);
        if(!userInformation.getActive()){
            logger.warn(String.format("The user wanted update is not active!, user mail: %s",mail));
            throw new UserIsNotActiveException();
        }
        //Model nesnesi
        UserInformation updatedUserInformation = new UserInformation(userInformation.getId(),userInformation.getMail(),updateUserRequest.getFirstName(), updateUserRequest.getLastName(),
                updateUserRequest.getMiddleName());

        return userDtoConverter.convert(userInformationRepository.save(updatedUserInformation));
    }

    public void deactivateUser(Long id) {
        changeActivateUser(id,false);
    }

    public void activeUser(Long id) {
        changeActivateUser(id,true);
    }

    public void deleteUser(Long id) {
        if(doesUserExist(id)){
            userInformationRepository.deleteById(id);
        }
        else{
            throw new UserNotFoundException("User couldn't be found by following id: "+id);
        }
    }

    //state'e bakıyorsa is, fiil eylem varsa does.
    private boolean doesUserExist(Long id){
        return userInformationRepository.existsById(id);
    }

    private UserInformation findUserByMail(String mail){
        return userInformationRepository.findByMail(mail).orElseThrow(()->new UserNotFoundException("User couldn't be found by following mail: "+mail));
    }

    private UserInformation findUserById(Long id){
        return userInformationRepository.findById(id).orElseThrow(()->new UserNotFoundException("User couldn't be found by following id: "+id));
    }

    private void changeActivateUser(Long id,Boolean isActive){
        UserInformation userInformation = findUserById(id);

        UserInformation updatedUserInformation = new UserInformation(userInformation.getId(),
                userInformation.getMail(),
                userInformation.getFirstName(),
                userInformation.getLastName(),
                userInformation.getMiddleName(),
                isActive);

        userInformationRepository.save(updatedUserInformation);
    }
}
