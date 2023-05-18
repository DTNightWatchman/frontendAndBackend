// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

/** searchPicture POST /api/picture/search */
export async function searchPictureUsingPOST(
  body: API.PictureQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseListPicture_>('/api/picture/search', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
