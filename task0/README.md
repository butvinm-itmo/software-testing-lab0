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

## Test plan

### Boundary values analysis

![](./graph.png)

Lets analyze both mathematical and programmatic behaviour of the function.

Mathematical function has different behaviour on the following ranges:

| Range          | Behaviour                       |
| -------------- | ------------------------------- |
| \|x\| > 1      | Series does not converge        |
| x = 0          | Series converge to 0            |
| x >= -1, x < 0 | Series converge to arctg(x) < 0 |
| x > 0, x < 1   | Series converge to arctg(x) > 0 |

From the programmatical point of view function has the following behaviour:

By **x**:

| Range        | Behaviour                                                                                                                                        |
| ------------ | ------------------------------------------------------------------------------------------------------------------------------------------------ |
| \|x\| > 1    | Throws IllegalArgumentException("arctg series converges only for \|x\| <= 1") because otherwise would never stop                                 |
| \|x\| <= 0.9 | Function evaluates fast, result is approximatelly arctg(x)                                                                                       |
| \|x\| > 0.95 | Function evaluates slowly (in more than 10000 iterations), result is approximatelly arctg(x), may exceed ITERATION_LIMIT and throw RuntimeException |

### Property based testing

We can also test that our function actually satisfies its mathematical properties.

For example we can verify that for any valid input arctg(x) == -arctg(-x).
