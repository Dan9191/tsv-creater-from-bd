package ru.dan.tsvcreater.model;

import lombok.Data;

/**
 * Модель колонки.
 */
@Data
public class ColumnModel {

    /**
     * Название колонки.
     */
    private String name;

    /**
     * Тип колонки.
     */
    private String type;
}

