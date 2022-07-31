package com.javaet.secondhand.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(String message){
        super(message);
    }

    /*Bir exception fırlatılsın. GeneralExceptionHandler'da bu exception'ı yakalayıp ilgili
    * ilgili response'u üretebilsin.
    * Exception -> Thread'im patladığı yerde ölecekti.
    * RunTimeException -> Thread'im ölmüyor ama ona göre işlemler yapabiliyorum. JVM'e exception
    * fırlatmış olacağım RunTimeException sayesinde ve GeneralExceptionHandler bunu yakalayıp bir
    * response entity'e çevirecek. Biz de sadece ilgili mesajı dönecez. Exception olsaydı bu thread
    * ölecekti.
    * */
}
