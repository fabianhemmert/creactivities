package de.ips.creactivities.chatbot.cms.dm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateSolution {

    private String text;

    @URL
    private String image;

}
