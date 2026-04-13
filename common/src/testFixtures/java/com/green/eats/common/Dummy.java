package com.green.eats.common;


import net.datafaker.Faker;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Rollback;

import java.util.Locale;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public abstract class Dummy {
    protected Faker koFaker = new Faker(Locale.KOREAN);
    protected Faker enFaker = new Faker(Locale.ENGLISH);
}
