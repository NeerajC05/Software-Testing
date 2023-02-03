package com.pdiot.harty;

import static org.junit.Assert.*;

import com.pdiot.harty.utils.Validator;

import org.junit.Assert;
import org.junit.Test;

public class ValidatorTest {

    @Test
    public void testValidPassword() {
        String[] inputValues = {"", "abcdef", "abcdefg", "abcde", "abcdefghijkl"};
        boolean[] expectedResults = {false, true, true, false, true};

        for (int i = 0; i < inputValues.length; i++) {
            Assert.assertEquals(expectedResults[i], Validator.validatePassword(inputValues[i]));
        }
    }

    @Test
    public void testValidEmail() {
        String[] inputValues = {"", "test", "abc-d@mail.com", "abc_def@mail.com", "abc#def@mail.com", "abc.def@mail.cc", "abc.def@mail.org", "abc.def@mail#archive.com", "abc.def@mail..com", "abc.def@mail"};
        boolean[] expectedResults = {false, false, true, true, false, true, true, false, false, false};

        for (int i = 0; i < inputValues.length; i++) {
            System.out.println(inputValues[i]);
            assertEquals(expectedResults[i], Validator.validateEmail(inputValues[i]));
        }
    }

    @Test
    public void testValidMAC() {
        String[] inputValues = {"01-23-45-67-89-AB", "01:23:45:67:89:AB", "0123.4567.89AB", "01-23-45-67-89-AH", "01-23-45-67-AH"};
        boolean[] expectedResults = {true, true, true, false, false};

        for (int i = 0; i < inputValues.length; i++) {
            assertEquals(expectedResults[i], Validator.validateMAC(inputValues[i]));
        }
    }

}