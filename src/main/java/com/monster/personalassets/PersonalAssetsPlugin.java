package com.monster.personalassets;


import org.springframework.stereotype.Component;
import run.halo.app.extension.Scheme;
import run.halo.app.extension.SchemeManager;
import run.halo.app.extension.index.IndexSpecs;
import run.halo.app.plugin.BasePlugin;
import run.halo.app.plugin.PluginContext;
import java.util.Optional;

@Component
public class PersonalAssetsPlugin extends BasePlugin {
    private final SchemeManager schemeManager;

    public PersonalAssetsPlugin(PluginContext pluginContext, SchemeManager schemeManager) {
        super(pluginContext);
        this.schemeManager = schemeManager;
    }

    @Override
    public void start() {
        schemeManager.register(PersonalAsset.class, indexSpecs -> {
            indexSpecs.add(IndexSpecs.<PersonalAsset, String>single("spec.groupName", String.class)
                .indexFunc(
                    personalAsset -> Optional.ofNullable(personalAsset.getSpec())
                        .map(PersonalAsset.PersonalAssetSpec::getGroupName)
                        .orElse(null)
                )
            );
            indexSpecs.add(IndexSpecs.<PersonalAsset, String>single("spec.displayName", String.class)
                .indexFunc(
                    personalAsset -> Optional.ofNullable(personalAsset.getSpec())
                        .map(PersonalAsset.PersonalAssetSpec::getDisplayName)
                        .orElse(null)
                )
            );
            indexSpecs.add(IndexSpecs.<PersonalAsset, Integer>single("spec.priority", Integer.class)
                .indexFunc(
                    personalAsset -> Optional.ofNullable(personalAsset.getSpec())
                        .map(PersonalAsset.PersonalAssetSpec::getPriority)
                        .orElse(0)
                )
            );
        });
        schemeManager.register(PersonalAssetGroup.class, indexSpecs -> {
            indexSpecs.add(IndexSpecs.<PersonalAssetGroup, Integer>single("spec.priority", Integer.class)
                .indexFunc(
                    group -> Optional.ofNullable(group.getSpec())
                        .map(PersonalAssetGroup.PersonalAssetGroupSpec::getPriority)
                        .orElse(0)
                )
            );
        });
    }

    @Override
    public void stop() {
        schemeManager.unregister(Scheme.buildFromType(PersonalAsset.class));
        schemeManager.unregister(Scheme.buildFromType(PersonalAssetGroup.class));
    }
}
