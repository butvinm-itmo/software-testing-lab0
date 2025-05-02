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

## Test plan

### App.parseConfig

**Decision table:**

| Conditions                                                                   | 0   | 1   | 2   | 3   | 4   | 5   | 6   | 7   |
| ---------------------------------------------------------------------------- | --- | --- | --- | --- | --- | --- | --- | --- |
| 3 args are provided                                                          |     |     |     |     | x   | x   | x   | x   |
| First arg is a string with a positive integer                                |     | x   |     | x   |     |     | x   | x   |
| Second arg is a case-insensitive string "true" or "false"                    |     |     | x   | x   |     | x   |     | x   |
| Outputs                                                                      |     |     |     |     |     |     |     |     |
| IllegalArgumentException("Unexpected number of arguments, expect exactly 3") | x   | x   | x   | x   |     |     |     |     |
| IllegalArgumentException("chat-message-limit must be a positive integer")    |     |     |     |     | x   | x   |     |     |
| IllegalArgumentException("log-requests must be either 'true' or 'false'")    |     |     |     |     |     |     | x   |     |
| Valid configuration object with corresponding values                         |     |     |     |     |     |     |     | x   |

We can see that function "short-circuits" on the first validation error and there is no need for the further testing.
Hence cases 0, 4, 6 and 7 provide full coverage of the possible output actions.
Although to achieve a full coverage of all pathes we need to consider more variants of the arguments values.

Lets make a boundary values analysis:

_args length:_

| Value | Validation result |
| ----- | ----------------- |
| < 3   | Invalid           |
| == 3  | Valid             |
| > 3   | Invalid           |

_chat-message-limit:_

| Value          | Validation result |
| -------------- | ----------------- |
| Not an integer | Invalid           |
| Integer <=0    | Invalid           |
| Integer > 0    | Valid             |

_logRequests:_

| Value                                  | Validation result |
| -------------------------------------- | ----------------- |
| "true", "True", "TrUe", "TRUE", ...    | Valid             |
| "false", "False, "FaLsE", "FALSE", ... | Valid             |
| Not a boolean string                   | Invalid           |

_openai-api-key:_

Technically might be an arbitrary string, but I prefer to have at least two different values of each output argument (`Config.openaiApiKey` in that case) to be sure it not hardcoded somewhere.

Summing up, test cases are:

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
