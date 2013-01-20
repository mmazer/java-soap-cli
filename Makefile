JAVAC=javac
JAVAC_FLAGS= -deprecation
SRC_DIR=src/java
LIB_DIR=lib
CLASS_DIR=$(LIB_DIR)/classes
JARS=$(LIB_DIR)/commons-cli-1.2.jar:$(LIB_DIR)/commons-httpclient-3.1.jar

SRC=$(SRC_DIR)/SOAPClient.java

build: $(CLASS_DIR)
	$(JAVAC) $(JAVAC_FLAGS) -sourcepath $(SRC_DIR) -classpath $(JARS) -d $(CLASS_DIR) $(SRC)

$(CLASS_DIR):
	@mkdir -p $(CLASS_DIR)

clean:
	@rm -rf $(CLASS_DIR)
