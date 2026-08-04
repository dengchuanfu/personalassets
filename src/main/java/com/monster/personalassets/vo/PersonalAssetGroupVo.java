package com.monster.personalassets.vo;

import java.util.List;
import lombok.Builder;
import lombok.Value;
import run.halo.app.extension.MetadataOperator;
import run.halo.app.theme.finders.vo.ExtensionVoOperator;
import com.monster.personalassets.PersonalAssetGroup;


@Value
@Builder
public class PersonalAssetGroupVo implements ExtensionVoOperator {
    MetadataOperator metadata;
    
    PersonalAssetGroup.PersonalAssetGroupSpec spec;
    
    PersonalAssetGroup.PersonalAssetGroupStatus status;
    
    List<PersonalAssetVo> personalAssets;
    
    public static PersonalAssetGroupVo from(PersonalAssetGroup PersonalAssetGroup) {
        return PersonalAssetGroupVo.builder()
            .metadata(PersonalAssetGroup.getMetadata())
            .spec(PersonalAssetGroup.getSpec())
            .status(PersonalAssetGroup.getStatusOrDefault())
            .personalAssets(List.of())
            .build();
    }
}
