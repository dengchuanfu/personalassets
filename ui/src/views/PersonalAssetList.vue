<script lang="ts" setup>
import LazyImage from "@/components/LazyImage.vue";
import PersonalAssetEditingModal from "@/components/PersonalAssetEditingModal.vue";
import type {PersonalAsset, PersonalAssetGroup, PersonalAssetGroupList, PersonalAssetList} from "@/types";
import { generateAssetId } from "@/utils/asset-id";
import {
  ASSET_ID_OPTIONS_API,
  PERSONAL_ASSET_API,
  PERSONAL_ASSET_API_VERSION,
  PERSONAL_ASSET_CONSOLE_API,
  PERSONAL_ASSET_GROUP_CONSOLE_API,
  PERSONAL_ASSET_KIND,
} from "@/utils/personalassets-api";
import { axiosInstance } from "@halo-dev/api-client";
import {
  Dialog,
  IconAddCircle,
  IconArrowLeft,
  IconArrowRight,
  IconCheckboxFill,
  Toast,
  VButton,
  VCard,
  VDropdown,
  VDropdownItem,
  VEmpty,
  VLoading,
  VPageHeader,
  VPagination,
  VSpace,
} from "@halo-dev/components";
import type { AttachmentLike } from "@halo-dev/ui-shared";
import { useQuery } from "@tanstack/vue-query";
import Fuse from "fuse.js";
import { computed, nextTick, ref, watch } from "vue";
import CarbonFinancialAssets from "~icons/carbon/financial-assets";
import GroupList from "../components/GroupList.vue";
import {VueDraggable} from "vue-draggable-plus";

const removeFileExtension = (filename: string) => {
  return filename.replace(/\.[^/.]+$/, "");
};

const selectedPersonalAsset = ref<PersonalAsset | undefined>();
const selectedPersonalAssetNames = ref<string[]>([]);
const selectedGroup = ref<string>();
const editingModal = ref(false);
const checkedAll = ref(false);
const groupListRef = ref();

const page = ref(1);
const size = ref(20);
const total = ref(0);
const keyword = ref("");
const personalAssets = ref<PersonalAsset[]>([]);
const assetIdLength = ref(6);

interface AssetIdOptions {
  prefix: string;
  length: number;
}

useQuery<AssetIdOptions>({
  queryKey: ["plugin:personalassets:asset-id-options"],
  queryFn: async () => {
    const { data } = await axiosInstance.get<AssetIdOptions>(ASSET_ID_OPTIONS_API);
    return data;
  },
  onSuccess(data) {
    assetIdLength.value = data.length || 6;
  },
  refetchOnWindowFocus: false,
});

const {
  isLoading,
  refetch,
} = useQuery<PersonalAsset[]>({
  queryKey: ["plugin:personalassets:data", page, size, keyword, selectedGroup],
  queryFn: async () => {
    if (!selectedGroup.value) {
      return [];
    }
    const { data } = await axiosInstance.get<PersonalAssetList>(PERSONAL_ASSET_CONSOLE_API, {
      params: {
        page: page.value,
        size: size.value,
        keyword: keyword.value,
        group: selectedGroup.value,
      },
    });
    total.value = data.total;
    return data.items
      .map((group) => {
        if (group.spec) {
          group.spec.priority = group.spec.priority || 0;
        }
        return group;
      })
      .sort((a, b) => {
        return (a.spec?.priority || 0) - (b.spec?.priority || 0);
      });
  },
  refetchInterval(data) {
    const hasDeletingGroup = data?.some((group) => !!group.metadata.deletionTimestamp);
    return hasDeletingGroup ? 1000 : false;
  },
  onSuccess(data) {
    personalAssets.value = data;
  },
  refetchOnWindowFocus: false,
});

const groups = ref<PersonalAssetGroup[]>([]);

const { refetch: groupRefetch, isLoading: groupIsLoading } = useQuery<PersonalAssetGroup[]>({
  queryKey: ["plugin:personalassets:groups"],
  queryFn: async () => {
    const { data } = await axiosInstance.get<PersonalAssetGroupList>(PERSONAL_ASSET_GROUP_CONSOLE_API);
    return data.items
      .map((group) => {
        if (group.spec) {
          group.spec.priority = group.spec.priority || 0;
        }
        return group;
      })
      .sort((a, b) => {
        return (a.spec?.priority || 0) - (b.spec?.priority || 0);
      });
  },
  refetchInterval(data) {
    const hasDeletingGroup = data?.some((group) => !!group.metadata.deletionTimestamp);
    return hasDeletingGroup ? 1000 : false;
  },
  onSuccess(data) {
    groups.value = data;
  },
  refetchOnWindowFocus: false,
});

const handleSelectPrevious = () => {
  if (!personalAssets.value) {
    return;
  }

  const currentIndex = personalAssets.value.findIndex((personalAsset) => personalAsset.metadata.name === selectedPersonalAsset.value?.metadata.name);

  if (currentIndex > 0) {
    selectedPersonalAsset.value = personalAssets.value[currentIndex - 1];
    return;
  }

  if (currentIndex <= 0) {
    selectedPersonalAsset.value = undefined;
  }
};

const handleSelectNext = () => {
  if (!personalAssets.value) {
    return;
  }

  if (!selectedPersonalAsset.value) {
    selectedPersonalAsset.value = personalAssets.value[0];
    return;
  }
  const currentIndex = personalAssets.value.findIndex((personalAsset) => personalAsset.metadata.name === selectedPersonalAsset.value?.metadata.name);
  if (currentIndex !== personalAssets.value.length - 1) {
    selectedPersonalAsset.value = personalAssets.value[currentIndex + 1];
  }
};

const handleOpenEditingModal = (personalAsset?: PersonalAsset) => {
  selectedPersonalAsset.value = personalAsset;
  editingModal.value = true;
};

const handleDeleteInBatch = () => {
  Dialog.warning({
    title: "是否确认删除所选的资产？",
    description: "删除之后将无法恢复。",
    confirmType: "danger",
    onConfirm: async () => {
      try {
        const promises = selectedPersonalAssetNames.value.map((name) => {
          return axiosInstance.delete(`${PERSONAL_ASSET_API}/${name}`);
        });
        await Promise.all(promises);
        checkedAll.value = false;
      } catch (e) {
        console.error(e);
      } finally {
        pageRefetch();
      }
    },
  });
};

async function handleMoveInBatch(group: PersonalAssetGroup) {
  const personalAssetsToUpdate = selectedPersonalAssetNames.value
    ?.map((name) => {
      return personalAssets.value?.find((personalAsset) => personalAsset.metadata.name === name);
    })
    .filter(Boolean) as PersonalAsset[];

  const requests = personalAssetsToUpdate.map((personalAsset) => {
    const patchDoc = [
      {
        op: "add",
        path: "/spec/groupName",
        value: group.metadata.name || "",
      },
    ];
    return axiosInstance.patch(`${PERSONAL_ASSET_API}/${personalAsset.metadata.name}`, JSON.stringify(patchDoc), {
      headers: {
        'Content-Type': 'application/json-patch+json'
      }
    });
  });

  if (requests) await Promise.all(requests);


  await pageRefetch();
  checkedAll.value = false;
  
  Toast.success("移动成功");
}

const handleCheckAllChange = (e: Event) => {
  const { checked } = e.target as HTMLInputElement;
  checkedAll.value = checked;
  if (checkedAll.value) {
    selectedPersonalAssetNames.value =
      personalAssets.value?.map((personalAsset) => {
        return personalAsset.metadata.name;
      }) || [];
  } else {
    selectedPersonalAssetNames.value.length = 0;
  }
};

const isChecked = (personalAsset: PersonalAsset) => {
  return (
    personalAsset.metadata.name === selectedPersonalAsset.value?.metadata.name ||
    selectedPersonalAssetNames.value
      .map((name) => name)
      .includes(personalAsset.metadata.name)
  );
};

watch(selectedPersonalAssetNames, (newValue) => {
  checkedAll.value = newValue.length === personalAssets.value?.length;
});

// search
let fuse: Fuse<PersonalAsset> | undefined = undefined;

watch(
  () => personalAssets.value,
  () => {
    if (!personalAssets.value) {
      return;
    }

    fuse = new Fuse(personalAssets.value, {
      keys: ["spec.displayName", "metadata.name", "spec.description", "spec.url"],
      useExtendedSearch: true,
    });
  }
);

const searchResults = computed({
  get() {
    if (!fuse || !keyword.value) {
      return personalAssets.value || [];
    }

    return fuse?.search(keyword.value).map((item) => item.item);
  },
  set(value) {
    personalAssets.value = value;
  },
});

// create by attachments
const attachmentModal = ref(false);

const onAttachmentsSelect = async (attachments: AttachmentLike[]) => {
  const personalAssets: {
    cover?: string;
    displayName?: string;
    type?: string;
  }[] = attachments
    .map((attachment) => {
      const post = {
        groupName: selectedGroup.value || "",
      };

      if (typeof attachment === "string") {
        return {
          ...post,
          cover: attachment,
        };
      }
      if ("url" in attachment) {
        return {
          ...post,
          cover: attachment.url,
        };
      }
      if ("spec" in attachment) {
        return {
          ...post,
          cover: attachment.status?.permalink,
          displayName: attachment.spec.displayName ? removeFileExtension(attachment.spec.displayName) : undefined,
          type: attachment.spec.mediaType,
        };
      }
    })
    .filter(Boolean) as {
    cover?: string;
    displayName?: string;
    type?: string;
  }[];

  for (const personalAsset of personalAssets) {
    const type = personalAsset.type;
    if (!type) {
      Toast.error("只支持选择图片");
      nextTick(() => {
        attachmentModal.value = true;
      });

      return;
    }
    const fileType = type.split("/")[0];
    if (fileType !== "image") {
      Toast.error("只支持选择图片");
      nextTick(() => {
        attachmentModal.value = true;
      });
      return;
    }
  }

  const createRequests = personalAssets.map((personalAsset) => {
    return axiosInstance.post<PersonalAsset>(PERSONAL_ASSET_API, {
      metadata: {
        name: generateAssetId(assetIdLength.value),
      },
      spec: personalAsset,
      kind: PERSONAL_ASSET_KIND,
      apiVersion: PERSONAL_ASSET_API_VERSION,
    });
  });

  await Promise.all(createRequests);

  Toast.success(`新建成功，一共创建了 ${personalAssets.length} 个资产。`);
  pageRefetch();
};

const handleSaveInBatch = async () => {
  try {
    const promises = personalAssets.value?.map((personalAsset: PersonalAsset, index) => {
      if (personalAsset.spec) {
        personalAsset.spec.priority = index;
      }
      return axiosInstance.put(`${PERSONAL_ASSET_API}/${personalAsset.metadata.name}`, personalAsset);
    });
    if (promises) {
      await Promise.all(promises);
    }
  } catch (e) {
    console.error(e);
  } finally {
    refetch();
  }
};

const groupSelectHandle = (group?: string) => {
  selectedGroup.value = group;
};

const pageRefetch = async () => {
  await groupListRef.value.refetch();
  await refetch();
  selectedPersonalAssetNames.value.length = 0;
};

const onEditingModalClose = () => {
  editingModal.value = false;
  refetch();
};

</script>
<template>
  <PersonalAssetEditingModal
    v-if="editingModal"
    :personal-asset="selectedPersonalAsset"
    :group="selectedGroup"
    :asset-id-length="assetIdLength"
    @close="onEditingModalClose"
    @saved="pageRefetch"
  >
    <template #append-actions>
      <span @click="handleSelectPrevious">
        <IconArrowLeft />
      </span>
      <span @click="handleSelectNext">
        <IconArrowRight />
      </span>
    </template>
  </PersonalAssetEditingModal>
  <AttachmentSelectorModal v-model:visible="attachmentModal" :accepts="['image/*']" @select="onAttachmentsSelect" />
  <VPageHeader title="资产">
    <template #icon>
      <CarbonFinancialAssets />
    </template>
  </VPageHeader>
  <div class=":uno: p-4">
    <div class=":uno: flex flex-col gap-2 lg:flex-row">
      <div class=":uno: w-full flex-none lg:w-96">
        <GroupList ref="groupListRef" @select="groupSelectHandle" />
      </div>
      <div class=":uno: min-w-0 flex-1 shrink">
        <VCard>
          <template #header>
            <div class=":uno: block w-full bg-gray-50 px-4 py-3">
              <div class=":uno: relative flex flex-col items-start sm:flex-row sm:items-center">
                <div class=":uno: mr-4 hidden items-center sm:flex">
                  <input v-model="checkedAll" type="checkbox" @change="handleCheckAllChange" />
                </div>
                <div class=":uno: w-full flex flex-1 sm:w-auto">
                  <SearchInput v-if="!selectedPersonalAssetNames.length" v-model="keyword" />
                  <VSpace v-else>
                    <VButton type="danger" @click="handleDeleteInBatch"> 删除 </VButton>
                    <VDropdown>
                      <VButton type="default">移动</VButton>
                      <template #popper>
                        <template v-for="group in groups" :key="group.metadata.name">
                          <VDropdownItem
                            v-if="group.metadata.name !== selectedGroup"
                            v-close-popper.all
                            @click="handleMoveInBatch(group)"
                          >
                            {{ group.spec?.displayName }}
                          </VDropdownItem>
                        </template>
                      </template>
                    </VDropdown>
                  </VSpace>
                </div>
                <div v-if="selectedGroup" v-permission="['plugin:personalassets:manage']" class=":uno: mt-4 flex sm:mt-0">
                  <VDropdown>
                    <VButton size="xs"> 新增 </VButton>
                    <template #popper>
                      <VDropdownItem @click="handleOpenEditingModal()"> 新增 </VDropdownItem>
                      <VDropdownItem @click="attachmentModal = true"> 从附件库选择 </VDropdownItem>
                    </template>
                  </VDropdown>
                </div>
              </div>
            </div>
          </template>
          <VLoading v-if="isLoading" />
          <Transition v-else-if="!selectedGroup" appear name="fade">
            <VEmpty message="请选择或新建分组" title="未选择分组"></VEmpty>
          </Transition>
          <Transition v-else-if="!searchResults.length" appear name="fade">
            <VEmpty message="你可以尝试刷新或者新建资产" title="当前没有资产">
              <template #actions>
                <VSpace>
                  <VButton @click="refetch"> 刷新</VButton>
                  <VButton v-permission="['plugin:personalassets:manage']" type="primary" @click="handleOpenEditingModal()">
                    <template #icon>
                      <IconAddCircle class=":uno: size-full" />
                    </template>
                    新增资产
                  </VButton>
                </VSpace>
              </template>
            </VEmpty>
          </Transition>
          <Transition v-else appear name="fade">
            <VueDraggable
              v-model="personalAssets"
              class=":uno: grid grid-cols-1 mt-2 gap-x-2 gap-y-3 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5"
              group="personalassets"
              handle=".drag-element"
              item-key="metadata.name"
              tag="div"
              role="list"
              @update="handleSaveInBatch"
            >
              <VCard
                v-for="personalAsset in personalAssets"
                :key="personalAsset.metadata.name"
                :body-class="[':uno: !p-0']"
                :class="{
                  ':uno: ring-primary ring-1': isChecked(personalAsset),
                  ':uno: ring-1 ring-red-600': personalAsset.metadata.deletionTimestamp,
                }"
                class=":uno: hover:shadow drag-element "
                @click="handleOpenEditingModal(personalAsset)"
              >
                <div class=":uno: group relative bg-white">
                  <div class=":uno: block aspect-16/9 size-full cursor-pointer overflow-hidden bg-gray-100 relative">
                    <LazyImage
                      :key="personalAsset.metadata.name"
                      :alt="personalAsset.spec.displayName"
                      :src="personalAsset.spec.cover || ''"
                      classes="size-full pointer-events-none group-hover:opacity-75"
                    >
                      <template #loading>
                        <div class=":uno: h-full flex justify-center">
                          <VLoading></VLoading>
                        </div>
                      </template>
                      <template #error>
                        <div class=":uno: h-full flex items-center justify-center object-cover">
                          <span class=":uno: text-xs text-red-400"> 加载异常 </span>
                        </div>
                      </template>
                    </LazyImage>
                  </div>

                  <p
                    v-tooltip="personalAsset.spec.displayName"
                    class=":uno: block cursor-pointer whitespace-normal break-all px-2 py-1 text-center text-xs text-gray-700 font-medium leading-4"
                  >
                    {{ personalAsset.spec.displayName }}
                  </p>

                  <div v-if="personalAsset.metadata.deletionTimestamp" class=":uno: absolute top-1 right-1 text-xs text-red-300">
                    删除中...
                  </div>

                  <div
                    v-if="!personalAsset.metadata.deletionTimestamp"
                    v-permission="['plugin:personalassets:manage']"
                    :class="{ ':uno: !flex': selectedPersonalAssetNames.includes(personalAsset.metadata.name) }"
                    class=":uno: absolute left-0 top-0 hidden h-1/3 w-full cursor-pointer justify-end from-gray-300 to-transparent bg-gradient-to-b ease-in-out group-hover:flex"
                    @click.stop="selectedPersonalAssetNames.includes(personalAsset.metadata.name) ? selectedPersonalAssetNames.splice(selectedPersonalAssetNames.indexOf(personalAsset.metadata.name), 1) : selectedPersonalAssetNames.push(personalAsset.metadata.name)"
                  >
                    <IconCheckboxFill
                      :class="{
                        ':uno: !text-primary': selectedPersonalAssetNames.includes(personalAsset.metadata.name),
                      }"
                      class=":uno: hover:text-primary mr-1 mt-1 h-6 w-6 cursor-pointer text-white transition-all"
                    />
                  </div>
                </div>
              </VCard>
            </VueDraggable>
          </Transition>

          <template #footer>
            <VPagination v-model:page="page" v-model:size="size" :total="total" :size-options="[20, 30, 50, 100]" />
          </template>
        </VCard>
      </div>
    </div>
  </div>
</template>
