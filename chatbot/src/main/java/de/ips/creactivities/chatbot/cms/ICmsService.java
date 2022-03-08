package de.ips.creactivities.chatbot.cms;

import de.ips.creactivities.chatbot.cms.dm.Challenge;
import de.ips.creactivities.chatbot.cms.dm.Course;
import de.ips.creactivities.chatbot.cms.dm.Level;

import java.util.List;
import java.util.Optional;

public interface ICmsService {

    List<String> getAvailableLanguageCodes();

    List<String> getCourseIdentifiers(String languageCode);

    List<String> getLevelsForCourse(String languageCode, String courseIdentifier);

    List<String> getChallengesForLevel(String languageCode, String levelIdentifier);

    Optional<Course> findCourseById(String languageCode, String courseIdentifier);

    Optional<Level> findLevelById(String languageCode, String levelIdentifier);

    Optional<Challenge> findChallengeById(String languageCode, String challengeIdentifier);

    List<String> getAdminIds();

    String getAdminGroupChatId();

    String getPrivacyPolicy(String languageCode);

    String getPrivacyPolicyImage(String languageCode);

    String getRightsOfUse(String languageCode);

    String getRightsOfUseImage(String languageCode);

    String getTermsOfService(String languageCode);

    String getTermsOfServiceImage(String languageCode);

    void uploadSolution(String userId, String challengeId, String solutionText, List<byte[]> pictures);

}
