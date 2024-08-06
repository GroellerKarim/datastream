package eu.groeller.datastreamserver.domain;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Exercise extends AbstractEntity {

    @NotNull
    private String name;
    @Nullable
    private String description;
    @Nullable
    private String link;

    @NotEmpty
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<TrackingData> trackingData = HashSet.newHashSet(2);
}
