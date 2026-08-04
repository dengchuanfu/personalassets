package com.monster.personalassets.finders;

import com.monster.personalassets.vo.PersonalAssetGroupVo;
import com.monster.personalassets.vo.PersonalAssetVo;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.ListResult;
import run.halo.app.extension.PageRequest;

public interface PersonalAssetPublicQueryService {

    /**
     * List personalAssets with filters and pagination.
     *
     * @param options list options
     * @param page    page request
     * @return a mono of list result
     */
    Mono<ListResult<PersonalAssetVo>> listPersonalAssets(ListOptions options, PageRequest page);

    /**
     * List photo groups without inline photos.
     *
     * @param options list options
     * @param page    page request
     * @return a mono of list result
     */
    Mono<ListResult<PersonalAssetGroupVo>> listGroups(ListOptions options, PageRequest page);

}
