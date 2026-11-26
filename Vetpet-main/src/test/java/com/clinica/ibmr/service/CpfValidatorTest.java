package com.clinica.ibmr.service;

import static org.junit.jupiter.api.Assertions.*;

import com.clinica.ibmr.api.ApiExceptions.InvalidRequestException;
import org.junit.jupiter.api.Test;

class CpfValidatorTest {
    @Test
    void acceptsValidCpfAndNormalizesFormatting() {
        assertEquals("11144477735", CpfValidator.normalizeAndValidate("111.444.777-35"));
    }

    @Test
    void rejectsRepeatedDigitsAndInvalidCheckDigits() {
        assertThrows(InvalidRequestException.class, () -> CpfValidator.normalizeAndValidate("11111111111"));
        assertThrows(InvalidRequestException.class, () -> CpfValidator.normalizeAndValidate("11144477734"));
    }

    @Test
    void rejectsNonCpfCharacters() {
        assertThrows(InvalidRequestException.class, () -> CpfValidator.normalizeAndValidate("abc11144477735"));
    }
}
