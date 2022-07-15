package ru.dan.tsvcreater.repository;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.dan.tsvcreater.mapper.ValueMapper;

import java.util.List;
import java.util.Map;

/**
 * Репозиторий для работы с БД.
 */
@Component
@Setter
@Getter
public class TableReaderRepositoryImpl implements TableReaderRepository {

    /**
     * Объект для выполнения запросов над БД.
     */
    private JdbcTemplate jdbcTemplate;

    /**
     * Схема, из которой требуется выгрузить таблицу.
     */
    private String currentSchema;

    /**
     * Запрос на получение названий столбцов.
     */
    private static final String TABLE_GET_COLUMNS_QUERY = "SELECT column_name FROM information_schema.columns"
            + " WHERE table_name=? and table_schema=?";

    /**
     * Запрос на получение данных из таблицы.
     */
    private static final String TABLE_GET_VALUES_QUERY = "SELECT * FROM %s.%s";

    @Autowired
    public TableReaderRepositoryImpl(JdbcTemplate jdbcTemplate,
                                     @Value("${current-schema}") String currentSchema) {
        this.jdbcTemplate = jdbcTemplate;
        this.currentSchema = currentSchema;
    }

    /**
     * Получение названия колонок таблицы.
     * @param tableName Имя таблицы.
     *
     * @return Список колонок.
     */
    @Override
    public List<String> getColumns(String tableName) {
        return jdbcTemplate.query(TABLE_GET_COLUMNS_QUERY, ((rs, rowNum) -> rs.getString("column_name")), tableName, currentSchema);
    }

    /**
     * Получение данных таблицы.
     * @param columns   Список колонок.
     * @param tableName Имя таблицы.
     *
     * @return Список мап данных.
     */
    @Override
    public List<Map<String, String>> getValues(List<String> columns, String tableName) {
        String query = String.format(TABLE_GET_VALUES_QUERY, currentSchema, tableName);
        return jdbcTemplate.query(query, new ValueMapper(columns));
    }
}
