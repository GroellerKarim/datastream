package eu.groeller.ds.persistence.entity.exercise;

import eu.groeller.ds.domain.exercise.WorkoutData;
import eu.groeller.ds.domain.exercise.WorkoutStatus;
import eu.groeller.ds.persistence.entity.AbstractJpaEntity;
import eu.groeller.ds.persistence.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "workout")
public class WorkoutEntity extends AbstractJpaEntity implements WorkoutData {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "workout_type_id", nullable = false)
    private WorkoutTypeEntity workoutType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkoutStatus status;

    @Column(nullable = false)
    private OffsetDateTime startTime;

    private OffsetDateTime endTime;

    @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<ExerciseEntryEntity> entries = new ArrayList<>();

    // getDurationMs() is provided by the WorkoutData default method
}
