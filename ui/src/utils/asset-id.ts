const LETTERS = "abcdefghijklmnopqrstuvwxyz";
const DIGITS = "0123456789";
const ASSET_ID_CHARS = `${LETTERS}${DIGITS}`;
const DEFAULT_ASSET_ID_LENGTH = 6;
const MIN_ASSET_ID_LENGTH = 2;
const MAX_ASSET_ID_LENGTH = 32;

export const normalizeAssetIdLength = (length = DEFAULT_ASSET_ID_LENGTH) => {
  return Math.min(Math.max(length, MIN_ASSET_ID_LENGTH), MAX_ASSET_ID_LENGTH);
};

const pick = (chars: string) => {
  return chars[Math.floor(Math.random() * chars.length)];
};

export const generateAssetId = (length = DEFAULT_ASSET_ID_LENGTH) => {
  const assetIdLength = normalizeAssetIdLength(length);
  const result = Array.from({ length: assetIdLength }, () => pick(ASSET_ID_CHARS));
  const letterIndex = Math.floor(Math.random() * assetIdLength);
  let digitIndex = Math.floor(Math.random() * assetIdLength);

  if (digitIndex === letterIndex) {
    digitIndex = (digitIndex + 1) % assetIdLength;
  }

  result[letterIndex] = pick(LETTERS);
  result[digitIndex] = pick(DIGITS);

  return result.join("");
};
