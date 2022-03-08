package de.ips.creactivities.chatbot.cms.dto.course;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoursePrologueElement {

    /**
     * Mandatory.
     */
    @JsonProperty("course_prologue_text")
    private String text;

    /**
     * Optional
     */
    @JsonProperty("course_prologue_image")
    private String image;

    /**
     * Optional user answers
     */
    @JsonProperty("course_prologue_answers")
    private List<String> userAnswers;


    @JsonProperty("course_prologue_delay")
    private String delay;

}
