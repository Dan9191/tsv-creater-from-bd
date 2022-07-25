package ru.dan.tsvcreater.repository;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.dan.tsvcreater.mapper.ColumnMapper;
import ru.dan.tsvcreater.mapper.ValueMapper;
import ru.dan.tsvcreater.model.ColumnModel;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Репозиторий для работы с БД.
 */
@Component
@Setter
@Getter
@Slf4j
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
     * Признак форматирования дат.
     */
    private boolean dateFormatterEnable;

    /**
     * Формат выводимой даты.
     */
    private String dateFormatter;

    /**
     * Типы колонок даты-времени.
     */
    private static final List<String> DATE_COLUMNS = Arrays.asList(
            "timestamp without time zone",
            "timestamp",
            "date",
            "time",
            "time with time zone",
            "interval"
    );

    /**
     * Запрос на получение названий столбцов.
     */
    private static final String TABLE_GET_COLUMNS_QUERY = "SELECT column_name, data_type FROM information_schema.columns"
            + " WHERE table_name=? and table_schema=?";

    /**
     * Запрос на получение данных из таблицы.
     */
    private static final String TABLE_GET_VALUES_QUERY = "SELECT * FROM %s.%s";

    @Autowired
    public TableReaderRepositoryImpl(JdbcTemplate jdbcTemplate,
                                     @Value("${current-schema}") String currentSchema,
                                     @Value("${date-formatter-enable}") boolean dateFormatterEnable,
                                     @Value("${date-formatter}") String dateFormatter
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.currentSchema = currentSchema;
        this.dateFormatterEnable = dateFormatterEnable;
        this.dateFormatter = dateFormatter;
    }

    /**
     * Получение списка колонок таблицы.
     *
     * @param tableName Имя таблицы.
     * @return Список колонок.
     */
    @Override
    public List<ColumnModel> getColumns(String tableName) {
        return jdbcTemplate.query(TABLE_GET_COLUMNS_QUERY, new ColumnMapper(), tableName, currentSchema);
    }

    /**
     * Получение данных таблицы.
     *
     * @param columnModel Список колонок.
     * @param tableName   Имя таблицы.
     * @return Список мап данных.
     */
    @Override
    public List<Map<String, String>> getValues(List<ColumnModel> columnModel, String tableName) {
        String query = String.format(generateGetValuesScript(columnModel), currentSchema, tableName);
        List<String> columns = columnModel.stream().map(ColumnModel::getName).collect(Collectors.toList());
        return jdbcTemplate.query(query, new ValueMapper(columns));
    }

    /**
     * Генерация SQL запроса с настройкой форматирования даты.
     *
     * @param columnModels Список колонок.
     * @return SQL запрос.
     */
    private String generateGetValuesScript(List<ColumnModel> columnModels) {
        log.info("Date formatting is enable: '{}', date formatter '{}'", dateFormatterEnable, dateFormatter);
        if (dateFormatterEnable) {
            String columns = columnModels.stream().map(row -> {
                if (DATE_COLUMNS.contains(row.getType())) {
                    return String.format("to_char(%s, '%s') as %s", row.getName(), dateFormatter, row.getName());
                } else {
                    return row.getName();
                }
            }).collect(Collectors.joining(", "));
            return "SELECT ".concat(columns).concat(" FROM %s.%s;");
        } else {
            return TABLE_GET_VALUES_QUERY;
        }
    }
}
