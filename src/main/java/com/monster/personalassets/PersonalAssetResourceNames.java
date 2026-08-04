package com.monster.personalassets;

final class PersonalAssetResourceNames {

    static final String ASSET_SINGULAR = "personalasset";
    static final String MODEL_GROUP = "personalassets.ffbf.top";
    static final String MODEL_API_VERSION = MODEL_GROUP + "/v1alpha1";
    static final String CONSOLE_API_VERSION = "console.api." + MODEL_API_VERSION;
    static final String PUBLIC_API_VERSION = "api." + MODEL_API_VERSION;

    static final String ASSET_KIND = "PersonalAsset";
    static final String GROUP_KIND = "PersonalAssetGroup";
    static final String ASSETS_PLURAL = "personalassets";
    static final String GROUPS_PLURAL = ASSET_SINGULAR + "groups";
    static final String GROUP_SINGULAR = ASSET_SINGULAR + "group";

    private PersonalAssetResourceNames() {
    }
}
