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

**run mutation testing:**

```bash
java --enable-preview build.java test-mutate
```

## Test cases

We will use coverage and mutation testing to figure out which test cases to create.

Lets start with simplest test case: graph with single node:

**Results:**

![](./assets/mutation_0.png)

Obviously we have never iterate the neighbors because we don't have any if them.
Lets add test case with some neighbors of the node.

**Results:**

![](./assets/mutation_1.png)

The remainging code path will only be triggered if there a cycle within the graph. Lets add one

**Results:**

![](./assets/mutation_2.png)

Now whole code is covered, but two mutatns still survide. 

The first is `resetCheckpoints()` - it is only used for testing, so we can safely ignore it.

The second is replacig of dfs result with empty list. It survives because we not validate dfs result, lets verify it too.

The final result is:
