import React from 'react';
import { Input } from 'antd';
import { PageContainer } from '@ant-design/pro-components';
import { Col, Row } from 'antd/lib/grid';
import type { ProCardTabsProps } from '@ant-design/pro-components';
import { ProCard } from '@ant-design/pro-components';
import { useState } from 'react';


const SearchMain: React.FC = () => {
  const onSearch = () => {
    alert("搜索")
  }
  const { Search } = Input;

  const [tab, setTab] = useState('post');
  const [tabPosition] =
    useState<ProCardTabsProps['tabPosition']>('top');

  return (


    <PageContainer title={false} ghost={true}>
      <ProCard direction="column" ghost gutter={[0, 8]}>
        <Row>
          <Col span={3}></Col>
          <Col span={18}>
            <ProCard  layout="center" bordered>
              <Search
                placeholder="请输入搜素内容"
                allowClear
                enterButton="Search"
                size="large"
                onSearch={onSearch}
              />

            </ProCard>
          </Col>
          <Col span={3}></Col>
        </Row>

      </ProCard>
      <br/>

      <Row>
        <Col span={3}></Col>
        <Col span={18}>
          <div>

            <ProCard
              tabs={{
                tabPosition,
                activeKey: tab,
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
                  setTab(key);
                },
              }}
            />
          </div>
        </Col>
        <Col span={3}></Col>
      </Row>

    </PageContainer>
  );
};
export default SearchMain;
