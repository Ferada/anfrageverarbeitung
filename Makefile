JAVAC = javac

FLAGS = -cp .:lib/jopt-simple.jar -Xlint

SOURCES = \
	$(wildcard parser/gene/*.java) \
	$(wildcard parser/syntaxtree/*.java) \
	$(wildcard parser/visitor/*.java) \
	$(wildcard relationenalgebra/*.java) \
	$(wildcard main/*.java)
OBJECTS = $(patsubst %.java,%.class,$(SOURCES))

all: $(OBJECTS) database
dbclean:
	rm -Rf db/*
sql: all
	./sql.sh
kunden: all
	./kunden.sh
database:
	mkdir database

%.class: %.java
	$(JAVAC) $(FLAGS) $<

clean:
	rm -Rf $(OBJECTS)

.PHONY: clean
