package com.kunkunyu.personalassets.finders.impl;

import com.kunkunyu.personalassets.PersonalAsset;
import com.kunkunyu.personalassets.PersonalAssetGroup;
import com.kunkunyu.personalassets.AssetIdOptions;
import com.kunkunyu.personalassets.finders.PersonalAssetPublicQueryService;
import com.kunkunyu.personalassets.vo.PersonalAssetGroupVo;
import com.kunkunyu.personalassets.vo.PersonalAssetVo;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.ListResult;
import run.halo.app.extension.PageRequest;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.plugin.ReactiveSettingFetcher;

import java.util.Comparator;

@Component
public class PersonalAssetPublicQueryServiceImpl implements PersonalAssetPublicQueryService {

    private final ReactiveExtensionClient client;

    private final ReactiveSettingFetcher settingFetcher;

    public PersonalAssetPublicQueryServiceImpl(ReactiveExtensionClient client,
        ReactiveSettingFetcher settingFetcher) {
        this.client = client;
        this.settingFetcher = settingFetcher;
    }

    @Override
    public Mono<ListResult<PersonalAssetVo>> listPersonalAssets(ListOptions options, PageRequest page) {

        return AssetIdOptions.fetch(settingFetcher)
            .flatMap(assetIdOptions -> client.listBy(PersonalAsset.class, options, page)
                .flatMap(result -> Flux.fromIterable(result.getItems())
                    .map(personalAsset -> PersonalAssetVo.from(personalAsset, assetIdOptions))
                    .collectList()
                    .map(items -> new ListResult<>(
                        result.getPage(), result.getSize(), result.getTotal(), items))));
    }

    @Override
    public Mono<ListResult<PersonalAssetGroupVo>> listGroups(ListOptions options, PageRequest page) {
        return client.listAll(PersonalAssetGroup.class, options, Sort.unsorted())
            .sort(groupComparator())
            .collectList()
            .flatMap(groups -> {
                int total = groups.size();
                var pageItems = ListResult.subList(groups, page.getPageNumber(), page.getPageSize());
                return Flux.fromIterable(pageItems)
                    .concatMap(this::toGroupVo)
                    .collectList()
                    .map(items -> new ListResult<>(page.getPageNumber(), page.getPageSize(), total, items));
            });
    }

    private Mono<PersonalAssetGroupVo> toGroupVo(PersonalAssetGroup group) {
        return fetchPersonalAssetCount(group)
            .map(count -> {
                var status = group.getStatusOrDefault();
                status.setPersonalAssetCount(count);
                return PersonalAssetGroupVo.builder()
                    .metadata(group.getMetadata())
                    .spec(group.getSpec())
                    .status(status)
                    .personalAssets(null)
                    .build();
            });
    }

    private Mono<Integer> fetchPersonalAssetCount(PersonalAssetGroup group) {
        String name = group.getMetadata().getName();
        return client.list(
                PersonalAsset.class,
                personalAsset -> !personalAsset.isDeleted()
                    && personalAsset.getSpec() != null
                    && name.equals(personalAsset.getSpec().getGroupName()),
                null
            )
            .count()
            .defaultIfEmpty(0L)
            .map(Long::intValue);
    }

    static Comparator<PersonalAssetGroup> groupComparator() {
        return (g1, g2) -> {
            var p1 = g1.getSpec() != null && g1.getSpec().getPriority() != null
                ? g1.getSpec().getPriority() : 0;
            var p2 = g2.getSpec() != null && g2.getSpec().getPriority() != null
                ? g2.getSpec().getPriority() : 0;
            int priorityCompare = Integer.compare(p1, p2);
            if (priorityCompare != 0) {
                return priorityCompare;
            }
            var t1 = g1.getMetadata() != null ? g1.getMetadata().getCreationTimestamp() : null;
            var t2 = g2.getMetadata() != null ? g2.getMetadata().getCreationTimestamp() : null;
            if (t1 == null && t2 == null) {
                return 0;
            }
            if (t1 == null) {
                return 1;
            }
            if (t2 == null) {
                return -1;
            }
            int timeCompare = t2.compareTo(t1);
            if (timeCompare != 0) {
                return timeCompare;
            }
            var n1 = g1.getMetadata() != null ? g1.getMetadata().getName() : "";
            var n2 = g2.getMetadata() != null ? g2.getMetadata().getName() : "";
            return n1.compareTo(n2);
        };
    }
}
