// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

/** searchAll POST /api/aggregation/search */
export async function searchAllUsingPOST(
  body: API.SearchRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseListObject_>('/api/aggregation/search', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
