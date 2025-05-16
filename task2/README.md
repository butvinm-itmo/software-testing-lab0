## Usage

**install dependencies:**

```bash
java --enable-preview build.java install
```

**build project:**

```bash
java --enable-preview build.java build
```

**run application:**

```bash
java --enable-preview build.java run <messages limit> <log requests> <openai api token>
```

**lint with checkstyle:**

```bash
java --enable-preview build.java lint
```

**test and generate coverage report:**

```bash
java --enable-preview build.java test
```

## Text

Общепризнанным и немаловажным фактом является то, что вещи не всегда таковы, каковыми кажутся. Например, на планете Земля было принято считать, что интеллект человека выше интеллекта дельфина, на том простом основании, что человек создал столько всяких полезных вещей – колесо, Нью-Йорк, войны и т.д. – в то время как дельфины только знай себе прохлаждались в воде. Зато дельфины, напротив, всегда считали себя гораздо умнее человека – на том же самом основании.

Интересно заметить, что дельфины заранее знали о грядущем уничтожении планеты Земля и старались принять меры, дабы предостеречь человечество; однако любые попытки контакта с их стороны ошибочно интерпретировались людьми как забавные трюки – скажем, подброшенный носом мяч в сопровождении красивого свиста – исполняемые с целью получения лакомства, так что в конце концов дельфины потеряли надежду объясниться и, незадолго до появления вогонов, эвакуировались с Земли собственными средствами.

Последнее отчаянное воззвание дельфинов показалось людям удивительно сложным двойным сальто через обруч с одновременным высвистыванием “Звездно-полосатого флага”. В действительности же, сообщение гласило: “Пока! И спасибо за рыбу”.

## План тестирования

Проведем анализ функции App.parseConfig

**Таблица решений:**

| Условие                                                                      | 0   | 1   | 2   | 3   | 4   | 5   | 6   | 7   |
| ---------------------------------------------------------------------------- | --- | --- | --- | --- | --- | --- | --- | --- |
| args содержит 3 элемента                                                     |     |     |     |     | x   | x   | x   | x   |
| Первый элемента - положительное целое число                                  |     | x   |     | x   |     |     | x   | x   |
| Второй элемента "true" или "false" (регистронезависимо)                      |     |     | x   | x   |     | x   |     | x   |
| Эффект                                                                       |     |     |     |     |     |     |     |     |
| IllegalArgumentException("Unexpected number of arguments, expect exactly 3") | x   | x   | x   | x   |     |     |     |     |
| IllegalArgumentException("chat-message-limit must be a positive integer")    |     |     |     |     | x   | x   |     |     |
| IllegalArgumentException("log-requests must be either 'true' or 'false'")    |     |     |     |     |     |     | x   |     |
| Возвращается объект конфигурации с заданными значениями                      |     |     |     |     |     |     |     | x   |

Функция выбрасывает ошибку при первом же некорректном аргументе, так что можно исключить комбинации из нескольких некорректных аргументов.
Остаются тестовые случаи 0, 4, 6 и 7.

Однако, стоит также рассмотреть различные некорректные значения аргументов, чтобы исключить ситуации, когда в коде проверяется не весь диапазон значений:

_args length:_

| Значение | Валидность |
| -------- | ---------- |
| < 3      | Невалидный |
| == 3     | Валидный   |
| > 3      | Невалидный |

_chat-message-limit:_

| Значение       | Валидность |
| -------------- | ---------- |
| Not an integer | Невалидный |
| Integer <=0    | Невалидный |
| Integer > 0    | Валидный   |

_logRequests:_

| Значение                               | Валидность |
| -------------------------------------- | ---------- |
| "true", "True", "TrUe", "TRUE", ...    | Валидный   |
| "false", "False, "FaLsE", "FALSE", ... | Валидный   |
| Not a boolean string                   | Невалидный |

_openai-api-key:_

Теоретически, это значение не влияет на поведение функции. Но стоит и здесь проверить несколько отличных значений, чтобы убедится, что в `Config.openaiApiKey` и вправду попадает переданное в функцию значение

Итак, тестовые случаи:

| Input                  | Output                                                                                   |
| ---------------------- | ---------------------------------------------------------------------------------------- |
| []                     | IllegalArgumentException("Unexpected number of arguments, expect exactly 3")             |
| ["_", "_", "_", "_"]   | IllegalArgumentException("Unexpected number of arguments, expect exactly 3")             |
| ["NaN", "_", "_"]      | IllegalArgumentException("chat-message-limit must be a positive integer, but got 'NaN'") |
| ["0", "_", "_"]        | IllegalArgumentException("chat-message-limit must be a positive integer, but got '0'")   |
| ["-1", "_", "_"]       | IllegalArgumentException("chat-message-limit must be a positive integer, but got '-1'")  |
| ["1", "NaB", "_"]      | IllegalArgumentException("log-requests must be either 'true' or 'false', but got 'NaB'") |
| ["1", "TrUe", "key0"]  | Config(messageLimit=1, logRequests=true, openaiApiKey="key0")                            |
| ["2", "FaLsE", "key1"] | Config(messageLimit=2, logRequests=false, openaiApiKey="key1")                           |
