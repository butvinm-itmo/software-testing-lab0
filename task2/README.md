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

## Funny testing ideas from DeepSeek

### **Existential Testing**  
   - **What?** Ask meta-questions about the system’s purpose.  
   - **Example Tests**:  
     - If the system vanished, would anyone notice?  
     - Does the codebase contain more test logic than business logic?  
     - Is the system’s complexity fractal? (Zoom into any module, and it’s just as complicated as the whole.)  
   - **Tool**: A whiskey bottle and late-night existential dread.  

### **Animal Testing** 🐾  
   - **What?** Use pets or untrained humans (e.g., toddlers) to interact with your UI.  
   - **Why?** Uncovers usability flaws that logical humans would never find.  
   - **How:**  
     - Let a cat walk on a keyboard during a live demo.  
     - Hand your app to a toddler and see which buttons they mash.  
   - **Weird Angle:** Publish a "Cat-Driven Development" blog post.  

### **The "IKEA Manual" Test** 🛠️  
   - **What?** Remove all text/copy from your UI, leaving only icons and diagrams.  
   - **Goal:** Can users navigate your app like they’d assemble a KALLAX shelf?  
   - **Real Value:** Tests visual intuition and accessibility.  

### **Haunted Code Testing** 👻  
   - **What?** Seed your codebase with "ghost code"—functionality that does nothing but looks scary.  
   - **Examples:**  
     - Add a `checkForCurse()` method that randomly logs "🔮".  
     - Write comments in Latin from "The Exorcist."  
   - **Goal:** See if future developers notice (or fear to remove it).  
