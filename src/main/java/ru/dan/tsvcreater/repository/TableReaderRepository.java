package ru.dan.tsvcreater.repository;

import java.util.List;
import java.util.Map;

/**
 * Интерфейс для работы с БД.
 */
public interface TableReaderRepository {
    List<String> getColumns(String tableName);
    List<Map<String, String>> getValues(List<String> columns, String tableName);
}
