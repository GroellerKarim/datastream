package eu.groeller.ds.persistence.exercise;

import eu.groeller.ds.config.TestContainersConfig;
import eu.groeller.ds.domain.exercise.ExerciseDefinition;
import eu.groeller.ds.domain.exercise.ExerciseType;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(TestContainersConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ExerciseDefinitionRepositoryTest {

    @Autowired
    private ExerciseDefinitionRepository repository;

    @Test
    void should_save_valid_exercise_definition() {
        // given
        ExerciseDefinition exercise = new ExerciseDefinition("Push-ups", ExerciseType.SETS_REPS);

        // when
        ExerciseDefinition saved = repository.save(exercise);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Push-ups");
        assertThat(saved.getType()).isEqualTo(ExerciseType.SETS_REPS);
    }

    @Test
    void should_fail_on_null_name() {
        // given
        ExerciseDefinition exercise = new ExerciseDefinition(null, ExerciseType.SETS_REPS);

        // when/then
        assertThatThrownBy(() -> repository.save(exercise))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void should_fail_on_null_type() {
        // given
        ExerciseDefinition exercise = new ExerciseDefinition("Push-ups", null);

        // when/then
        assertThatThrownBy(() -> repository.save(exercise))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void should_save_exercise_with_same_name_but_different_type() {
        // given
        ExerciseDefinition setsRepsExercise = new ExerciseDefinition("Running", ExerciseType.SETS_REPS);
        ExerciseDefinition distanceExercise = new ExerciseDefinition("Running", ExerciseType.DISTANCE);

        // when
        repository.save(setsRepsExercise);
        ExerciseDefinition saved = repository.save(distanceExercise);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(repository.count()).isEqualTo(2);
    }

    @Test
    void should_check_exists_by_name_and_type() {
        // given
        ExerciseDefinition exercise = new ExerciseDefinition("Push-ups", ExerciseType.SETS_REPS);
        repository.save(exercise);

        // when/then
        assertThat(repository.existsByNameAndType("Push-ups", ExerciseType.SETS_REPS)).isTrue();
        assertThat(repository.existsByNameAndType("Push-ups", ExerciseType.DISTANCE)).isFalse();
        assertThat(repository.existsByNameAndType("Pull-ups", ExerciseType.SETS_REPS)).isFalse();
    }
}