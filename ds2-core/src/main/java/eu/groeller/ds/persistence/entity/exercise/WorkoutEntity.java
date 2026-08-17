package eu.groeller.ds.persistence.entity.exercise;

import eu.groeller.ds.domain.exercise.Workout;
import eu.groeller.ds.domain.exercise.WorkoutData;
import eu.groeller.ds.domain.exercise.WorkoutStatus;
import eu.groeller.ds.persistence.entity.AbstractJpaEntity;
import eu.groeller.ds.persistence.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Hibernate only; every other path must supply the domain-assigned id
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

    private WorkoutEntity(@NonNull UUID id) {
        super(id);
    }

    /**
     * User and workout type are passed separately because they must be the managed instances for
     * those rows, not copies rebuilt from the domain object's view of them.
     */
    public static WorkoutEntity from(@NonNull Workout workout,
                                     @NonNull UserEntity user,
                                     @NonNull WorkoutTypeEntity workoutType) {
        var entity = new WorkoutEntity(workout.getId());
        entity.user = user;
        entity.workoutType = workoutType;
        entity.status = workout.getStatus();
        entity.startTime = workout.getStartTime();
        entity.endTime = workout.getEndTime();
        return entity;
    }

    /** Keeps both ends of the association in step; the child owns the foreign key. */
    public void addEntry(@NonNull ExerciseEntryEntity entry) {
        entry.setWorkout(this);
        this.entries.add(entry);
    }

    // getDurationMs() is provided by the WorkoutData default method
}
