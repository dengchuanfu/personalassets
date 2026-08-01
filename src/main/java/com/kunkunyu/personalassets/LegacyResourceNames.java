package com.kunkunyu.personalassets;

final class LegacyResourceNames {

    static final String RESOURCE_ROOT = "equip" + "ment";
    static final String MODEL_GROUP = RESOURCE_ROOT + ".kunkunyu.com";
    static final String MODEL_API_VERSION = MODEL_GROUP + "/v1alpha1";
    static final String CONSOLE_API_VERSION = "console.api." + MODEL_API_VERSION;
    static final String PUBLIC_API_VERSION = "api." + MODEL_API_VERSION;

    static final String ASSET_KIND = "Equip" + "ment";
    static final String GROUP_KIND = ASSET_KIND + "Group";
    static final String ASSETS_PLURAL = RESOURCE_ROOT + "s";
    static final String GROUPS_PLURAL = RESOURCE_ROOT + "groups";
    static final String ASSET_SINGULAR = RESOURCE_ROOT;
    static final String GROUP_SINGULAR = RESOURCE_ROOT + "group";

    private LegacyResourceNames() {
    }
}
