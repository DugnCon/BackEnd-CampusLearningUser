package com.javaweb.model.dto.Profile.ProfileInformation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SocialLinkDTO {

    @JsonProperty("facebook")
    private String facebook;

    @JsonProperty("instagram")
    private String instagram;

    @JsonProperty("x")
    private String x;

    @JsonProperty("reddit")
    private String reddit;

    @JsonProperty("linkedin")
    private String linkedin;

    @JsonProperty("github")
    private String github;

    @JsonProperty("youtube")
    private String youtube;

    @JsonProperty("tiktok")
    private String tiktok;

    @JsonProperty("website")
    private String website;

    // Constructors
    public SocialLinkDTO() {}

    public SocialLinkDTO(String facebook, String instagram, String x, String reddit,
                         String linkedin, String github, String youtube, String tiktok, String website) {
        this.facebook = facebook;
        this.instagram = instagram;
        this.x = x;
        this.reddit = reddit;
        this.linkedin = linkedin;
        this.github = github;
        this.youtube = youtube;
        this.tiktok = tiktok;
        this.website = website;
    }

    // Getters & Setters
    public String getFacebook() { return facebook; }
    public void setFacebook(String facebook) { this.facebook = facebook; }

    public String getInstagram() { return instagram; }
    public void setInstagram(String instagram) { this.instagram = instagram; }

    public String getX() { return x; }
    public void setX(String x) { this.x = x; }

    public String getReddit() { return reddit; }
    public void setReddit(String reddit) { this.reddit = reddit; }

    public String getLinkedin() { return linkedin; }
    public void setLinkedin(String linkedin) { this.linkedin = linkedin; }

    public String getGithub() { return github; }
    public void setGithub(String github) { this.github = github; }

    public String getYoutube() { return youtube; }
    public void setYoutube(String youtube) { this.youtube = youtube; }

    public String getTiktok() { return tiktok; }
    public void setTiktok(String tiktok) { this.tiktok = tiktok; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    // Helper method to convert to Map (nếu cần)
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        if (facebook != null) map.put("facebook", facebook);
        if (instagram != null) map.put("instagram", instagram);
        if (x != null) map.put("x", x);
        if (reddit != null) map.put("reddit", reddit);
        if (linkedin != null) map.put("linkedin", linkedin);
        if (github != null) map.put("github", github);
        if (youtube != null) map.put("youtube", youtube);
        if (tiktok != null) map.put("tiktok", tiktok);
        if (website != null) map.put("website", website);
        return map;
    }

    // Helper method to create from Map
    public static SocialLinkDTO fromMap(Map<String, String> map) {
        if (map == null) return null;

        SocialLinkDTO dto = new SocialLinkDTO();
        dto.setFacebook(map.get("facebook"));
        dto.setInstagram(map.get("instagram"));
        dto.setX(map.get("x"));
        dto.setReddit(map.get("reddit"));
        dto.setLinkedin(map.get("linkedin"));
        dto.setGithub(map.get("github"));
        dto.setYoutube(map.get("youtube"));
        dto.setTiktok(map.get("tiktok"));
        dto.setWebsite(map.get("website"));
        return dto;
    }
}
