const assetSingular = "personalasset";
const modelGroup = "personalassets.ffbf.top";
const modelApiVersion = `${modelGroup}/v1alpha1`;

export const PERSONAL_ASSET_KIND = "PersonalAsset";
export const PERSONAL_ASSET_GROUP_KIND = "PersonalAssetGroup";
export const PERSONAL_ASSET_API_VERSION = modelApiVersion;
export const PERSONAL_ASSET_MODEL_GROUP = modelGroup;

export const PERSONAL_ASSET_API = `/apis/${modelApiVersion}/personalassets`;
export const PERSONAL_ASSET_GROUP_API = `/apis/${modelApiVersion}/${assetSingular}groups`;
export const PERSONAL_ASSET_CONSOLE_API = `/apis/console.api.${modelApiVersion}/personalassets`;
export const PERSONAL_ASSET_GROUP_CONSOLE_API = `/apis/console.api.${modelApiVersion}/${assetSingular}groups`;
export const ASSET_ID_OPTIONS_API = `/apis/api.${modelApiVersion}/asset-id-options`;
