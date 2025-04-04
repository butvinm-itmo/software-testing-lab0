final Path LIB_DIR = Paths.get("lib");

final Path TARGET_DIR = Paths.get("target");

final Path CLASSES_DIR = TARGET_DIR.resolve("classes");

final Path TEST_CLASSES_DIR = TARGET_DIR.resolve("test-classes");

final Path INSTRUCTED_DIR = TARGET_DIR.resolve("instructed");

final List<Path> CP_DIRS = List.of(CLASSES_DIR, TEST_CLASSES_DIR, INSTRUCTED_DIR);

final Path COVERAGE_REPORT_DIR = TARGET_DIR.resolve("coverage-report");

final Path SOURCE_DIR = Paths.get("src");

final String MAIN_CLASS = "butvinm.lab0.task2.App";

final DependencyManager dm = new DependencyManager(LIB_DIR, "https://repo1.maven.org/maven2");

final String LANGCHAIN4J_VERSION = "1.0.0-beta2";

final String JACKSON_VERSION = "2.18.3";

final Map<String, Dependency> DEPENDENCIES = Map.of(
    "junit",
    dm.fromMaven("org.junit.platform", "junit-platform-console-standalone", "1.12.0-RC1"),
    "jacoco",
    dm.fromMaven("org.jacoco", "org.jacoco.cli", "0.8.13", "nodeps"),
    "jacoco-agent",
    dm.fromMaven("org.jacoco", "org.jacoco.agent", "0.8.13", "runtime", dm.fromMaven("org.jacoco", "org.jacoco.agent", "0.8.13")),
    "checkstyle",
    dm.fromUrl(
        "checkstyle-10.21.3-all.jar",
        "https://github.com/checkstyle/checkstyle/releases/download/checkstyle-10.21.3/checkstyle-10.21.3-all.jar"
    ),
    "langchain4j",
    dm.fromMaven(
        "dev.langchain4j",
        "langchain4j",
        LANGCHAIN4J_VERSION,
        dm.fromMaven(
            "dev.langchain4j",
            "langchain4j-http-client-jdk",
            LANGCHAIN4J_VERSION,
            dm.fromMaven("dev.langchain4j", "langchain4j-http-client", LANGCHAIN4J_VERSION)
        )
    ),
    "langchain4j-core",
    dm.fromMaven(
        "dev.langchain4j",
        "langchain4j-core",
        LANGCHAIN4J_VERSION,
        dm.fromMaven(
            "com.fasterxml.jackson.core",
            "jackson-databind",
            JACKSON_VERSION,
            dm.fromMaven("com.fasterxml.jackson.core", "jackson-core", JACKSON_VERSION),
            dm.fromMaven("com.fasterxml.jackson.core", "jackson-annotations", JACKSON_VERSION)
        ),
        dm.fromMaven("org.slf4j", "slf4j-api", "2.1.0-alpha1"),
        dm.fromMaven(
            "org.tinylog",
            "slf4j-tinylog",
            "2.8.0-M1",
            dm.fromMaven("org.tinylog", "tinylog-api", "2.8.0-M1"),
            dm.fromMaven("org.tinylog", "tinylog-impl", "2.8.0-M1")
        )
    ),
    "langchain4j-openai",
    dm.fromMaven("dev.langchain4j", "langchain4j-open-ai", LANGCHAIN4J_VERSION, dm.fromMaven("com.knuddels", "jtokkit", "1.1.0"))
);

void main(String... args) throws Exception {
    if (args.length == 0) {
        System.err.println("Expect target: install, build, test, run");
        System.exit(1);
    }
    var restArgs = Arrays.copyOfRange(args, 1, args.length);
    switch (args[0]) {
        case "install" -> installCmd();
        case "build" -> buildCmd();
        case "test" -> testCmd();
        case "lint" -> lintCmd();
        case "run" -> runCmd(List.of(restArgs));
        default -> {
            System.err.println("Unknown target: %s".formatted(args[0]));
            System.exit(1);
        }
    }
}

void installCmd() throws Exception {
    Files.createDirectories(LIB_DIR);
    DEPENDENCIES.values().forEach(dep -> pohui(() -> dm.installDependency(dep, true)).run());
}

void buildCmd() throws Exception {
    installCmd();
    cleanDir(TARGET_DIR);
    compileJavaSources(SOURCE_DIR.resolve("main"), DEPENDENCIES.values(), CP_DIRS, CLASSES_DIR);
    compileJavaSources(SOURCE_DIR.resolve("test"), DEPENDENCIES.values(), CP_DIRS, TEST_CLASSES_DIR);
}

void testCmd() throws Exception {
    buildCmd();

    cmd("java", "-jar", DEPENDENCIES.get("jacoco").jarPath(), "instrument", "--dest", INSTRUCTED_DIR);
    // java -javaagent:lib/org.jacoco.agent-0.8.13.jar=destfile=target/jacoco.exec \
    //      -cp "target/instrumented:target/classes:lib/*" \
    //      org.junit.runner.JUnitCore com.example.TestClass  # Replace with your test class

    var jacocoExecFile = TARGET_DIR.resolve("jacoco.exec");

    cmd(
        "java",
        "-javaagent:%s=destfile=%s".formatted(DEPENDENCIES.get("jacoco-agent").jarPath(), jacocoExecFile),
        "-cp",
        buildClassPath(CP_DIRS, DEPENDENCIES.values()),
        "-jar",
        DEPENDENCIES.get("junit").jarPath(),
        "execute",
        "--classpath",
        buildClassPath(CP_DIRS, DEPENDENCIES.values()),
        "--scan-classpath"
    );

    // java -jar lib/org.jacoco.cli-0.8.13-nodeps.jar report target/jacoco.exec \
    //      --classfiles target/classes \
    //      --sourcefiles src/main/java \
    //      --html target/report

    cmd(
        "java",
        "-jar",
        DEPENDENCIES.get("jacoco").jarPath(),
        "report",
        jacocoExecFile,
        "--classfiles",
        CLASSES_DIR,
        "--sourcefiles",
        SOURCE_DIR.resolve("main/java"),
        "--html",
        COVERAGE_REPORT_DIR
    );
    // cmd(
    //     "java",
    //     "-jar",
    //     DEPENDENCIES.get("junit").jarPath().toString(),
    //     "execute",
    //     "--classpath",
    //     buildClassPath(CP_DIRS, DEPENDENCIES.values()),
    //     "--scan-classpath"
    // );
}

void lintCmd() throws Exception {
    buildCmd();
    var command = commands("java", "-jar", DEPENDENCIES.get("checkstyle").jarPath().toString(), "-c", "checkstyle.xml", "--debug");
    command.addAll(findJavaFiles(SOURCE_DIR).stream().map(Path::toString).toList());
    cmd(command);
}

void runCmd(List<String> args) throws Exception {
    buildCmd();
    var command = commands("java", "--enable-preview", "-cp", buildClassPath(CP_DIRS, DEPENDENCIES.values()), MAIN_CLASS);
    command.addAll(args);
    cmd(command);
}

// Build library
void cmd(Object... command) throws IOException, InterruptedException {
    cmd(Arrays.asList(command));
}

void cmd(List<Object> command) throws IOException, InterruptedException {
    var args = command.stream().map(Object::toString).toList();

    System.out.println("Execute command: %s".formatted(String.join(" ", args)));

    var process = new ProcessBuilder(args).inheritIO().start();
    var exitCode = process.waitFor();
    if (exitCode != 0) {
        System.err.println("Command failed with exit code: %d".formatted(exitCode));
        System.exit(exitCode);
    }
}

ArrayList<Object> commands(Object... items) {
    return new ArrayList<Object>(List.of(items));
}

record Dependency(String jarName, Path jarPath, URI jarUri, List<Dependency> subDependencies) {}

class DependencyManager {

    final Path libDir;
    final String registry;

    public DependencyManager(Path libDir, String registry) {
        this.libDir = libDir;
        this.registry = registry;
    }

    public Dependency fromUrl(String jarName, String jarUrl, Dependency... subDependencies) {
        var jarPath = this.libDir.resolve(jarName);
        var jarUri = URI.create(jarUrl);
        return new Dependency(jarName, jarPath, jarUri, Arrays.asList(subDependencies));
    }

    public Dependency fromMaven(String groupId, String artifactId, String version, Dependency... subDependencies) {
        var jarName = String.format("%s-%s.jar", artifactId, version);
        var jarPath = this.libDir.resolve(jarName);
        var jarUrl = String.format("%s/%s/%s/%s/%s", this.registry, groupId.replace(".", "/"), artifactId, version, jarName);
        var jarUri = URI.create(jarUrl);
        return new Dependency(jarName, jarPath, jarUri, Arrays.asList(subDependencies));
    }

    public Dependency fromMaven(String groupId, String artifactId, String version, String modifier, Dependency... subDependencies) {
        var jarName = String.format("%s-%s-%s.jar", artifactId, version, modifier);
        var jarPath = this.libDir.resolve(jarName);
        var jarUrl = String.format("%s/%s/%s/%s/%s", this.registry, groupId.replace(".", "/"), artifactId, version, jarName);
        var jarUri = URI.create(jarUrl);
        return new Dependency(jarName, jarPath, jarUri, Arrays.asList(subDependencies));
    }

    void installDependency(Dependency dep, boolean verbose) throws Exception {
        if (Files.notExists(dep.jarPath())) {
            if (verbose) System.out.println("Downloading jar %s".formatted(dep.jarUri()));
            downloadJar(dep);
        }
        if (verbose) System.out.println("Installed %s".formatted(dep.jarName()));
        dep.subDependencies().stream().forEach(sub -> pohui(() -> installDependency(sub, verbose)).run());
    }

    void downloadJar(Dependency dep) throws IOException {
        try (InputStream in = dep.jarUri().toURL().openStream()) {
            Files.copy(in, dep.jarPath());
        }
    }
}

void cleanDir(Path dir) throws IOException {
    if (Files.exists(dir)) {
        Files.walk(dir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
    }
}

void compileJavaSources(Path sourceDir, Collection<Dependency> dependencies, List<Path> cpDirs, Path buildDir)
    throws IOException, InterruptedException {
    var javaFiles = findJavaFiles(sourceDir);
    if (javaFiles.isEmpty()) {
        throw new RuntimeException("No Java files found in source directories");
    }
    var classpath = buildClassPath(cpDirs, dependencies);

    var command = commands("javac", "--enable-preview", "--source", "24", "-cp", classpath, "-d", buildDir, "-Xlint:unchecked");
    command.addAll(javaFiles);
    cmd(command);
}

List<Path> findJavaFiles(Path sourceDir) throws IOException {
    return Files.walk(sourceDir).filter(path -> path.toString().endsWith(".java")).toList();
}

String buildClassPath(List<Path> cpDirs, Collection<Dependency> dependencies) {
    var classpathEntries = new LinkedHashSet<Path>();
    classpathEntries.addAll(cpDirs);
    dependencies.stream().forEach(dep -> addDependencyToClasspath(dep, classpathEntries));
    return classpathEntries.stream().map(Path::toString).collect(Collectors.joining(File.pathSeparator));
}

void addDependencyToClasspath(Dependency dep, Set<Path> classpathEntries) {
    classpathEntries.add(dep.jarPath());
    dep.subDependencies().stream().forEach(sub -> addDependencyToClasspath(sub, classpathEntries));
}

@FunctionalInterface
interface UnsafeSupplier<R> {
    R produce() throws Exception;
}

@FunctionalInterface
interface UnsafeRunnable {
    void run() throws Exception;
}

<R> Supplier<R> pohui(UnsafeSupplier<R> func) {
    return () -> {
        try {
            return func.produce();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };
}

Runnable pohui(UnsafeRunnable func) {
    return () -> {
        try {
            func.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };
}
