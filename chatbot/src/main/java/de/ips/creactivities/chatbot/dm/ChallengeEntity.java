package de.ips.creactivities.chatbot.dm;

import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Data
public class ChallengeEntity {

    @Id
    private String id;

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SolutionEntity> solutions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChallengeEntity that = (ChallengeEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
