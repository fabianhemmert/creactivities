package de.ips.creactivities.chatbot.dm;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Data
public class EvaluationEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * User that evaluated the Solution.
     */
    @ManyToOne
    private UserEntity user;

    private EvaluationType evaluationType;

    private int evaluationScore;

    private long issuedOn;

}
