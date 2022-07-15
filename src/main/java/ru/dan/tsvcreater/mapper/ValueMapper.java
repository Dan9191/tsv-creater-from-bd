package ru.dan.tsvcreater.mapper;

import org.springframework.jdbc.core.RowMapper;

import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Маппер для работы с БД. Данные сохраняются в мапу, где ключ - колонка таблицы, значение - соответствующие данные.
 */
public class ValueMapper implements RowMapper<Map<String, String>> {

    /**
     * Список колонок, по которым требуется смапить таблицу.
     */
    private final List<String> columns;

    public ValueMapper(List<String> columns) {
        this.columns = columns;
    }

    @Override
    public Map<String, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
        Map<String, String> rows = new HashMap<>();
        for (String column : columns) {
            String value = rs.getString(column);
            if (StringUtils.isEmpty(value)) {
                rows.put(column, "");
            } else {
                rows.put(column, value);
            }
        }
        return rows;
    }
}
