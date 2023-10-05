package com.jaimemartz.playerbalancer.velocity.settings.props.features;

import com.jaimemartz.playerbalancer.velocity.settings.props.shared.SectionProps;
import lombok.Data;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;
import java.util.Map;

@ConfigSerializable
@Data
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
}
