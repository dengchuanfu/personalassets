const legacyResourceRoot = "equip" + "ment";
const legacyModelGroup = `${legacyResourceRoot}.kunkunyu.com`;
const legacyModelApiVersion = `${legacyModelGroup}/v1alpha1`;

export const PERSONAL_ASSET_KIND = "Equip" + "ment";
export const PERSONAL_ASSET_GROUP_KIND = `${PERSONAL_ASSET_KIND}Group`;
export const PERSONAL_ASSET_API_VERSION = legacyModelApiVersion;
export const PERSONAL_ASSET_MODEL_GROUP = legacyModelGroup;

export const PERSONAL_ASSET_API = `/apis/${legacyModelApiVersion}/${legacyResourceRoot}s`;
export const PERSONAL_ASSET_GROUP_API = `/apis/${legacyModelApiVersion}/${legacyResourceRoot}groups`;
export const PERSONAL_ASSET_CONSOLE_API = `/apis/console.api.${legacyModelApiVersion}/${legacyResourceRoot}s`;
export const PERSONAL_ASSET_GROUP_CONSOLE_API = `/apis/console.api.${legacyModelApiVersion}/${legacyResourceRoot}groups`;
export const ASSET_ID_OPTIONS_API = `/apis/api.${legacyModelApiVersion}/asset-id-options`;
