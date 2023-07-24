package org.example.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@ConfigurationProperties(prefix = "application.filesettings")
public class RelevantType {

    private Set<String> relevanttypes = new HashSet<>();

    public Set<String> getRelevanttypes() {
        return relevanttypes;
    }

    public void setRelevanttypes(Set<String> relevanttypes) {
        this.relevanttypes = relevanttypes;
    }
}
