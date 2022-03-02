package org.zahran.myshop.admin.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {

    @Test
    public void testEncodePassword(){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String rawPass = "789456";
//        "$2a$10$Xz9MVRKAsLvCjQf80gysburjVFrtCm7i5ciULQJKf69pbw8kMB3EK"
//        "$2a$10$AW5sJrnEktcAeH5ImlzQw.xIZKcuykXfh7Kz2/bVxG89O7MJJCHX."
//        "$2a$10$AW5sJrnEktcAeH5ImlzQw.xIZKcuykXfh7Kz2/bVxG89O7MJJCHX."
        String encodedPassword = encoder.encode(rawPass);

        System.out.println(encodedPassword);

        boolean matches = encoder.matches(rawPass,encodedPassword);

        Assertions.assertThat(matches).isTrue();
    }
}
