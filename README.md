### Утилита для генерирования tsv файла

1. Выполнить сборку: gradle build
2. Запустить приложение командой: java -jar ./tsv-downloader-1.0.1-SNAPSHOT.jar
3. Возможные настройки:

| Имя параметра | Значение по умолчанию | Описание |
| --- | --- | --- |
| table-name | test_table | Имя таблицы |
| temp-directory | C:/tmp/tsv | Директория, в которую сохраняется tsv файл |
| delete-columns-enable | true | Требуется ли вывести данные без определенных столбцов |
| delete-columns | date_from,date_to | Удаляемые столбцы |
| convert-columns-enable | true | Требуется ли изменить название столбцов |
| convert-columns | {id:'subsystem_id'} | Старое и новое название изменяемого столбца |
| current-schema | test_schema | Схема, в которой содержиться требуемая таблица |

примеры запросов

1. Запрос без конвертирования названия столбцов

   java -jar ./tsv-downloader-1.0.1-SNAPSHOT.jar --convert-columns-enable=false

2. Запрос с удалением столбца name

   java -jar ./tsv-downloader-1.0.1-SNAPSHOT.jar --delete-columns=name
   
3. Запрос на выгрузку данных из таблицы test_table2

   java -jar ./tsv-downloader-1.0.1-SNAPSHOT.jar --table_name=test_table2

4. Запрос с выгрузкой данных и изменением названия столбца 'name' на 'HELLO_WORLD'

   java -jar ./tsv-downloader-1.0.1-SNAPSHOT.jar --convert-columns={name:'HELLO_WORLD'}
