package com.kunkunyu.personalassets.service.impl;

import static run.halo.app.extension.router.selector.SelectorUtil.labelAndFieldSelectorToListOptions;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.ListResult;
import run.halo.app.extension.PageRequestImpl;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.index.query.Queries;
import run.halo.app.extension.router.IListRequest.QueryListRequest;
import com.kunkunyu.personalassets.PersonalAsset;
import com.kunkunyu.personalassets.PersonalAssetGroup;
import com.kunkunyu.personalassets.service.PersonalAssetGroupService;

/**
 * Service implementation for {@link PersonalAsset}.
 *
 */
@Component
class PersonalAssetGroupServiceImpl implements PersonalAssetGroupService {

    private final ReactiveExtensionClient client;

    public PersonalAssetGroupServiceImpl(ReactiveExtensionClient client) {
        this.client = client;
    }

    @Override
    public Mono<ListResult<PersonalAssetGroup>> listPersonalAssetGroup(QueryListRequest query) {
        return this.client.listBy(
                PersonalAssetGroup.class,
                toListOptions(query),
                PageRequestImpl.of(query.getPage(), query.getSize())
            )
            .flatMap(listResult -> Flux.fromStream(listResult.get())
                .flatMap(this::populatePersonalAssets)
                .collectList()
                .map(groups -> new ListResult<>(
                    listResult.getPage(),
                    listResult.getSize(),
                    listResult.getTotal(),
                    groups
                ))
            );
    }

    @Override
    public Mono<PersonalAssetGroup> deletePersonalAssetGroup(String name) {
        return this.client.fetch(PersonalAssetGroup.class, name)
            .flatMap(this.client::delete)
            .flatMap(deleted -> {
                    var listOptions = ListOptions.builder()
                        .andQuery(Queries.equal("spec.groupName", name))
                        .build();
                    return this.client.listAll(PersonalAsset.class, listOptions, Sort.unsorted())
                        .flatMap(this.client::delete)
                        .then()
                        .thenReturn(deleted);
                }
            );
    }

    private Mono<PersonalAssetGroup> populatePersonalAssets(PersonalAssetGroup personalAssetGroup) {
        return fetchPersonalAssetCount(personalAssetGroup)
            .doOnNext(count -> personalAssetGroup.getStatusOrDefault().setPersonalAssetCount(count))
            .thenReturn(personalAssetGroup);
    }

    Mono<Integer> fetchPersonalAssetCount(PersonalAssetGroup personalAssetGroup) {
        Assert.notNull(personalAssetGroup, "The personalAssetGroup must not be null.");
        String name = personalAssetGroup.getMetadata().getName();
        return client.list(
                PersonalAsset.class,
                personalAsset -> !personalAsset.isDeleted() && personalAsset.getSpec().getGroupName().equals(name),
                null
            )
            .count()
            .defaultIfEmpty(0L)
            .map(Long::intValue);
    }

    ListOptions toListOptions(QueryListRequest query) {
        return labelAndFieldSelectorToListOptions(
            query.getLabelSelector(), query.getFieldSelector()
        );
    }
}
