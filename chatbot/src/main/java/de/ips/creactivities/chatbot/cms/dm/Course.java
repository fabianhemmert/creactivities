package de.ips.creactivities.chatbot.cms.dm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    private String identifier;

    private Boolean active;

    private List<String> levelIdentifiers;

    private List<InteractionElement> prologue;

    private List<InteractionElement> epilogue;

}
