package com.monster.personalassets;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;


@Data
@EqualsAndHashCode(callSuper = true)
@GVK(group = PersonalAssetResourceNames.MODEL_GROUP, version = "v1alpha1",
    kind = PersonalAssetResourceNames.GROUP_KIND, plural = PersonalAssetResourceNames.GROUPS_PLURAL,
    singular = PersonalAssetResourceNames.GROUP_SINGULAR)
public class PersonalAssetGroup extends AbstractExtension {

    @Schema(required = true)
    private PersonalAssetGroupSpec spec;

    @Schema
    private PersonalAssetGroupStatus status;

    @Data
    public static class PersonalAssetGroupSpec {
        @Schema(required = true)
        private String displayName;

        private String description;

        private Integer priority;
    }

    @JsonIgnore
    public PersonalAssetGroupStatus getStatusOrDefault() {
        if (this.status == null) {
            this.status = new PersonalAssetGroupStatus();
        }
        return this.status;
    }

    @Data
    public static class PersonalAssetGroupStatus {

        public Integer personalAssetCount;
    }
}
