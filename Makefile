JAVAC = javac

FLAGS = -cp .:lib/jopt-simple.jar -Xlint

SOURCES = \
	$(wildcard parser/gene/*.java) \
	$(wildcard parser/syntaxtree/*.java) \
	$(wildcard parser/visitor/*.java) \
	$(wildcard relationenalgebra/*.java) \
	$(wildcard main/*.java)
OBJECTS = $(patsubst %.java,%.class,$(SOURCES))

EXPORT = ../Anfrageverarbeitung_Frahm

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

export:
	@cp parser/gene/*.{java,class} $(EXPORT)/parser/gene/
	@cp parser/syntaxtree/*.{java,class} $(EXPORT)/parser/syntaxtree/
	@cp parser/visitor/*.{java,class} $(EXPORT)/parser/visitor/
	@cp parser/*.jj $(EXPORT)/parser/
	@cp relationenalgebra/*.{java,class} $(EXPORT)/relationenalgebra/
	@cp main/*.{java,class} $(EXPORT)/main/
	@echo "Enter matriculation number."
	@sed -e "s/MATRIKELNUMMER/`cat`/" README > $(EXPORT)/README
	@rm -f $(EXPORT)/database/*

dist: export
	@cd $(EXPORT)/..; tar zcf Anfrageverarbeitung_Frahm.tar.gz Anfrageverarbeitung_Frahm

clean:
	rm -Rf $(OBJECTS)

.PHONY: clean
