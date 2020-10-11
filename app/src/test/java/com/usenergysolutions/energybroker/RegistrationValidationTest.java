package com.usenergysolutions.energybroker;


import com.usenergysolutions.energybroker.utils.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RegistrationValidationTest {

    @Test
    public void isEmailValid() {
        boolean validation = StringUtils.isValidEmail("eyal@123.123");
        boolean expected = true;
        assertEquals("isEmailValid failed", expected, validation);
    }

    @Test
    public void isPasswordStrength() {
        int strength = StringUtils.passwordStrengthCaculation("Master007014!");
        int expected = 135;
        assertEquals("isEmailValid failed", expected, strength, 5);
    }
}
