package de.ips.creactivities.chatbot.dm;

import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Data
public class SolutionEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private UserEntity user;

    @ManyToOne
    private ChallengeEntity challenge;

    private boolean blocked = false;

    /**
     * Link to images or w/e the solution is.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> solutionValues;

    @OneToMany(cascade = CascadeType.ALL)
    private List<EvaluationEntity> evaluations;

    /**
     * Date when the solution was issued.
     */
    private long issuedOn;

    @Override
    public String toString() {
        return "SolutionEntity{" +
                "id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SolutionEntity solution = (SolutionEntity) o;
        return id.equals(solution.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @PreRemove
    public void dismissSolution() {
        this.user.getSolutions().remove(this);
        if (this.challenge != null) {
            this.challenge.getSolutions().remove(this);
        }
    }
}
