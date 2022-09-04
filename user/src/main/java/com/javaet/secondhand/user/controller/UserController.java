package com.javaet.secondhand.user.controller;

import com.javaet.secondhand.user.dto.CreateUserRequest;
import com.javaet.secondhand.user.dto.UpdateUserRequest;
import com.javaet.secondhand.user.dto.UserDto;
import com.javaet.secondhand.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/user")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUser());
    }

    @GetMapping("/{mail}")
    public ResponseEntity<UserDto> getUserByMail(@PathVariable("mail") String mail){
        return ResponseEntity.ok(userService.getUserByMail(mail));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody CreateUserRequest createUserRequest){
        return ResponseEntity.ok(userService.createUser(createUserRequest));
    }

    @PutMapping("/{mail}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("mail") String mail,@RequestBody UpdateUserRequest updateUserRequest){
        return ResponseEntity.ok(userService.updateUser(mail,updateUserRequest));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> deactivateUser(@PathVariable("id") Long id){
        userService.deactivateUser(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> activeUser(@PathVariable("id") Long id){
        userService.activeUser(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id){
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }


    /*Bean anatasyonu nesneyi application context'e atıp singleton bir şekilde yaratıp
    * her yerde aynı instance üzerinden kullanmanı sağlıyor.
    * */

    /*
    * Asenkron programlama için spring'in kendi içerisinde webflux var.
    * Rabbit mq vs.'de kullanılabiliyor.
    * */

    /*
    * CQRS, ana odağı write (yazma) ve read (okuma) sorumluluklarının ayrıştırılmasına dayanan
    * bir mimari tasarım modelidir.
     * */

    /*
    * Post -> 201
    * Put -> 202
    * Patch -> User'a ait herhangi bir parametre vermiyorum. Sadece id veriyorum. Bu endpoint User'in
    * id'sini alarak o User ile ilgili bir değişiklik yapıyor. User'ı deactive et dersin. User'ı deactive
    * etmek için bir request body'in olmaz. Sen sadece id verip sonucunda bir alanınla ile ilgili bir güncelleme
    * yaparsın senin request'inden bağımsız. Bu durumlarda patch kullanılıyor. Patch ile body göndermiyorsun
    * sadece id gönderiyorsun.
    * */

    //mapstruct dto conventer vs. işleri için interface tanımlayıp kolay bir şekilde converter yapmanı sağlıyor.

    /*Put Post yaparken vs. frontend id bilgisine nereden ulaşacak? Dto üzerinden de ulaşabilirim,
    * veya authentication yaparken de ulaştırabilirim. Requestler authenticated olmalı dolayısıyla
    * backende token gelmeli ki sen bu request'i authenticate edebilesin. Token ile geldiği zaman
    * token'ın kime ait olduğunu doğrularken buluyorsun oradan da userId bir şekilde inject edilebilir.
    * */
}
