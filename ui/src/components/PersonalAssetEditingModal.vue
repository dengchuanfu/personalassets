<script lang="ts" setup>
import type { PersonalAsset } from "@/types";
import { generateAssetId } from "@/utils/asset-id";
import {
  PERSONAL_ASSET_API,
  PERSONAL_ASSET_API_VERSION,
  PERSONAL_ASSET_KIND,
  PERSONAL_ASSET_MODEL_GROUP,
} from "@/utils/personalassets-api";
import { axiosInstance } from "@halo-dev/api-client";
import { VSpace, VButton, VModal } from "@halo-dev/components";
import { cloneDeep } from "lodash-es";
import {computed, nextTick, onMounted, ref, useTemplateRef} from "vue";

const props = withDefaults(
  defineProps<{
    personalAsset?: PersonalAsset;
    group?: string;
    assetIdLength?: number;
  }>(),
  {
    personalAsset: undefined,
    group: undefined,
    assetIdLength: 6,
  }
);

const emit = defineEmits<{
  (event: "close"): void;
  (event: "saved", personalAsset: PersonalAsset): void;
}>();

const createInitialFormState = (): PersonalAsset => ({
  metadata: {
    name: generateAssetId(props.assetIdLength),
  },
  spec: {
    displayName: "",
    cover: "",
    groupName: props.group || "",
  },
  kind: PERSONAL_ASSET_KIND,
  apiVersion: PERSONAL_ASSET_API_VERSION,
} as PersonalAsset);

const formState = ref<PersonalAsset>(createInitialFormState());
const isSubmitting = ref(false);
const modal = useTemplateRef<InstanceType<typeof VModal> | null>("modal");

const isUpdateMode = computed(() => {
  return !!formState.value.metadata.creationTimestamp;
});

const modalTitle = computed(() => {
  return isUpdateMode.value ? "编辑资产" : "添加资产";
});

onMounted(() => {
  if (props.personalAsset) {
    formState.value = cloneDeep(props.personalAsset);
  }
});

const annotationsFormRef = ref();

const handleSavePersonalAsset = async () => {
  annotationsFormRef.value?.handleSubmit();
  await nextTick();
  const { customAnnotations, annotations, customFormInvalid, specFormInvalid } = annotationsFormRef.value || {};
  if (customFormInvalid || specFormInvalid) {
    return;
  }
  formState.value.metadata.annotations = {
    ...annotations,
    ...customAnnotations,
  };
  try {
    isSubmitting.value = true;
    if (isUpdateMode.value) {
      await axiosInstance.put<PersonalAsset>(
        `${PERSONAL_ASSET_API}/${formState.value.metadata.name}`,
        formState.value
      );
    } else {
      if (props.group) {
        formState.value.spec.groupName = props.group;
      }
      formState.value.metadata.name = formState.value.metadata.name || generateAssetId(props.assetIdLength);
      delete formState.value.metadata.generateName;
      const { data } = await axiosInstance.post<PersonalAsset>(PERSONAL_ASSET_API, formState.value);
      emit("saved", data);
    }
    modal.value?.close();
  } catch (e) {
    console.error(e);
  } finally {
    isSubmitting.value = false;
  }
};
</script>
<template>
  <VModal ref="modal" :title="modalTitle" :width="650" @close="emit('close')">
    <template #actions>
      <slot name="append-actions" />
    </template>

    <FormKit
      id="personalassets-form"
      v-model="formState.spec"
      name="personalassets-form"
      :actions="false"
      :config="{ validationVisibility: 'submit' }"
      type="form"
      @submit="handleSavePersonalAsset"
    >
      <div class=":uno: md:grid md:grid-cols-4 md:gap-6">
        <div class=":uno: md:col-span-1">
          <div class=":uno: sticky top-0">
            <span class=":uno: text-base text-gray-900 font-medium"> 常规 </span>
          </div>
        </div>
        <div class=":uno: mt-5 md:col-span-3 md:mt-0 divide-y divide-gray-100">
          <FormKit name="displayName" label="名称" type="text" validation="required"></FormKit>
          <FormKit name="cover" label="封面" type="attachment" :accepts="['image/*']"></FormKit>
          <FormKit name="url" label="价格" type="text"></FormKit>
          <FormKit name="specification" label="资产规格" type="text" ></FormKit>
          <FormKit name="description" label="描述" type="textarea"></FormKit>
        </div>
      </div>
    </FormKit>
    <div class=":uno: py-5">
      <div class=":uno: border-t border-gray-200"></div>
    </div>
    <div class=":uno: md:grid md:grid-cols-4 md:gap-6">
      <div class=":uno: md:col-span-1">
        <div class=":uno: sticky top-0">
          <span class=":uno: text-base text-gray-900 font-medium"> 元数据 </span>
        </div>
      </div>
      <div class=":uno: mt-5 md:col-span-3 md:mt-0 divide-y divide-gray-100">
        <AnnotationsForm
          :key="formState.metadata.name"
          ref="annotationsFormRef"
          :value="formState.metadata.annotations"
          :kind="PERSONAL_ASSET_KIND"
          :group="PERSONAL_ASSET_MODEL_GROUP"
        />
      </div>
    </div>
    <template #footer>
      <VSpace>
        <VButton :loading="isSubmitting" type="secondary"
                 @click="$formkit.submit('personalassets-form')"> 保存 </VButton>
        <VButton @click="modal?.close()">取消</VButton>
      </VSpace>
    </template>
  </VModal>
</template>
