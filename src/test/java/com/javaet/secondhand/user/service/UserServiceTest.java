package com.javaet.secondhand.user.service;

import com.javaet.secondhand.TestSupport;
import com.javaet.secondhand.user.dto.UserDto;
import com.javaet.secondhand.user.dto.UserDtoConverter;
import com.javaet.secondhand.user.model.UserInformation;
import com.javaet.secondhand.user.repository.UserInformationRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
/*Test sınıfının lifecycle'ını oluşturuyor, anastasyonları kullanmamazı sağlıyor. BeforeAll BeforeEach vs
* statik kullanmak istemiyorsam bunu kullanmam lazım.*/
public class UserServiceTest extends TestSupport {

    /*Burada anatasyon ile mocklamak yerine private yapıp setUp içerisinde metodla mockluyorum.
    * Mock da converter nesnei yaratmayacak ve bean not found excepiton tarzı hata verecek.
    * Immutability uyguluyorum. Mock anatasyonunu kullansaydım UserInformationRepository için InjectMocks
    * anatasyonunu kullanacaktım. Bu şekilde BeforeAll anatasyonu ile setUp metodunda yaptığım işlerin aynısını
    * yapıyorum.*/

    /*Folksdev'den farklı olarak Junit5 yerine Junit4 kullanmayı tercih ettim.*/
    //@Mock
    private UserDtoConverter converter;
    //@Mock
    //@MockBean
    private UserInformationRepository repository;
    //@InjectMocks
    private UserService userService;

    /*BeforeEach ile setUp her test case'de çalışacak. Bazen mockların içerisinde ki datalar
    * sen mock'u tazelemediğin sürece kalabiliyor. BeforeAll bütün test caseler
    * başlamadan önce bir kere çalışır bütün test caseler setUp içerisinde ki değişikliği
    * kullanır. Daha çok integration testlerde before all kullanılıyor. BeforeEach JUnit5'De var
    * JUnit4'de karşılık gelen Before'u kullanıyoruz.
    * */
    @Before
    public void setUp(){
        //MockitoAnnotations.initMocks(this);
        converter = mock(UserDtoConverter.class);
        repository = mock(UserInformationRepository.class);

        userService = new UserService(repository,converter);
    }

    @Test
    public void testAllUsers_itShouldReturnsUserDtoList(){
        //Preparation
        List<UserInformation> userList = generateUsers();
        List<UserDto> userDtoList = generateUserDtoList(userList);

        //Condition
        when(repository.findAll()).thenReturn(userList);
        when(converter.convert(userList)).thenReturn(userDtoList);

        //ServiceCall
        List<UserDto> result = userService.getAllUser();

        //Equality
        assertEquals(userDtoList,result);
        verify(repository).findAll();
        verify(converter).convert(userList);
    }

}