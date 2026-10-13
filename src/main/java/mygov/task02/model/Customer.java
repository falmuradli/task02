package mygov.task02.model;

public record Customer(
        Long id,
        String name,
        String email,
        String phone
) {}
