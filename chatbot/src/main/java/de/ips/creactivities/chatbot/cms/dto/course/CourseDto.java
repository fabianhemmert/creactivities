package de.ips.creactivities.chatbot.cms.dto.course;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.ips.creactivities.chatbot.cms.dto.AbstractObjectDeserializer;
import de.ips.creactivities.chatbot.cms.dto.CmsReferenceDto;
import de.ips.creactivities.chatbot.cms.dto.challenge.ChallengeDto;
import lombok.Data;

import java.io.IOException;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseDto {

    private Integer id;

    @JsonProperty("course_name")
    private String name;

    @JsonProperty("course_description")
    private String description;

    @JsonProperty("course_active")
    private Boolean active;

    @JsonProperty("course_image")
    private String image;

    @JsonProperty("course_prologue")
    private List<CoursePrologueElement> coursePrologue;

    @JsonDeserialize(using =  CmsReferenceDto.Deserializer.class)
    @JsonProperty("course_levels")
    private List<CmsReferenceDto> levels;

    @JsonProperty("course_epilogue")
    private List<CourseEpilogueElement> courseEpilogue;

    public static class ObjectDeserializer extends AbstractObjectDeserializer<CourseDto> {
        @Override
        public CourseDto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            return internalDeserialize(jsonParser, deserializationContext, CourseDto.class);
        }
    }

}
