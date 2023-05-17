import React, { useRef} from 'react';
import { Input } from 'antd';
import { PageContainer } from '@ant-design/pro-components';
import { Col, Row } from 'antd/lib/grid';
import type { ProCardTabsProps } from '@ant-design/pro-components';
import { ProCard } from '@ant-design/pro-components';
import { useState } from 'react';
import useUrlState from '@ahooksjs/use-url-state';
import PostResult from "@/pages/result/PostResult";
import PictureResult from "@/pages/result/PictureResult";

const SearchMain: React.FC = () => {

  const { Search } = Input;

  const [tabPosition] = useState<ProCardTabsProps['tabPosition']>('top');
  const [query, setQuery] = useUrlState({ query: '', tab: 'post' });
  //const [queryTest, setQueryTest] = useState<string>(query.query)
  const queryTest = useRef<string>(query.query);

  // const onSearch = useCallback((value: string) => {
  //   alert(queryTest.current)
  //   console.log(queryTest.current)
  //   // todo 搜索
  //   setQuery({
  //     query: queryTest.current,
  //   })
  // }, [query, setQuery]);

  const onSearch = (value: string) =>{
    alert(value + " " + query.tab)
    setQuery({
      query: value,
    })
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
                //value={queryTest}
                onChange={(e) => queryTest.current = (e.target.value)}
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
                    children: <PictureResult />,
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
