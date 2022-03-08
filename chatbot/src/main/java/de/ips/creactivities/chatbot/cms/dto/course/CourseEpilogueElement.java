package de.ips.creactivities.chatbot.cms.dto.course;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseEpilogueElement {

    /**
     * Mandatory.
     */
    @JsonProperty("course_epilogue_text")
    private String text;

    /**
     * Optional
      */
    @JsonProperty("course_epilogue_image")
    private String image;

    /**
     * Optional user answers
     */
    @JsonProperty("course_epilogue_answers")
    private List<String> userAnswers;

    @JsonProperty("course_epilogue_delay")
    private String delay;

}
