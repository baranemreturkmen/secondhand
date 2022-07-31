package com.javaet.secondhand.user.model

import java.time.LocalDateTime

data class BaseEntity(val createdDate: LocalDateTime? = null,
                      val updatedDate: LocalDateTime? = null){

}

/*createdDate -> user ilk yaratıldığında olan
* updateDate -> user ile yapılan her güncelleme de datenow ile güncelleme yaptığım kısım.
* Zorunluluk. Bazı şeyleri yasal olarak saklaman gerekiyor. Banka işlemleri vs. updatedDate göstermen
* lazım. Bankalarda aktif kolon örneği yada. Kullanıcı çıkarsa deaktif yap active kolonunu. Yada çok veri
* var. 1 aylık verinin olduğu tablodan verileri çekip sadece 1 günlük verilerin olduğu bir tablo oluştarabilirsin
* daha hızlı işlem yapabilmek adına. Alanlar requirementlara göre değişkenlik gösterir.
* */

//? -> optional demek. Default value'su null demek.
