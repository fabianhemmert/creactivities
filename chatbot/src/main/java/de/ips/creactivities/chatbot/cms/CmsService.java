package de.ips.creactivities.chatbot.cms;

import de.ips.creactivities.chatbot.ChatbotProperties;
import de.ips.creactivities.chatbot.cms.dm.Challenge;
import de.ips.creactivities.chatbot.cms.dm.Course;
import de.ips.creactivities.chatbot.cms.dm.Level;
import de.ips.creactivities.chatbot.cms.dto.challenge.ChallengeDto;
import de.ips.creactivities.chatbot.cms.dto.challenge.CmsChallengeDto;
import de.ips.creactivities.chatbot.cms.dto.course.CmsCourseDto;
import de.ips.creactivities.chatbot.cms.dto.course.CourseDto;
import de.ips.creactivities.chatbot.cms.dto.level.CmsLevelDto;
import de.ips.creactivities.chatbot.cms.dto.level.LevelDto;
import de.ips.creactivities.chatbot.cms.dto.post.CmsPostDto;
import de.ips.creactivities.chatbot.cms.dto.post.CmsPostResponseDto;
import de.ips.creactivities.chatbot.cms.dto.options.AdminIdDto;
import de.ips.creactivities.chatbot.cms.dto.options.CmsOptionsDto;
import de.ips.creactivities.chatbot.cms.dto.options.OptionsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CmsService implements ICmsService {

    public static final String DEFAULT_LANGUAGE_CODE = "en";

    public static final String PAGE_SIZE = "100";

    private List<String> languageCodes = Arrays.asList(DEFAULT_LANGUAGE_CODE, "de");

    private ChatbotProperties properties;

    private RestTemplate cmsRestTemplate;

    private Map<String, OptionsDto> options = new HashMap<>();

    private Map<String, List<Course>> courses = new HashMap<>();

    private Map<String, List<Level>> levels = new HashMap<>();

    private Map<String, List<Challenge>> challenges = new HashMap<>();

    @Autowired
    public CmsService(@Qualifier("cmsRestTemplate") RestTemplate cmsRestTemplate,
                      ChatbotProperties properties) {
        this.cmsRestTemplate = cmsRestTemplate;
        this.properties = properties;
    }

    private String getCmsBaseUrl() {

        String url = properties.getCmsBaseUrl();
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        return url;
    }

    @Scheduled(fixedRate = 60_000)
    private void refreshCmsData() {

        ContentMapper contentMapper = new ContentMapper();

        for (String languageCode : languageCodes) {

            List<ChallengeDto> challengeDtos = fetchChallenges(languageCode);
            List<Challenge> challengeList = contentMapper.convertChallengeDtosToModel(challengeDtos);
            challenges.put(languageCode, challengeList);

            List<LevelDto> levelDtos = fetchLevels(languageCode);
            List<Level> levelList = contentMapper.convertLevelDtosToModel(levelDtos);
            levels.put(languageCode, levelList);

            List<CourseDto> courseDtos = fetchCourses(languageCode);
            List<Course> courseList = contentMapper.convertCourseDtosToModel(courseDtos);
            courses.put(languageCode, courseList);

            OptionsDto optionsDto = fetchOptions(languageCode);
            if (optionsDto != null) {
                options.put(languageCode, optionsDto);
            }
        }

    }

    @Override
    public List<String> getAvailableLanguageCodes() {
        return new ArrayList<>(languageCodes);
    }

    @Override
    public List<String> getCourseIdentifiers(String languageCode) {
        return getCourseListByLanguage(languageCode).stream().map(Course::getIdentifier).toList();
    }

    @Override
    public List<String> getLevelsForCourse(String languageCode, String courseIdentifier) {

        if (isNullOrEmpty(courseIdentifier)) {
            return Collections.emptyList();
        }

        return new ArrayList<>(getReferenceCourse(languageCode, courseIdentifier).getLevelIdentifiers());
    }

    private Course getReferenceCourse(String languageCode, String courseIdentifier) {
        return getCourseListByLanguage(languageCode).stream().filter(c -> courseIdentifier.equals(c.getIdentifier())).findFirst().orElse(null);
    }

    @Override
    public List<String> getChallengesForLevel(String languageCode, String levelIdentifier) {

        if (isNullOrEmpty(levelIdentifier)) {
            return Collections.emptyList();
        }

        return new ArrayList<>(getReferenceLevel(languageCode, levelIdentifier).getChallengeIdentifiers());
    }

    private Level getReferenceLevel(String languageCode, String levelIdentifier) {
        return getLevelListByLanguage(languageCode).stream().filter((l) -> levelIdentifier.equals(l.getIdentifier())).findFirst().orElse(null);
    }

    @Override
    public Optional<Course> findCourseById(String languageCode, String courseIdentifier) {

        if (isNullOrEmpty(courseIdentifier)) {
            return Optional.empty();
        }

        return getCourseListByLanguage(languageCode).stream().filter(course -> courseIdentifier.equals(course.getIdentifier())).findFirst();
    }

    @Override
    public Optional<Level> findLevelById(String languageCode, String levelIdentifier) {

        if (isNullOrEmpty(levelIdentifier)) {
            return Optional.empty();
        }

        return getLevelListByLanguage(languageCode).stream().filter(course -> levelIdentifier.equals(course.getIdentifier())).findFirst();
    }

    @Override
    public Optional<Challenge> findChallengeById(String languageCode, String challengeIdentifier) {

        if (isNullOrEmpty(challengeIdentifier)) {
            return Optional.empty();
        }

        return getChallengeListByLanguage(languageCode).stream().filter(course -> challengeIdentifier.equals(course.getIdentifier())).findFirst();
    }

    @Override
    public List<String> getAdminIds() {
        List<String> ids = new ArrayList<>();

        for (AdminIdDto dto : getRefenceOptions().getAdmins()) {
            ids.add(dto.getIdentifier());
        }
        return ids;
    }

    @Override
    public String getAdminGroupChatId() {
        OptionsDto optionsDto = getRefenceOptions();
        return optionsDto.getAdminGroupChatId();
    }

    @Override
    public String getPrivacyPolicy(String languageCode) {
        OptionsDto optionsDto = getOptionsByLanguage(languageCode);

        String result = "Default language: " + languageCode + " PrivacyPolicy is empty in CMS!";
        if (optionsDto != null) {
            result = optionsDto.getPolicy();
        }
        return result;
    }

    @Override
    public String getPrivacyPolicyImage(String languageCode) {
        OptionsDto optionsDto = getOptionsByLanguage(languageCode);

        String result = null;
        if (optionsDto != null) {
            result = optionsDto.getPolicyImage();
        }
        return result;
    }

    @Override
    public String getRightsOfUse(String languageCode) {
        OptionsDto optionsDto = getOptionsByLanguage(languageCode);

        String result = "Default language: " + languageCode + " RightsOfUse is empty in CMS!";
        if (optionsDto != null) {
            result = optionsDto.getRightsOfUse();
        }
        return result;
    }

    @Override
    public String getRightsOfUseImage(String languageCode) {
        OptionsDto optionsDto = getOptionsByLanguage(languageCode);

        String result = null;
        if (optionsDto != null) {
            result = optionsDto.getRightsOfUseImage();
        }
        return result;
    }

    @Override
    public String getTermsOfService(String languageCode) {
        OptionsDto optionsDto = getOptionsByLanguage(languageCode);

        String result = "Default language: " + languageCode + " TermsOfService is empty in CMS!";
        if (optionsDto != null) {
            result = optionsDto.getTermsOfService();
        }
        return result;
    }

    @Override
    public String getTermsOfServiceImage(String languageCode) {
        OptionsDto optionsDto = getOptionsByLanguage(languageCode);

        String result = null;
        if (optionsDto != null) {
            result = optionsDto.getTermsOfServiceImage();
        }
        return result;
    }

    @Override
    public void uploadSolution(String userId, String challengeId, String solutionText, List<byte[]> pictures) {

        log.info("Posting solution for challenge:" + challengeId);

        List<String> mediaLinks = new ArrayList<>();
        String mediaUrl = getCmsBaseUrl() + "wp/v2/media";
        URI mediaUri = UriComponentsBuilder.fromHttpUrl(mediaUrl).build().toUri();

        for (int i = 0; i < pictures.size(); i++) {
            byte[] picture = pictures.get(i);
            String mediaName = "solution:" + challengeId + "_user:" + userId + "_media:" + i;

            HttpHeaders headers = getHeaders(MediaType.IMAGE_PNG);
            HttpEntity<?> httpEntity = new HttpEntity<>(picture, headers);
            headers.setBasicAuth(properties.getCmsUser(), properties.getCmsPassword());
            headers.setContentDisposition(ContentDisposition.attachment().filename(mediaName + ".png").build());

            ResponseEntity<CmsPostResponseDto> responseEntity = cmsRestTemplate.exchange(mediaUri, HttpMethod.POST, httpEntity, CmsPostResponseDto.class);
            mediaLinks.add(Objects.requireNonNull(responseEntity.getBody()).getGuid().getRaw());
            log.info(responseEntity.getBody().toString());
            log.info("Solution media to CMS Service for challenge:" + challengeId);
        }

        String postUrl = getCmsBaseUrl() + "wp/v2/posts";
        log.info(postUrl);
        URI uri = UriComponentsBuilder.fromHttpUrl(postUrl).build().toUri();
        try {
            CmsPostDto postDto = new CmsPostDto();
            postDto.setCommentStatus("closed");
            postDto.setTitle("solution:" + challengeId + "_user:" + userId);
            postDto.setStatus("publish");
            postDto.setFormat("standard");

            StringBuilder template = new StringBuilder();
            if (!isNullOrEmpty(solutionText)) {
                template.append("<p>").append(solutionText).append("</p>\n");
            }
            for (String mediaLink : mediaLinks) {
                template.append("<p><img src=\"").append(mediaLink).append("\"/></p>\n");
            }
            postDto.setContent(template.toString());

            HttpHeaders headers = getHeaders(MediaType.APPLICATION_JSON);
            HttpEntity<?> httpEntity = new HttpEntity<>(postDto, headers);
            ResponseEntity<String> responseEntity = cmsRestTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);

            log.info(responseEntity.getBody());
            log.info("Solution uploaded to CMS Service for challenge:" + challengeId);

        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            throw new IllegalStateException("Content can not be loaded!" + e.getMessage(), e);
        }


    }

    private List<CourseDto> fetchCourses(String languageCode) {

        log.info("CMS Service fetching new data for courses with language:" + languageCode);

        List<CourseDto> courseDtos = new ArrayList<>();
        int page = 1;
        int totalPages = 1;

        do {
            String courseUrl = getCmsBaseUrl() + "wp/v2/creactivities_course";
            URI uri = UriComponentsBuilder.fromHttpUrl(courseUrl)
                    .queryParam("_fields", "id,acf")
                    .queryParam("acf_format", "standard")
                    .queryParam("lang", languageCode)
                    .queryParam("per_page", PAGE_SIZE)
                    .queryParam("page", String.valueOf(page))
                    .build().toUri();
            try {
                HttpHeaders headers = getHeaders(MediaType.APPLICATION_JSON);
                HttpEntity<?> httpEntity = new HttpEntity<>(headers);
                ResponseEntity<CmsCourseDto[]> responseEntity = cmsRestTemplate.exchange(uri, HttpMethod.GET, httpEntity, CmsCourseDto[].class);
                courseDtos.addAll(Arrays.stream(Objects.requireNonNull(responseEntity.getBody())).map(cms -> {
                    CourseDto dto = cms.getCourse();
                    dto.setId(cms.getId());
                    return dto;
                }).toList());

                List<String> totalPageHeaders = responseEntity.getHeaders().getOrDefault("x-wp-totalpages", Collections.emptyList());
                totalPages = parseTotalPageHeaders(totalPageHeaders);
                page = page + 1;

            } catch (RuntimeException e) {
                log.error(e.getMessage(), e);
                throw new IllegalStateException("Content can not be loaded!" + e.getMessage(), e);
            }
        } while (page <= totalPages);

        log.info(courseDtos.size() + " Courses loaded from CMS Service for language:" + languageCode);

        return courseDtos;

    }

    private List<LevelDto> fetchLevels(String languageCode) {

        log.info("CMS Service fetching new data for levels with language:" + languageCode);

        List<LevelDto> levelDtos = new ArrayList<>();
        int page = 1;
        int totalPages = 1;

        do {
            String levelsUrl = getCmsBaseUrl() + "wp/v2/creactivities_level";
            URI uri = UriComponentsBuilder.fromHttpUrl(levelsUrl)
                    .queryParam("_fields", "id,acf")
                    .queryParam("acf_format", "standard")
                    .queryParam("lang", languageCode)
                    .queryParam("per_page", PAGE_SIZE)
                    .queryParam("page", String.valueOf(page))
                    .build().toUri();
            try {
                HttpHeaders headers = getHeaders(MediaType.APPLICATION_JSON);
                HttpEntity<?> httpEntity = new HttpEntity<>(headers);
                ResponseEntity<CmsLevelDto[]> responseEntity = cmsRestTemplate.exchange(uri, HttpMethod.GET, httpEntity, CmsLevelDto[].class);
                levelDtos.addAll(Arrays.stream(Objects.requireNonNull(responseEntity.getBody())).map(cms -> {
                    LevelDto dto = cms.getLevel();
                    dto.setId(cms.getId());
                    return dto;
                }).toList());

                List<String> totalPageHeaders = responseEntity.getHeaders().getOrDefault("x-wp-totalpages", Collections.emptyList());
                totalPages = parseTotalPageHeaders(totalPageHeaders);
                page = page + 1;

            } catch (RuntimeException e) {
                log.error(e.getMessage(), e);
                throw new IllegalStateException("Content can not be loaded!" + e.getMessage(), e);
            }
        } while (page <= totalPages);

        log.info(levelDtos.size() + " Levels loaded from CMS Service for language:" + languageCode);

        return levelDtos;

    }

    private List<ChallengeDto> fetchChallenges(String languageCode) {

        log.info("CMS Service fetching new data for challenges with language:" + languageCode);

        List<ChallengeDto> challengeDtos = new ArrayList<>();
        int page = 1;
        int totalPages = 1;

        do {
            String levelsUrl = getCmsBaseUrl() + "wp/v2/challenge";
            URI uri = UriComponentsBuilder.fromHttpUrl(levelsUrl)
                    .queryParam("_fields", "id,acf")
                    .queryParam("acf_format", "standard")
                    .queryParam("lang", languageCode)
                    .queryParam("per_page", PAGE_SIZE)
                    .queryParam("page", String.valueOf(page))
                    .build().toUri();
            try {
                HttpHeaders headers = getHeaders(MediaType.APPLICATION_JSON);
                HttpEntity<?> httpEntity = new HttpEntity<>(headers);
                ResponseEntity<CmsChallengeDto[]> responseEntity = cmsRestTemplate.exchange(uri, HttpMethod.GET, httpEntity, CmsChallengeDto[].class);

                challengeDtos.addAll(Arrays.stream(Objects.requireNonNull(responseEntity.getBody())).map(cms -> {
                    ChallengeDto dto = cms.getChallenge();
                    dto.setId(cms.getId());
                    return dto;
                }).toList());

                List<String> totalPageHeaders = responseEntity.getHeaders().getOrDefault("x-wp-totalpages", Collections.emptyList());
                totalPages = parseTotalPageHeaders(totalPageHeaders);
                page = page + 1;

            } catch (RuntimeException e) {
                log.error(e.getMessage(), e);
                throw new IllegalStateException("Content can not be loaded!" + e.getMessage(), e);
            }
        } while (page <= totalPages);

        log.info(challengeDtos.size() + " Challenges loaded from CMS Service for language:" + languageCode);

        return challengeDtos;
    }

    private int parseTotalPageHeaders(List<String> totalPageHeaders) {
        int result = 1;

        if (totalPageHeaders != null && totalPageHeaders.size() > 0) {
            String page = totalPageHeaders.get(0);
            try {
                result = Integer.parseInt(page);
            } catch (RuntimeException e) {
                log.error("Can not parse total pages" + totalPageHeaders);
            }
        }

        return result;
    }

    private OptionsDto fetchOptions(String languageCode) {

        // initialize RestTemplate and load content from CMS.
        String adminIdsUrl = getCmsBaseUrl() + "acf/v3/options/option";
        URI uri = UriComponentsBuilder.fromHttpUrl(adminIdsUrl)
                .queryParam("_fields", "acf")
                .queryParam("acf_format", "standard")
                .queryParam("lang", languageCode)
                .build().toUri();
        try {
            HttpHeaders headers = getHeaders(MediaType.APPLICATION_JSON);
            HttpEntity<?> httpEntity = new HttpEntity<>(headers);
            ResponseEntity<CmsOptionsDto> responseEntity = cmsRestTemplate.exchange(uri, HttpMethod.GET, httpEntity, CmsOptionsDto.class);
            OptionsDto optionsDto = Objects.requireNonNull(responseEntity.getBody()).getOption();
            log.info("Content Loaded from CMS Service for Admin Ids and Options");

            return optionsDto;

        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            throw new IllegalStateException("Content can not be loaded!" + e.getMessage(), e);
        }

    }

    private HttpHeaders getHeaders(MediaType contentType) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        if (!isNullOrEmpty(properties.getCmsUser()) && !isNullOrEmpty(properties.getCmsPassword())) {
            headers.setBasicAuth(properties.getCmsUser(), properties.getCmsPassword());
        }

        return headers;
    }

    private OptionsDto getRefenceOptions() {
        return getOptionsByLanguage(DEFAULT_LANGUAGE_CODE);
    }

    private OptionsDto getOptionsByLanguage(String languageCode) {
        OptionsDto result = options.get(languageCode);

        if (result == null) {
            result = options.get(DEFAULT_LANGUAGE_CODE);
        }

        return result;
    }

    private List<Course> getCourseListByLanguage(String languageCode) {
        List<Course> coursesByLanguage = courses.get(languageCode);

        if (coursesByLanguage == null || coursesByLanguage.isEmpty()) {
            coursesByLanguage = courses.get(DEFAULT_LANGUAGE_CODE);
        }

        return Optional.ofNullable(coursesByLanguage).orElse(Collections.emptyList());
    }

    private List<Level> getLevelListByLanguage(String languageCode) {
        List<Level> levelsByLanguage = levels.get(languageCode);

        if (levelsByLanguage == null || levelsByLanguage.isEmpty()) {
            levelsByLanguage = levels.get(DEFAULT_LANGUAGE_CODE);
        }

        return levelsByLanguage;
    }

    private List<Challenge> getChallengeListByLanguage(String languageCode) {
        List<Challenge> challengesByLanguage = challenges.get(languageCode);

        if (challengesByLanguage == null || challengesByLanguage.isEmpty()) {
            challengesByLanguage = challenges.get(DEFAULT_LANGUAGE_CODE);
        }

        return challengesByLanguage;
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

}
