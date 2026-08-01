package com.kunkunyu.personalassets.service.impl;

import static run.halo.app.extension.router.selector.SelectorUtil.labelAndFieldSelectorToListOptions;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.ListResult;
import run.halo.app.extension.PageRequestImpl;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.index.query.Queries;
import com.kunkunyu.personalassets.PersonalAsset;
import com.kunkunyu.personalassets.PersonalAssetQuery;
import com.kunkunyu.personalassets.service.PersonalAssetService;

/**
 * Service implementation for {@link PersonalAsset}.
 *
 */
@Component
class PersonalAssetServiceImpl implements PersonalAssetService {

    private final ReactiveExtensionClient client;

    public PersonalAssetServiceImpl(ReactiveExtensionClient client) {
        this.client = client;
    }

    @Override
    public Mono<ListResult<PersonalAsset>> listPersonalAsset(PersonalAssetQuery query) {
        return this.client.listBy(
            PersonalAsset.class,
            toListOptions(query),
            PageRequestImpl.of(query.getPage(), query.getSize(), query.getSort())
        );
    }

    ListOptions toListOptions(PersonalAssetQuery query) {
        var builder = ListOptions.builder(labelAndFieldSelectorToListOptions(
            query.getLabelSelector(), query.getFieldSelector())
        );

        if (StringUtils.isNotBlank(query.getKeyword())) {
            builder.andQuery(Queries.contains("spec.displayName", query.getKeyword()));
        }
        if (StringUtils.isNotBlank(query.getGroup())) {
            builder.andQuery(Queries.equal("spec.groupName", query.getGroup()));
        }
        return builder.build();
    }
}
