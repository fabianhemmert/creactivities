package de.ips.creactivities.chatbot.dm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    private String id;

    private boolean consentGiven;

    private UserState state;

    private String activeWorld;

    private String chatId;

    private String languageId;

    private String activeChallenge;

    private String activeCommand;

    private boolean blocked = false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SolutionEntity> solutions;

    @OneToMany(mappedBy = "user",  cascade = CascadeType.ALL)
    private List<EvaluationEntity> evaluations;

    @Override
    public String toString() {
        return "UserEntity{" +
                "id='" + id + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
