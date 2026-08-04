package com.monster.personalassets.finders;

import com.monster.personalassets.PersonalAsset;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListResult;
import com.monster.personalassets.vo.PersonalAssetGroupVo;
import com.monster.personalassets.vo.PersonalAssetVo;


/**
 * A finder for {@link PersonalAsset}.
 *
 */
public interface PersonalAssetsFinder {
    
    /**
     * List all personalAsset.
     *
     * @return a flux of personalAsset vo
     */
    Flux<PersonalAssetVo> listAll();
    
    /**
     * List personalAsset by page.
     *
     * @param page page number
     * @param size page size
     * @return a mono of list result
     */
    Mono<ListResult<PersonalAssetVo>> list(Integer page, Integer size);
    
    /**
     * List personalAsset by page and group.
     *
     * @param page  page number
     * @param size  page size
     * @param group group name
     * @return a mono of list result
     */
    Mono<ListResult<PersonalAssetVo>> list(Integer page, Integer size, String group);
    
    /**
     * List personalAsset by group.
     *
     * @param group group name
     * @return a flux of personalAsset vo
     */
    Flux<PersonalAssetVo> listBy(String group);
    
    /**
     * List all groups.
     *
     * @return a flux of personalAsset group vo
     */
    Flux<PersonalAssetGroupVo> groupBy();
}
