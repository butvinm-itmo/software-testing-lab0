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

record Dependency(String jarName, String jarUrl) {
    Path getJarPath() {
        return LIB_DIR.resolve(jarName);
    }
    URI getJarUri() {
        return URI.create(jarUrl);
    }
}

final Map<String, Dependency> DEPENDENCIES = Map.of(
    "junit",
    new Dependency(
        "junit-platform-console-standalone-1.12.0-RC1.jar",
        "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.12.0-RC1/junit-platform-console-standalone-1.12.0-RC1.jar"
    ),
    "openai",
    new Dependency(
        "openai-java-0.26.1.jar",
        "https://repo1.maven.org/maven2/com/openai/openai-java/0.26.1/openai-java-0.26.1.jar"
    ),
    "langchain4j",
    new Dependency(
        "langchain4j-open-ai-1.0.0-beta2.jar",
        "https://repo1.maven.org/maven2/dev/langchain4j/langchain4j-open-ai/1.0.0-beta2/langchain4j-open-ai-1.0.0-beta2.jar"
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
            System.err.println("Unknown target: " + args[0]);
            System.exit(1);
        }
    }
}

void installCmd() throws Exception {
    Files.createDirectories(LIB_DIR);
    for (Dependency dep : DEPENDENCIES.values()) {
        if (Files.notExists(dep.getJarPath())) {
            downloadJar(dep);
        }
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
    String classpath = DEPENDENCIES.values()
        .stream()
        .map(dep -> dep.getJarPath().toString())
        .collect(Collectors.joining(File.pathSeparator));

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
    return (
        BUILD_DIR +
        File.pathSeparator +
        DEPENDENCIES.values()
            .stream()
            .map(dep -> dep.getJarPath().toString())
            .collect(Collectors.joining(File.pathSeparator))
    );
}

void executeCommand(List<String> command)
    throws IOException, InterruptedException {
    Process process = new ProcessBuilder(command).inheritIO().start();
    int exitCode = process.waitFor();
    if (exitCode != 0) {
        throw new RuntimeException(
            "Command failed with exit code: " + exitCode
        );
    }
}
