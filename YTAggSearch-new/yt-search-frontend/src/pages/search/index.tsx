import React, {useEffect, useRef} from 'react';
import {Input, message} from 'antd';
import { PageContainer } from '@ant-design/pro-components';
import { Col, Row } from 'antd/lib/grid';
import type { ProCardTabsProps } from '@ant-design/pro-components';
import { ProCard } from '@ant-design/pro-components';
import { useState } from 'react';
import useUrlState from '@ahooksjs/use-url-state';
import PostResult from "@/pages/result/PostResult";
import PictureResult from "@/pages/result/PictureResult";
import {searchAllUsingPOST} from "@/services/yt-search-backend/searchAllController";

const SearchMain: React.FC = () => {

  const { Search } = Input;

  const [tabPosition] = useState<ProCardTabsProps['tabPosition']>('top');
  const [query, setQuery] = useUrlState({ query: '', tab: 'post' });
  //const [queryTest, setQueryTest] = useState<string>(query.query)
  const [queryTest, setQueryTest] = useState<string>(query.query);
  //const data = useRef<any>([]);
  const [data, setData] = useState<any>([]);


  const searchAll = async (searchTest: string) => {
    try {
      const searchResult: API.BaseResponseListObject_ = await searchAllUsingPOST({
        searchTest: searchTest,
        tab: query.tab
      })
      if (searchResult.code === 0) {
        setData(searchResult.data);
      } else {
        message.error(searchResult.message);
      }
    } catch (e) {
      message.error("搜索错误");
    }
  }

  useEffect(() => {
    if (query.query !== null && query.query !== "") {
      searchAll(query.query).then(r => {});
    }
  },[query])

  const onSearch = (value: string) =>{
    setQuery({
      query: value,
    })
    searchAll(value).then(r => {});
    //alert(JSON.stringify(query))
  };

  return (


    <PageContainer title={false} ghost={true}>
      {/*<>*/}
      {/*  <button*/}
      {/*    style={{ marginRight: 8 }}*/}
      {/*    type="button"*/}
      {/*    onClick={() => setState({ count: Number(state.count || 0) + 1 })}*/}
      {/*  >*/}
      {/*    add*/}
      {/*  </button>*/}
      {/*  <button type="button" onClick={() => setState({ count: undefined })}>*/}
      {/*    clear*/}
      {/*  </button>*/}
      {/*  <div>state: {state?.count}</div>*/}
      {/*</>*/}
      <ProCard direction="column" ghost gutter={[0, 8]}>
        <Row>
          <Col xs={0} sm={3}></Col>
          <Col xs={24} sm={18}>
            <ProCard  layout="center" bordered>
              <Search
                value={queryTest}
                onChange={(e) => setQueryTest(e.target.value)}
                placeholder="请输入搜素内容"
                allowClear
                enterButton="Search"
                size="large"
                onSearch={onSearch}
              />

            </ProCard>
          </Col>
          <Col xs={0} sm={3}></Col>
        </Row>

      </ProCard>
      <br/>

      <Row>
        <Col xs={0} sm={3}></Col>
        <Col xs={24} sm={18}>
          <div>

            <ProCard
              tabs={{
                tabPosition,
                activeKey: query.tab,
                items: [
                  {
                    label: `帖子`,
                    key: 'post',
                    children: <PostResult />,
                  },
                  {
                    label: `图片`,
                    key: 'picture',
                    children: <PictureResult data={data} />,
                  },
                  {
                    label: `用户`,
                    key: 'user',
                    children: `用户`,
                  },
                ],
                onChange: (key) => {
                  setQuery({
                    query: query.query,
                    tab: key
                  });
                },
              }}
            />
          </div>
        </Col>
        <Col xs={0} sm={3}></Col>
      </Row>

    </PageContainer>
  );
};
export default SearchMain;
