package com.monster.personalassets;

import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.ReactiveSettingFetcher;

public record AssetIdOptions(String prefix, int length) {
    private static final String DEFAULT_PREFIX = "";
    private static final int DEFAULT_LENGTH = 6;
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 32;

    public static AssetIdOptions defaultOptions() {
        return new AssetIdOptions(DEFAULT_PREFIX, DEFAULT_LENGTH);
    }

    public static Mono<AssetIdOptions> fetch(ReactiveSettingFetcher settingFetcher) {
        return settingFetcher.get("base")
            .map(setting -> new AssetIdOptions(
                sanitizePrefix(setting.get("assetIdPrefix").asText(DEFAULT_PREFIX)),
                normalizeLength(setting.get("assetIdLength").asInt(DEFAULT_LENGTH))
            ))
            .defaultIfEmpty(defaultOptions());
    }

    public static int normalizeLength(int length) {
        return Math.min(Math.max(length, MIN_LENGTH), MAX_LENGTH);
    }

    private static String sanitizePrefix(String prefix) {
        return StringUtils.defaultString(prefix).trim();
    }
}
