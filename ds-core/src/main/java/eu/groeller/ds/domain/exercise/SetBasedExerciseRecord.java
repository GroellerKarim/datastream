package eu.groeller.ds.domain.exercise;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "set_based_exercise_record")
public class SetBasedExerciseRecord extends ExerciseRecord {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "exercise_record_id")
    private List<ExerciseSet> sets = new ArrayList<>();

    @Column(name = "average_rest_time")
    private Double averageRestTime;
}
