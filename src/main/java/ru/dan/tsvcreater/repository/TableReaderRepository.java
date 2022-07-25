package ru.dan.tsvcreater.repository;

import ru.dan.tsvcreater.model.ColumnModel;

import java.util.List;
import java.util.Map;

/**
 * Интерфейс для работы с БД.
 */
public interface TableReaderRepository {

    List<ColumnModel> getColumns(String tableName);

    List<Map<String, String>> getValues(List<ColumnModel> columns, String tableName);
}
