package ru.ern.tsvdownloader.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import ru.dan.tsvcreater.model.ColumnModel;
import ru.ern.tsvdownloader.repository.TableReaderRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Создатель tsv файла.
 */
@Component
@Getter
@Setter
@Slf4j
public class TsvCreator {

    /**
     * Репозиторий для работы с БД.
     */
    private TableReaderRepository tableReaderRepository;

    /**
     * Название таблицы.
     */
    private String tableName;

    /**
     * Папка для удаления.
     */
    private File tempDir;

    /**
     * Требуется ли архивировать файл по достижению указанного размера.
     */
    private boolean zipArchiveEnable;

    /**
     * Размер для архивирования.
     */
    private DataSize zipSize;

    /**
     * Признак удаления столбцов.
     */
    private boolean deleteColumnsEnable;

    /**
     * Список столбцов, которые требуется удалить.
     */
    private List<String> deletedColumns;

    /**
     * Признак конвертации названия столбцов.
     */
    private boolean convertColumnsEnable;

    /**
     * Список столбцов, которые требуется удалить.
     */
    private Map<String, String> covertColumns;

    @Autowired
    public TsvCreator(TableReaderRepository tableReaderRepository,
                      @Value("${table-name}") String tableName,
                      @Value("${temp-directory}") File tempDir,
                      @Value("${zip-archive-enable}") boolean zipArchiveEnable,
                      @Value("${zip-size}") DataSize zipSize,
                      @Value("${delete-columns-enable}") boolean deleteColumnsEnable,
                      @Value("${delete-columns}") List<String> deletedColumns,
                      @Value("${convert-columns-enable}") boolean convertColumnsEnable,
                      @Value("#{${convert-columns}}") Map<String, String> covertColumns) {
        this.tableReaderRepository = tableReaderRepository;
        this.tableName = tableName;
        this.tempDir = tempDir;
        this.zipArchiveEnable = zipArchiveEnable;
        this.zipSize = zipSize;
        this.deleteColumnsEnable = deleteColumnsEnable;
        this.deletedColumns = deletedColumns;
        this.convertColumnsEnable = convertColumnsEnable;
        this.covertColumns = covertColumns;
    }

    /**
     * Формирует tsv файл.
     */
    public void createTsvFile() {
        String fileName = String.format("%s.tsv", tableName);
        File tsvFile = new File(tempDir, fileName);
        try (FileOutputStream fileOutputStream = new FileOutputStream(tsvFile);
             Writer fos = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8)) {
            List<String> data = getData();
            data.forEach(row -> {
                try {
                    fos.append(row);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            fos.flush();
        } catch (Exception e) {
            FileUtils.deleteQuietly(tsvFile);
            log.error(e.getMessage());
        }

        if (zipArchiveEnable && FileUtils.sizeOf(tsvFile) >= zipSize.toBytes()) {
            File zip = new File(tempDir, String.format("%s.zip", tableName));
            ZipParameters zipParameters = new ZipParameters();
            ZipFile zipArchiver = new ZipFile(zip);
            zipParameters.setFileNameInZip(fileName);
            try {
                zipArchiver.addFile(tsvFile);
            } catch (ZipException e) {
                log.error(e.getMessage());
            } finally {
                FileUtils.deleteQuietly(tsvFile);
            }
        }
    }

    /**
     * Обращение к БД для получения данных таблицы. Первый элемент списка - колонки таблицы, далее - её данные.
     *
     * @return Список из данных таблицы.
     */
    public List<String> getData() {
        List<String> result = new ArrayList<>();
        List<ColumnModel> columns;
        log.info("Deleted columns is enable: '{}', delete columns '{}'", deleteColumnsEnable, deletedColumns);
        if (deleteColumnsEnable) {
            columns = tableReaderRepository.getColumns(tableName).stream()
                    .filter(column -> !deletedColumns.contains(column.getName()))
                    .collect(Collectors.toList());
        } else {
            columns = tableReaderRepository.getColumns(tableName);
        }
        List<String> values = tableReaderRepository.getValues(columns, tableName).stream()
                .map(map -> {
                    List<String> rowValues = new ArrayList<>();
                    for (ColumnModel column : columns) {
                        rowValues.add(map.get(column.getName()));
                    }
                    return rowValues;
                })
                .map(list -> String.join("\t", list).concat("\n"))
                .collect(Collectors.toList());

        log.info("Convert columns is enable: '{}', convert columns '{}'", convertColumnsEnable, covertColumns);
        if (convertColumnsEnable) {
            Set<String> convertedColumnsSet = covertColumns.keySet();
            result.add(columns.stream()
                    .map(ColumnModel::getName)
                    .map(column -> {
                        if (convertedColumnsSet.contains(column)) {
                            return covertColumns.get(column);
                        }
                        return column;
                    })
                    .collect(Collectors.joining("\t")).concat("\n"));
        } else {
            result.add(columns.stream().map(ColumnModel::getName).collect(Collectors.joining("\t")).concat("\n"));
        }
        result.addAll(values);
        return result;
    }
}
