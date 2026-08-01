export interface Metadata {
  name: string;
  generateName?: string;
  labels?: {
    [key: string]: string;
  } | null;
  annotations?: {
    [key: string]: string;
  } | null;
  version?: number | null;
  creationTimestamp?: string | null;
  deletionTimestamp?: string | null;
}

export interface PersonalAssetGroupSpec {
  displayName: string;
  description?: string;
  priority?: number;
}

export interface PersonalAssetGroupStatus {
  personalAssetCount: number;
}

export interface PersonalAssetSpec {
  displayName: string;
  specification?: string;
  description?: string;
  url?: string;
  cover?: string;
  priority?: number;
  groupName: string;
}

export interface PersonalAsset {
  spec: PersonalAssetSpec;
  apiVersion: string;
  kind: string;
  metadata: Metadata;
}

export interface PersonalAssetGroup {
  spec: PersonalAssetGroupSpec;
  apiVersion: string;
  kind: string;
  metadata: Metadata;
  status: PersonalAssetGroupStatus;
}

export interface PersonalAssetList {
  page: number;
  size: number;
  total: number;
  totalPages: number;
  items: Array<PersonalAsset>;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
}

export interface PersonalAssetGroupList {
  page: number;
  size: number;
  total: number;
  items: Array<PersonalAssetGroup>;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
}
