package de.ips.creactivities.chatbot.cms.dto.course;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.ips.creactivities.chatbot.cms.dto.LinksDto;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CmsCourseDto {

    @JsonProperty("id")
    @NotNull
    private Integer id;

    @JsonDeserialize(using = CourseDto.ObjectDeserializer.class)
    @JsonProperty("acf")
    private CourseDto course;

    @JsonProperty("_links")
    private LinksDto links;

}
