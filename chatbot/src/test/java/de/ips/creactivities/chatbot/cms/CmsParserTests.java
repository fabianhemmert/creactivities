package de.ips.creactivities.chatbot.cms;

import de.ips.creactivities.chatbot.cms.dto.CmsReferenceDto;
import de.ips.creactivities.chatbot.cms.dto.level.LevelDto;
import de.ips.creactivities.chatbot.cms.dto.level.LevelEpilogueElement;
import de.ips.creactivities.chatbot.cms.dto.level.LevelPrologueElement;
import de.ips.creactivities.chatbot.cms.dto.options.OptionsDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CmsParserTests {

    @Test
    public void testSinglelevel() throws IOException {

        Path singlelevel = Paths.get("src", "test", "resources", "cms", "single-level.json");
        String content = Files.readString(singlelevel, StandardCharsets.UTF_8);

        CmsParser cmsParser = new CmsParser();
        LevelDto levelDto = cmsParser.parseSingleLevel(content);
        Assertions.assertEquals("Dorf der Tüftler", levelDto.getName());
        Assertions.assertEquals("Dies ist das Dorf der Tüftler", levelDto.getDescription());

    }

    @Test
    public void testlevelList() throws IOException {

        Path multiplelevels = Paths.get("src", "test", "resources", "cms", "level-list.json");
        String content = Files.readString(multiplelevels, StandardCharsets.UTF_8);

        CmsParser cmsParser = new CmsParser();
        List<LevelDto> levelDtos = cmsParser.parseLevelList(content);

        Assertions.assertEquals(2, levelDtos.size());

        LevelDto firstlevelDto = levelDtos.get(0);
        Assertions.assertEquals("Welt der Wunder", firstlevelDto.getName());
        Assertions.assertEquals("Beschreibung der Welt der Wunder", firstlevelDto.getDescription());

        LevelDto secondlevelDto = levelDtos.get(1);
        Assertions.assertEquals("Dorf der Tüftler", secondlevelDto.getName());
        Assertions.assertEquals("Dies ist das Dorf der Tüftler", secondlevelDto.getDescription());

    }


    @Test
    public void testLevelWithPrologueAndEpilogue() throws IOException {

        Path multiplelevels = Paths.get("src", "test", "resources", "cms", "cms-level.json");
        String content = Files.readString(multiplelevels, StandardCharsets.UTF_8);

        CmsParser cmsParser = new CmsParser();
        List<LevelDto> levelDtos = cmsParser.parseLevelList(content);
        LevelDto levelDto = levelDtos.get(0);

        Assertions.assertEquals("Wald der Erfindungen", levelDto.getName());
        Assertions.assertEquals("Dies ist das Wald der Erfindungen", levelDto.getDescription());

        List<LevelPrologueElement> levelPrologue = levelDto.getLevelPrologue();
        Assertions.assertEquals(2, levelPrologue.size());
        Assertions.assertEquals("Schau mal, dort vorne scheint der Eingang zum Wald der Erfindungen zu sein! Aber ich kann zwei Wege erkennen!", levelPrologue.get(0).getText());
        Assertions.assertEquals("Wo müssen wir lang?", levelPrologue.get(0).getUserAnswers().get(0).getAnswer());

        List<LevelEpilogueElement> levelEpilogue = levelDto.getLevelEpilogue();
        Assertions.assertEquals(2, levelEpilogue.size());
        Assertions.assertEquals("OH, da ist sie schon!", levelEpilogue.get(0).getText());
        Assertions.assertEquals("Nichts wie hin!", levelEpilogue.get(0).getUserAnswers().get(0).getAnswer());

        List<CmsReferenceDto> challenges = levelDto.getChallenges();
        Assertions.assertEquals(2, challenges.size());

    }

    @Test
    public void testLevel2WithPrologueAndEpilogue() throws IOException {

        Path multiplelevels = Paths.get("src", "test", "resources", "cms", "level2.json");
        String content = Files.readString(multiplelevels, StandardCharsets.UTF_8);

        CmsParser cmsParser = new CmsParser();
        List<LevelDto> levelDtos = cmsParser.parseLevelList(content);
        LevelDto levelDto = levelDtos.get(0);

        Assertions.assertEquals("Wald der Erfindungen", levelDto.getName());
        Assertions.assertEquals("Dies ist das Wald der Erfindungen", levelDto.getDescription());

        List<LevelPrologueElement> levelPrologue = levelDto.getLevelPrologue();
        Assertions.assertEquals(2, levelPrologue.size());
        Assertions.assertEquals("Schau mal, dort vorne scheint der Eingang zum Wald der Erfindungen zu sein! Aber ich kann zwei Wege erkennen!", levelPrologue.get(0).getText());
        Assertions.assertEquals("Wo müssen wir lang?", levelPrologue.get(0).getUserAnswers().get(0).getAnswer());

        List<LevelEpilogueElement> levelEpilogue = levelDto.getLevelEpilogue();
        Assertions.assertEquals(2, levelEpilogue.size());
        Assertions.assertEquals("OH, da ist sie schon!", levelEpilogue.get(0).getText());
        Assertions.assertEquals("Nichts wie hin!", levelEpilogue.get(0).getUserAnswers().get(0).getAnswer());

        List<CmsReferenceDto> challenges = levelDto.getChallenges();
        Assertions.assertEquals(5, challenges.size());

    }

    /**
     * See if this is a real problem with false values for empty sets.
     * Remove the Test if problem is fixed on CMS side.
     *
     * @throws IOException if an error occurs.
     */
    @Test
    public void testSinglelevelWithFalseObjects() throws IOException {
        Path singlelevel = Paths.get("src", "test", "resources", "cms", "cms-level-with-false.json");
        String content = Files.readString(singlelevel, StandardCharsets.UTF_8);

        CmsParser cmsParser = new CmsParser();
        LevelDto levelDto = cmsParser.parseSingleLevel(content);
        Assertions.assertEquals("Tal der Einfallslosigkeit", levelDto.getName());
        Assertions.assertEquals("einfallslose Beschreibung", levelDto.getDescription());

    }

    @Test
    public void testOptions() throws IOException {
        Path singlelevel = Paths.get("src", "test", "resources", "cms", "cms-options.json");
        String content = Files.readString(singlelevel, StandardCharsets.UTF_8);

        CmsParser cmsParser = new CmsParser();
        OptionsDto dto = cmsParser.parseOptions(content);

        Assertions.assertEquals("asdfasdfasdfada", dto.getPolicy());
        Assertions.assertEquals("47668302", dto.getAdmins().get(0).getIdentifier());


    }
}
