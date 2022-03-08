package de.ips.creactivities.chatbot.basemocks;

import de.ips.creactivities.chatbot.cms.ICmsService;
import de.ips.creactivities.chatbot.cms.dm.Challenge;
import de.ips.creactivities.chatbot.cms.dm.Course;
import de.ips.creactivities.chatbot.cms.dm.Level;

import java.util.List;
import java.util.Optional;

public abstract class CmsServiceMockBase implements ICmsService {

    @Override
    public List<String> getAvailableLanguageCodes() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public List<String> getCourseIdentifiers(String languageCode) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public List<String> getLevelsForCourse(String languageCode, String courseIdentifier) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public List<String> getChallengesForLevel(String languageCode, String levelIdentifier) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public Optional<Course> findCourseById(String languageCode, String courseIdentifier) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public Optional<Level> findLevelById(String languageCode, String levelIdentifier) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public Optional<Challenge> findChallengeById(String languageCode, String challengeIdentifier) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public List<String> getAdminIds() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public String getAdminGroupChatId() {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public String getPrivacyPolicy(String languageCode) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public String getPrivacyPolicyImage(String languageCode) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public String getRightsOfUse(String languageCode) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public String getRightsOfUseImage(String languageCode) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public String getTermsOfService(String languageCode) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public String getTermsOfServiceImage(String languageCode) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }

    @Override
    public void uploadSolution(String userId, String challengeId, String solutionText, List<byte[]> pictures) {
        throw new NoSuchMethodError("Not implemented in base mock, fix your implementation!");
    }
}
