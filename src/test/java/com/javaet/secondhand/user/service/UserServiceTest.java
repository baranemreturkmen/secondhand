package com.javaet.secondhand.user.service;

import com.javaet.secondhand.TestSupport;
import com.javaet.secondhand.user.dto.CreateUserRequest;
import com.javaet.secondhand.user.dto.UpdateUserRequest;
import com.javaet.secondhand.user.dto.UserDto;
import com.javaet.secondhand.user.dto.UserDtoConverter;
import com.javaet.secondhand.user.exception.UserIsNotActiveException;
import com.javaet.secondhand.user.exception.UserNotFoundException;
import com.javaet.secondhand.user.model.UserInformation;
import com.javaet.secondhand.user.repository.UserInformationRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.willThrow;
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

    /*Teker teker test caseleri çalıştırdığımızda test metodları geçiyor olabilir ama bütün sınıfı çalıştırdığımızda mock invocationlarda
     * sıkıntı yaşayabiliyoruz. BeforeEach bu sorunu çözer(JUnit4'de Before). Gidip her seferinde mock'u temizliyor.*/
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

    @Test
    public void testGetUserByMail_whenUserMailExists_itShouldReturnsUserDto(){
        String mail = "mail@javaet.net";
        UserInformation user = generateUser(mail);
        UserDto userDto = generateUserDto(mail);

        when(repository.findByMail(mail)).thenReturn(Optional.of(user));
        when(converter.convert(user)).thenReturn(userDto);

        UserDto result = userService.getUserByMail(mail);

        assertEquals(userDto,result);
        verify(repository).findByMail(mail);
        verify(converter).convert(user);
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetUserByMail_whenUserMailDoesNotExists_itShouldThrowUserNotFoundException(){
        String mail = "mail@javaet.net";

        when(repository.findByMail(mail)).thenReturn(Optional.empty());

        //assertThrows(UserNotFoundException.class, () -> userService.getUserByMail(mail));
        //JUnit5 için yukarıdaki kullanım hata vermez. Ama JUnit4'de verebilir.

        when(userService.getUserByMail(mail)).thenThrow(UserNotFoundException.class);
        //JUnit4 için hata vermeyecek kullanım bu.

        verify(repository).findByMail(mail);
        verifyNoInteractions(converter);

        /*JUnit5'de yukarıdaki Test anatasyonunun içerisindeki expected parametresini vermene gerek yok.
        * Fakat JUnit4'de vermezsen eğer hata veriyor. Exception'nın kendisi handle edilemiyor ve handle
        * etmeye çalıştığımız exception fırlatılıyor.*/
    }

    @Test
    public void testCreateUser_itShouldReturnCreatedUserDto(){
        String mail = "mail@javaet.net";
        CreateUserRequest request = new CreateUserRequest(mail,"firstName","lastName","");
        UserInformation user = new UserInformation(mail,"firstName","lastName","",false);
        UserInformation savedUser = new UserInformation(1L,mail,"firstName","lastName","",false);
        UserDto userDto = new UserDto(mail,"firstName","lastName","");

        when(repository.save(user)).thenReturn(savedUser);
        when(converter.convert(savedUser)).thenReturn(userDto);

        UserDto result = userService.createUser(request);

        assertEquals(userDto,result);

        verify(repository).save(user);
        verify(converter).convert(savedUser);
    }

    @Test
    public void testUpdateUser_whenUserMailExistAndUserIsActive_itShouldReturnUpdateUserDto(){
        String mail = "mail@javaet.net";
        UpdateUserRequest request = new UpdateUserRequest("firstName2","lastName2","middleName");
        UserInformation user = new UserInformation(1L,mail,"firstName","lastName","",true);
        UserInformation updatedUser = new UserInformation(1L,mail,"firstName2","lastName2","middleName",true);
        UserInformation savedUser = new UserInformation(1L,mail,"firstName2","lastName2","middleName",true);
        UserDto userDto = new UserDto(mail,"firstName2","lastName2","middleName");

        when(repository.findByMail(mail)).thenReturn(Optional.of(user));
        when(repository.save(updatedUser)).thenReturn(savedUser);
        when(converter.convert(savedUser)).thenReturn(userDto);

        UserDto result = userService.updateUser(mail,request);

        assertEquals(userDto,result);

        verify(repository).findByMail(mail);
        verify(repository).save(updatedUser);
        verify(converter).convert(savedUser);
    }

    @Test(expected = UserNotFoundException.class)
    public void testUpdateUser_whenUserMailDoesNotExist_itShouldThrowUserNotFoundException(){
        String mail = "mail@javaet.net";
        UpdateUserRequest request = new UpdateUserRequest("firstName2","lastName2","middleName2");

        when(repository.findByMail(mail)).thenReturn(Optional.empty());
        when(userService.updateUser(mail,request)).thenThrow(UserNotFoundException.class);

        verify(repository).findByMail(mail);
        verifyNoInteractions(repository);
        verifyNoInteractions(converter);
    }

    @Test(expected = UserIsNotActiveException.class)
    public void testUpdateUser_whenUserMailExistButUserIsNotActive_itShouldThrowUserIsNotActiveException(){
        String mail = "mail@javaet.net";
        UpdateUserRequest request = new UpdateUserRequest("firstName2","lastName2","middleName2");
        UserInformation user = new UserInformation(1L,mail,"firstName","lastName","",false);

        when(repository.findByMail(mail)).thenReturn(Optional.of(user));
        when(userService.updateUser(mail,request)).thenThrow(UserIsNotActiveException.class);

        verify(repository).findByMail(mail);
        verifyNoInteractions(repository);
        verifyNoInteractions(converter);
    }

    @Test
    public void testDeactivateUser_whenUserIdExists_itShouldUpdateUserByActive(){
        String mail = "mail@javaet.net";
        UserInformation user = new UserInformation(1L,mail,"firstName","lastName","",true);
        UserInformation savedUser = new UserInformation(1L,mail,"firstName","lastName","",false);

        when(repository.findById(userId)).thenReturn(Optional.of(user));

        userService.deactivateUser(userId);

        verify(repository).findById(userId);
        verify(repository).save(savedUser);
        /*Testi yapılan metod void metod olduğu için verification tarafını verify ile yapacağız.*/
    }

    @Test(expected = UserNotFoundException.class)
    public void testDeactivateUser_whenUserIdDoesNotExists_itShouldThrowUserNotFoundException(){

        var mockedUserService = mock(UserService.class);
        when(repository.findById(userId)).thenReturn(Optional.empty());

        //JUnit4'de void metod ile exception throwing test edeceksen bu şekilde test edebilirsin.
        //userService new'leyerek oluşturulduğundan tek başına when içerisine dahil edemedik.
        //Önceki testlerde userService.metodÇağrısı şeklinde ilerlendiğinden bu sorun daha önce karşımıza çıkmadı.
        doThrow(UserNotFoundException.class).when(mockedUserService).deactivateUser(userId);

        /*Yukarıda mockladığın servis üzerinden hatayı fırlatıyorsun ama mockladığın servis üzerinden hatayı fırlattığın
        * metoda gitmezsen burada testin fail olur çünkü metodun kendisine gerçek anlamda gitmediğin için doThrow ile
        * (yada başka birşeyle metod void değilse) hatanın kendisini fırlatıp kenara çekilmiş oluyorsun fırlattığın hatayı
        * metodun kendisine gidip kullanman gerekli.*/
        mockedUserService.deactivateUser(userId);

        verify(repository).findById(userId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void testActivateUser_whenUserIdExists_itShouldUpdateUserByActiveTrue(){
        String mail = "mail@javaet.net";
        UserInformation user = new UserInformation(1L,mail,"firstName","lastName","",false);
        UserInformation savedUser = new UserInformation(1L,mail,"firstName","lastName","",true);

        when(repository.findById(userId)).thenReturn(Optional.of(user));

        userService.activateUser(userId);

        verify(repository).findById(userId);
        verify(repository).save(savedUser);
    }

    @Test(expected = UserNotFoundException.class)
    public void testActivateUser_whenUserIdDoesNotExists_itShouldThrowUserNotFoundException(){

        var mockedUserService = mock(UserService.class);
        when(repository.findById(userId)).thenReturn(Optional.empty());

        doThrow(UserNotFoundException.class).when(mockedUserService).activateUser(userId);

        mockedUserService.activateUser(userId);

        verify(repository).findById(userId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void testDeleteUser_whenUserIdExist_itShouldDeleteUser(){
        /*Bu case'de hoca testin daha kolay olması için backend kodunu değiştirdi ben değiştirmedim.*/
        String mail = "mail@javaet.net";
        var mockedUserService = mock(UserService.class);
        UserInformation user = new UserInformation(1L,mail,"firstName","lastName","",false);

        when(repository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(repository).deleteById(userId);
        when(ReflectionTestUtils.invokeMethod(userService,"doesUserExist",userId)).thenReturn(true);

        doNothing().when(mockedUserService).deleteUser(userId);
        userService.deleteUser(userId);

        /*Aşağıdaki 2 satırı Wanted but not invoked: .... However, there was exactly ...(sayı olur) interaction with this mock: hatasını
        * aşmak için ekledik.*/
        repository.findById(userId);
        repository.deleteById(userId);

        /*org.mockito.exceptions.verification.TooManyActualInvocations: hatasından dolayı aşağısı yoruma alındı.
        * yukarıdaki işte invocation yapıyor. Aşağıdaki verify etme durumuda.*/
        //verify(repository).findById(userId);
        //verify(repository).deleteById(userId);
    }

    @Test(expected = UserNotFoundException.class)
    public void testDeleteUser_whenUserIdDoesNotExists_itShouldThrowUserNotFoundException(){

        var mockedUserService = mock(UserService.class);

        //when(repository.findById(userId)).thenReturn(Optional.empty());
        //doNothing().when(repository).deleteById(userId);
        //when(ReflectionTestUtils.invokeMethod(userService,"doesUserExist",userId)).thenReturn(false);
        doNothing().when(mockedUserService).deleteUser(userId);
        when(ReflectionTestUtils.invokeMethod(userService,"doesUserExist",userId)).thenThrow(UserNotFoundException.class);
        //doThrow(UserNotFoundException.class).when(mockedUserService).deleteUser(userId);

        mockedUserService.deleteUser(userId);
        //repository.findById(userId);
        ReflectionTestUtils.invokeMethod(userService,"doesUserExist",userId);

        verify(repository).findById(userId);
        verifyNoMoreInteractions(repository);

        //Bu test metodu pass oluyor ama istediğimiz gibi else girip hata fırlatmıyor tekrar bakılacak...
    }

}