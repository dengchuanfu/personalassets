package com.kunkunyu.personalassets.vo;

import lombok.Builder;
import lombok.Value;
import run.halo.app.extension.MetadataOperator;
import run.halo.app.theme.finders.vo.ExtensionVoOperator;
import com.kunkunyu.personalassets.AssetIdOptions;
import com.kunkunyu.personalassets.PersonalAsset;


@Value
@Builder
public class PersonalAssetVo implements ExtensionVoOperator {
    private static final String ASSET_ID_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    
    MetadataOperator metadata;
    
    String assetId;
    
    PersonalAsset.PersonalAssetSpec spec;
    
    public static PersonalAssetVo from(PersonalAsset personalAsset) {
        return from(personalAsset, AssetIdOptions.defaultOptions());
    }
    
    public static PersonalAssetVo from(PersonalAsset personalAsset, AssetIdOptions assetIdOptions) {
        String name = personalAsset.getMetadata().getName();
        String assetId = formatAssetId(
            assetIdOptions.prefix(),
            generateAssetId(name, assetIdOptions.length())
        );
        return PersonalAssetVo.builder()
            .metadata(personalAsset.getMetadata())
            .assetId(assetId)
            .spec(personalAsset.getSpec())
            .build();
    }
    
    private static String formatAssetId(String prefix, String assetId) {
        if (prefix == null || prefix.isBlank()) {
            return assetId;
        }
        return prefix + "-" + assetId;
    }
    
    private static String generateAssetId(String source, int length) {
        if (source != null && source.matches("^[A-Za-z0-9]{" + length + "}$")) {
            return ensureLetterAndDigit(new StringBuilder(source.toUpperCase()), source);
        }
        
        String value = source == null || source.isBlank() ? "personalAsset" : source;
        long hash = Integer.toUnsignedLong(value.hashCode());
        var builder = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            int index = (int) (hash % ASSET_ID_CHARS.length());
            builder.append(ASSET_ID_CHARS.charAt(index));
            hash = hash / ASSET_ID_CHARS.length();
            if (hash == 0) {
                hash = Integer.toUnsignedLong((value + i).hashCode());
            }
        }
        
        return ensureLetterAndDigit(builder, value);
    }
    
    private static String ensureLetterAndDigit(StringBuilder builder, String source) {
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (int i = 0; i < builder.length(); i++) {
            char current = builder.charAt(i);
            hasLetter = hasLetter || Character.isLetter(current);
            hasDigit = hasDigit || Character.isDigit(current);
        }
        
        int hash = Math.floorMod(source.hashCode(), 1_000_000);
        if (!hasLetter) {
            builder.setCharAt(0, (char) ('A' + hash % 26));
        }
        if (!hasDigit) {
            builder.setCharAt(builder.length() - 1, (char) ('0' + hash % 10));
        }
        return builder.toString();
    }
}
