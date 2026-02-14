package eu.groeller.ds.service.utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DtoUtilsTest {

    private record TestDto(String stringField1, String stringField2, Integer intField1, Integer intField2) {
    }

    @Test
    void checkAllNullsAndBlanks_WhenFieldsAreNull_ThrowsNullPointerException() {
        var dto = new TestDto(null, "test2", 1, 2);

        assertThrows(NullPointerException.class, () -> DtoUtils.checkAllNullsAndBlanks(dto));
    }

    @Test
    void checkAllNullsAndBlanks_WhenStringFieldsAreBlank_ThrowsIllegalArgumentException() {
        var dto = new TestDto("", "test2", 1, 2);

        assertThrows(IllegalArgumentException.class, () -> DtoUtils.checkAllNullsAndBlanks(dto));
    }

    @Test
    void checkAllNullsAndBlanks_WhenAllFieldsValidAndNotBlank_DoesNotThrow() {
        var dto = new TestDto("test1", "test2", 1, 2);

        assertDoesNotThrow(() -> DtoUtils.checkAllNullsAndBlanks(dto));
    }


    @Test
    void checkAllNulls_WhenMultipleFieldsAreNull_ThrowsNullPointerException() {
        var dto = new TestDto(null, null, 1, 2);

        assertThrows(NullPointerException.class, () -> DtoUtils.checkAllNulls(dto));
    }

    @Test
    void checkAllNulls_WhenAllFieldsNotNull_DoesNotThrow() {
        var dto = new TestDto("test1", "test2", 1, 2);

        assertDoesNotThrow(() -> DtoUtils.checkAllNulls(dto));
    }

    @Test
    void checkNulls_WhenSpecifiedFieldsAreNull_ThrowsNullPointerException() {
        var dto = new TestDto(null, null, 1, 2);

        assertThrows(NullPointerException.class,
                () -> DtoUtils.checkNulls(dto, List.of("stringField1", "stringField2")));
    }

    @Test
    void checkNulls_WhenFieldDoesNotExist_ThrowsRuntimeException() {
        var dto = new TestDto("test1", "test2", 1, 2);

        assertThrows(RuntimeException.class,
                () -> DtoUtils.checkNulls(dto, List.of("nonExistentField")));
    }

    @Test
    void checkBlanks_WhenMultipleStringFieldsAreBlank_ThrowsIllegalArgumentException() {
        var dto = new TestDto("", "   ", 1, 2);

        assertThrows(IllegalArgumentException.class,
                () -> DtoUtils.checkBlanks(dto, List.of("stringField1", "stringField2")));
    }

    @Test
    void checkBlanks_WhenStringFieldsHaveValues_DoesNotThrow() {
        var dto = new TestDto("test1", "test2", 1, 2);

        assertDoesNotThrow(() -> DtoUtils.checkBlanks(dto, List.of("stringField1", "stringField2")));
    }

    @Test
    void checkBlankString_WhenStringIsBlank_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> DtoUtils.checkBlankString("", "testField"));
    }

    @Test
    void checkBlankString_WhenStringIsWhitespace_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> DtoUtils.checkBlankString("   ", "testField"));
    }

    @Test
    void checkBlankString_WhenStringHasValue_DoesNotThrow() {
        assertDoesNotThrow(() -> DtoUtils.checkBlankString("test", "testField"));
    }

}
