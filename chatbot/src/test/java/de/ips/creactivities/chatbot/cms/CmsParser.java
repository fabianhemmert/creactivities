package de.ips.creactivities.chatbot.cms;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ips.creactivities.chatbot.cms.dto.options.CmsOptionsDto;
import de.ips.creactivities.chatbot.cms.dto.course.CmsCourseDto;
import de.ips.creactivities.chatbot.cms.dto.level.CmsLevelDto;
import de.ips.creactivities.chatbot.cms.dto.course.CourseDto;
import de.ips.creactivities.chatbot.cms.dto.level.LevelDto;
import de.ips.creactivities.chatbot.cms.dto.options.OptionsDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CmsParser {

    public LevelDto parseSingleLevel(String content) throws IOException {

        ObjectMapper mapper = createObjectMapper();
        CmsLevelDto dto = mapper.readValue(content, CmsLevelDto.class);

        return dto.getLevel();
    }

    public List<LevelDto> parseLevelList(String content) throws IOException {

        ObjectMapper mapper = createObjectMapper();
        CmsLevelDto[] dtos = mapper.readValue(content, CmsLevelDto[].class);

        List<LevelDto> result = new ArrayList<>();
        for (int i = 0; i < dtos.length; i++) {
            result.add(dtos[i].getLevel());
        }

        return result;
    }

    public OptionsDto parseOptions(String content) throws IOException {
        ObjectMapper mapper = createObjectMapper();
        CmsOptionsDto dtos = mapper.readValue(content, CmsOptionsDto.class);

        return dtos.getOption();
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper;
    }

}
