package com.kunkunyu.personalassets.finders.impl;

import static org.springframework.data.domain.Sort.Order.asc;
import static org.springframework.data.domain.Sort.Order.desc;

import com.kunkunyu.personalassets.finders.PersonalAssetPublicQueryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.ListResult;
import run.halo.app.extension.PageRequestImpl;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.index.query.Queries;
import run.halo.app.theme.finders.Finder;
import com.kunkunyu.personalassets.finders.PersonalAssetsFinder;
import com.kunkunyu.personalassets.vo.PersonalAssetGroupVo;
import com.kunkunyu.personalassets.vo.PersonalAssetVo;

@Finder("personalassets")
public class PersonalAssetsFinderImpl implements PersonalAssetsFinder {
    private final ReactiveExtensionClient client;

    private final PersonalAssetPublicQueryService personalAssetPublicQueryService;

    public PersonalAssetsFinderImpl(ReactiveExtensionClient client,
        PersonalAssetPublicQueryService personalAssetPublicQueryService) {
        this.client = client;
        this.personalAssetPublicQueryService = personalAssetPublicQueryService;
    }

    @Override
    public Flux<PersonalAssetVo> listAll() {
        return personalAssetPublicQueryService.listPersonalAssets(ListOptions.builder().build(),
                PageRequestImpl.of(1, Integer.MAX_VALUE, defaultSort()))
            .flatMapIterable(ListResult::getItems);
    }

    @Override
    public Mono<ListResult<PersonalAssetVo>> list(Integer page, Integer size) {
        return list(page, size, null);
    }

    @Override
    public Mono<ListResult<PersonalAssetVo>> list(Integer page, Integer size, String group) {
        return pagePersonalAsset(page, size, group);
    }

    private Mono<ListResult<PersonalAssetVo>> pagePersonalAsset(Integer page, Integer size, String group) {
        var options = ListOptions.builder();
        if (StringUtils.isNotEmpty(group)) {
            options.andQuery(Queries.equal("spec.groupName", group));
        }

        return personalAssetPublicQueryService.listPersonalAssets(options.build(),
            PageRequestImpl.of(page, size, defaultSort()));
    }

    @Override
    public Flux<PersonalAssetVo> listBy(String groupName) {
        var options = ListOptions.builder()
            .andQuery(Queries.equal("spec.groupName", groupName))
            .build();
        return personalAssetPublicQueryService.listPersonalAssets(options,
                PageRequestImpl.of(1, Integer.MAX_VALUE, defaultSort()))
            .flatMapIterable(ListResult::getItems);
    }

    @Override
    public Flux<PersonalAssetGroupVo> groupBy() {

        return personalAssetPublicQueryService.listGroups(ListOptions.builder().build(),
                PageRequestImpl.of(1, Integer.MAX_VALUE, defaultSort()))
            .flatMapIterable(ListResult::getItems)
            .concatMap(group -> {
                String groupName = group.getMetadata().getName();
                return personalAssetPublicQueryService.listPersonalAssets(
                        ListOptions.builder()
                            .andQuery(Queries.equal("spec.groupName", groupName))
                            .build(),
                        PageRequestImpl.of(1, Integer.MAX_VALUE, defaultSort()))
                    .map(personalAssets -> {
                        var status = group.getStatus();
                        return PersonalAssetGroupVo.builder()
                            .metadata(group.getMetadata())
                            .spec(group.getSpec())
                            .status(status)
                            .personalAssets(personalAssets.getItems())
                            .build();
                    });
            });
    }

    static Sort defaultSort() {
        return Sort.by(
            asc("spec.priority"),
            desc("metadata.creationTimestamp"),
            asc("metadata.name")
        );
    }
}
