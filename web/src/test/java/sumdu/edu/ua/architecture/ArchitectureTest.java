package sumdu.edu.ua.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.servlet.http.HttpServlet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.constructors;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchitectureTest {
    private static JavaClasses importedClasses;

    @BeforeAll
    static void setUp() {
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("sumdu.edu.ua");
    }

    @Test
    void webShouldNotDependOnPersistence() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..web..")
                .should().dependOnClassesThat().resideInAPackage("..persistence..");
        rule.check(importedClasses);
    }

    @Test
    void coreShouldNotDependOnServletOrJdbc() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..core..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("jakarta.servlet..", "javax.servlet..", "java.sql..", "javax.sql..");
        rule.check(importedClasses);
    }

    @Test
    void controllersShouldResideOnlyInWeb() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Controller")
                .or().areAssignableTo(HttpServlet.class)
                .should().resideInAPackage("..web.controller..");
        rule.check(importedClasses);
    }

    @Test
    void repositoriesShouldResideOnlyInPersistence() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Repository")
                .should().resideInAPackage("..persistence.repository..");
        rule.check(importedClasses);
    }

    @Test
    void allCoreClassesShouldHavePublicConstructors() {
        ArchRule rule = constructors()
                .that().areDeclaredInClassesThat().resideInAPackage("..core..")
                .should().haveModifier(JavaModifier.PUBLIC);
        rule.check(importedClasses);
    }
}
