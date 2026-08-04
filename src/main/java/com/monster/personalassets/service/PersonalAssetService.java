package com.monster.personalassets.service;

import reactor.core.publisher.Mono;
import run.halo.app.extension.ListResult;
import com.monster.personalassets.PersonalAsset;
import com.monster.personalassets.PersonalAssetQuery;

/**
 * A service for {@link PersonalAsset}.
 *
 */
public interface PersonalAssetService {
    
    /**
     * List personalAsset.
     *
     * @param query query
     * @return a mono of list result
     */
    Mono<ListResult<PersonalAsset>> listPersonalAsset(PersonalAssetQuery query);
}
