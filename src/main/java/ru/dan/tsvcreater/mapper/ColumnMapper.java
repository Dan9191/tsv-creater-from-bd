package ru.dan.tsvcreater.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.dan.tsvcreater.model.ColumnModel;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Маппер для работы с названием и типом колонок таблицы.
 */
public class ColumnMapper implements RowMapper<ColumnModel> {

    @Override
    public ColumnModel mapRow(ResultSet rs, int rowNum) throws SQLException {
        ColumnModel columnModel = new ColumnModel();
        columnModel.setName(rs.getString("column_name"));
        columnModel.setType(rs.getString("data_type"));
        return columnModel;
    }
}
