package com.jaimemartz.playerbalancer.settings.props.features;

import com.jaimemartz.playerbalancer.settings.props.shared.SectionProps;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;
import java.util.Map;

@ConfigSerializable
public class BalancerProps {
    @Setting(value = "principal-section")
    private String principalSectionName;

    @Setting(value = "default-principal")
    private boolean defaultPrincipal;

    @Setting(value = "dummy-sections")
    private List<String> dummySectionNames;

    @Setting(value = "reiterative-sections")
    private List<String> reiterativeSectionNames;

    @Setting(value = "sections")
    private Map<String, SectionProps> sectionProps;

    @Setting(value = "show-players")
    private boolean showPlayers;

    public String getPrincipalSectionName() {
        return principalSectionName;
    }

    public void setPrincipalSectionName(String principalSectionName) {
        this.principalSectionName = principalSectionName;
    }

    public List<String> getDummySectionNames() {
        return dummySectionNames;
    }

    public void setDummySectionNames(List<String> dummySectionNames) {
        this.dummySectionNames = dummySectionNames;
    }

    public List<String> getReiterativeSectionNames() {
        return reiterativeSectionNames;
    }

    public void setReiterativeSectionNames(List<String> reiterativeSectionNames) {
        this.reiterativeSectionNames = reiterativeSectionNames;
    }

    public Map<String, SectionProps> getSectionProps() {
        return sectionProps;
    }

    public void setSectionProps(Map<String, SectionProps> sectionProps) {
        this.sectionProps = sectionProps;
    }

    public boolean isShowPlayers() {
        return showPlayers;
    }

    public void setShowPlayers(boolean showPlayers) {
        this.showPlayers = showPlayers;
    }

    public boolean isDefaultPrincipal() {
        return defaultPrincipal;
    }

    public void setDefaultPrincipal(boolean defaultPrincipal) {
        this.defaultPrincipal = defaultPrincipal;
    }

    @Override
    public String toString() {
        return "BalancerProps{" +
                "principalSectionName='" + principalSectionName + '\'' +
                ", defaultPrincipal=" + defaultPrincipal +
                ", dummySectionNames=" + dummySectionNames +
                ", reiterativeSectionNames=" + reiterativeSectionNames +
                ", sectionProps=" + sectionProps +
                ", showPlayers=" + showPlayers +
                '}';
    }
}
