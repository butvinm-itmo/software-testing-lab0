JUNIT_JAR=junit-platform-console-standalone-1.12.0-RC1.jar
JUNIT=java -jar lib/${JUNIT_JAR}
BUILD_DIR=build


.PHONY: deps
deps:
	mkdir -p lib
	cd lib && wget https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.12.0-RC1/${JUNIT_JAR}


.PHONY: tests-build
tests-build:
	rm -rf ${BUILD_DIR}
	javac -cp lib/${JUNIT_JAR} -d ${BUILD_DIR} tests/* src/*


.PHONY: tests-run
tests-run: tests-build
	 ${JUNIT} execute --classpath ${BUILD_DIR} --scan-classpath
