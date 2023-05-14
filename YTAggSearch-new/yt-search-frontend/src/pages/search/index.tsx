import React from 'react';
import { Input } from 'antd';
import { PageContainer } from '@ant-design/pro-components';
import { Col, Row } from 'antd/lib/grid';
import type { ProCardTabsProps } from '@ant-design/pro-components';
import { ProCard } from '@ant-design/pro-components';
import { useState } from 'react';
import useUrlState from '@ahooksjs/use-url-state';

const SearchMain: React.FC = () => {

  const { Search } = Input;

  const [tabPosition] = useState<ProCardTabsProps['tabPosition']>('top');
  const [query, setQuery] = useUrlState({ query: '', tab: 'post' });

  const onSearch = (value: string) =>{
    setQuery({
      query: value,
    })
    alert(JSON.stringify(query))
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
                value={query.query}
                onChange={(e) => setQuery({ query: e.target.value})}
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
                    children: `帖子`,
                  },
                  {
                    label: `图片`,
                    key: 'picture',
                    children: `图片`,
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
