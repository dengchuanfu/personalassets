package com.kunkunyu.personalassets;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

/**
 * @author ryanwang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@GVK(group = LegacyResourceNames.MODEL_GROUP, version = "v1alpha1",
    kind = LegacyResourceNames.ASSET_KIND, plural = LegacyResourceNames.ASSETS_PLURAL,
    singular = LegacyResourceNames.ASSET_SINGULAR)
public class PersonalAsset extends AbstractExtension {

    private PersonalAssetSpec spec;

    @Data
    public static class PersonalAssetSpec {
        @Schema(requiredMode = REQUIRED)
        private String displayName;

        private String specification;

        private String description;

        @Schema(requiredMode = REQUIRED)
        private String cover;

        private String url;

        private Integer priority;

        @Schema(requiredMode = REQUIRED, pattern = "^\\S+$")
        private String groupName;
    }

    @JsonIgnore
    public boolean isDeleted() {
        return Objects.equals(true,
            getMetadata().getDeletionTimestamp() != null
        );
    }

}
