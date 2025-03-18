import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

static final Path LIB_DIR = Paths.get("lib");

static final Path BUILD_DIR = Paths.get("target");

static final List<Path> SOURCE_DIRS = List.of(
    Paths.get("src"),
    Paths.get("tests")
);

static final String LANGCHAIN4J_VERSION = "1.0.0-beta2";

final Map<String, Dependency> DEPENDENCIES = Map.of(
    "junit",
    new Dependency(
        "org.junit.platform",
        "junit-platform-console-standalone",
        "1.12.0-RC1"
    ),
    "openai",
    new Dependency("com.openai", "openai-java", "0.26.1"),
    "langchain4j",
    new Dependency(
        "dev.langchain4j",
        "langchain4j",
        LANGCHAIN4J_VERSION,
        new Dependency(
            "dev.langchain4j",
            "langchain4j-http-client-jdk",
            LANGCHAIN4J_VERSION,
            new Dependency(
                "dev.langchain4j",
                "langchain4j-http-client",
                LANGCHAIN4J_VERSION
            )
        )
    ),
    "langchain4j-core",
    new Dependency(
        "dev.langchain4j",
        "langchain4j-core",
        LANGCHAIN4J_VERSION,
        new Dependency(
            "com.fasterxml.jackson.core",
            "jackson-databind",
            "2.18.3",
            new Dependency(
                "com.fasterxml.jackson.core",
                "jackson-core",
                "2.18.3"
            ),
            new Dependency(
                "com.fasterxml.jackson.core",
                "jackson-annotations",
                "2.18.3"
            )
        ),
        new Dependency("org.slf4j", "slf4j-api", "2.1.0-alpha1")
    ),
    "langchain4j-openai",
    new Dependency(
        "dev.langchain4j",
        "langchain4j-open-ai",
        LANGCHAIN4J_VERSION,
        new Dependency("com.knuddels", "jtokkit", "1.1.0")
    )
);

void main(String[] args) throws Exception {
    if (args.length == 0) {
        System.err.println("Expect target: install, build, test, run");
        System.exit(1);
    }
    switch (args[0]) {
        case "install" -> installCmd();
        case "build" -> buildCmd();
        case "test" -> testCmd();
        case "run" -> runCmd();
        default -> {
            System.err.println("Unknown target: %s".formatted(args[0]));
            System.exit(1);
        }
    }
}

void installCmd() throws Exception {
    Files.createDirectories(LIB_DIR);
    for (Dependency dep : DEPENDENCIES.values()) {
        installDependency(dep);
    }
}

void buildCmd() throws Exception {
    installCmd();
    cleanBuildDir();
    compileJavaSources();
}

void testCmd() throws Exception {
    buildCmd();
    executeCommand(
        List.of(
            "java",
            "-jar",
            DEPENDENCIES.get("junit").getJarPath().toString(),
            "execute",
            "--classpath",
            buildClassPath(),
            "--scan-classpath"
        )
    );
}

void runCmd() throws Exception {
    buildCmd();
    executeCommand(
        List.of("java", "-cp", buildClassPath(), "butvinm.lab0.task0.App")
    );
}

record Dependency(
    String groupId,
    String artifactId,
    String version,
    List<Dependency> dependencies
) {
    private static final String REPOSITORY = "https://repo1.maven.org/maven2";

    public Dependency(
        String groupId,
        String artifactId,
        String version,
        Dependency... dependencies
    ) {
        this(groupId, artifactId, version, Arrays.asList(dependencies));
    }

    public Dependency(String groupId, String artifactId, String version) {
        this(groupId, artifactId, version, List.of());
    }

    String getJarName() {
        return String.format("%s-%s.jar", artifactId, version);
    }

    String getJarUrl() {
        return String.format(
            "%s/%s/%s/%s/%s",
            REPOSITORY,
            groupId.replace(".", "/"),
            artifactId,
            version,
            getJarName()
        );
    }

    Path getJarPath() {
        return LIB_DIR.resolve(getJarName());
    }

    URI getJarUri() {
        return URI.create(getJarUrl());
    }
}

void installDependency(Dependency dep) throws Exception {
    System.out.print("Installing %s: ".formatted(dep.getJarPath()));
    if (Files.notExists(dep.getJarPath())) {
        System.out.println("Downloading jar %s".formatted(dep.getJarUri()));
        downloadJar(dep);
    } else {
        System.out.println("Installed");
    }
    for (Dependency transitiveDep : dep.dependencies()) {
        installDependency(transitiveDep);
    }
}

void downloadJar(Dependency dep) throws IOException {
    try (InputStream in = dep.getJarUri().toURL().openStream()) {
        Files.copy(in, dep.getJarPath());
    }
}

void cleanBuildDir() throws IOException {
    if (Files.exists(BUILD_DIR)) {
        Files.walk(BUILD_DIR)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
    }
}

void compileJavaSources() throws IOException, InterruptedException {
    List<Path> javaFiles = findJavaFiles();
    if (javaFiles.isEmpty()) {
        throw new RuntimeException("No Java files found in source directories");
    }
    String classpath = buildClassPath();

    List<String> command = new ArrayList<>();
    command.add("javac");
    command.add("-cp");
    command.add(classpath);
    command.add("-d");
    command.add(BUILD_DIR.toString());
    command.add("-Xlint:unchecked");
    javaFiles.forEach(path -> command.add(path.toString()));
    executeCommand(command);
}

List<Path> findJavaFiles() throws IOException {
    return SOURCE_DIRS.stream()
        .filter(Files::exists)
        .flatMap(dir -> {
            try {
                return Files.walk(dir);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        })
        .filter(path -> path.toString().endsWith(".java"))
        .collect(Collectors.toList());
}

String buildClassPath() {
    Set<Path> classpathEntries = new LinkedHashSet<>();
    classpathEntries.add(BUILD_DIR);
    for (Dependency dep : DEPENDENCIES.values()) {
        addDependencyToClasspath(dep, classpathEntries);
    }
    return classpathEntries
        .stream()
        .map(Path::toString)
        .collect(Collectors.joining(File.pathSeparator));
}

void addDependencyToClasspath(Dependency dep, Set<Path> classpathEntries) {
    classpathEntries.add(dep.getJarPath());
    for (Dependency transitiveDep : dep.dependencies()) {
        addDependencyToClasspath(transitiveDep, classpathEntries);
    }
}

void executeCommand(List<String> command)
    throws IOException, InterruptedException {
    System.out.println(
        "Execute command: %s".formatted(command.stream().collect(Collectors.joining(" ")))
    );
    Process process = new ProcessBuilder(command).inheritIO().start();
    int exitCode = process.waitFor();
    if (exitCode != 0) {
        throw new RuntimeException(
            "Command failed with exit code: %d".formatted(exitCode)
        );
    }
}
