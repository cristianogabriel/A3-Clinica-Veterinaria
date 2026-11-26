package com.clinica.ibmr.service;

import com.clinica.ibmr.api.ApiExceptions.InvalidRequestException;

public final class CpfValidator {
    private CpfValidator() {}

    public static String normalizeAndValidate(String value) {
        if (value == null) throw new InvalidRequestException("CPF é obrigatório.");
        if (!value.matches("[0-9.\\-/ ]+")) throw new InvalidRequestException("CPF inválido.");
        String cpf = value.replaceAll("\\D", "");
        if (cpf.length() != 11 || cpf.chars().distinct().count() == 1) {
            throw new InvalidRequestException("CPF inválido.");
        }
        int first = digit(cpf, 9);
        int second = digit(cpf, 10);
        if (cpf.charAt(9) - '0' != first || cpf.charAt(10) - '0' != second) {
            throw new InvalidRequestException("CPF inválido.");
        }
        return cpf;
    }

    private static int digit(String cpf, int length) {
        int sum = 0;
        for (int i = 0; i < length; i++) sum += (cpf.charAt(i) - '0') * (length + 1 - i);
        int result = (sum * 10) % 11;
        return result == 10 ? 0 : result;
    }
}
