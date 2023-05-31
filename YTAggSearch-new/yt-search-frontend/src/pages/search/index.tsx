import React, {useEffect,} from 'react';
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
import UserResult from '../result/UserResult';

const SearchMain: React.FC = () => {

  const { Search } = Input;

  const [tabPosition] = useState<ProCardTabsProps['tabPosition']>('top');
  const [query, setQuery] = useUrlState({ query: '', tab: 'post' });
  //const [queryTest, setQueryTest] = useState<string>(query.query)
  const [queryTest, setQueryTest] = useState<string>(query.query);
  //const data = useRef<any>([]);
  const [postData, setPostData] = useState<any>([]);
  const [pictureData, setPictureData] = useState<any>([]);
  const [userData, setUserData] = useState<any>([]);
  const [javaApiDocData, setJavaApiDocData] = useState<any>([]);
  const [loading, setLoading] = useState(false);


  const searchAll = async (searchTest: string) => {
    try {
      setLoading(true)
      const searchResult: API.BaseResponseListObject_ = await searchAllUsingPOST({
        searchTest: searchTest,
        tab: query.tab
      })
      if (searchResult.code === 0) {
        if (query.tab === 'post') {
          setPostData(searchResult.data);
        } else if (query.tab === 'picture') {
          setPictureData(searchResult.data);
        } else if (query.tab === 'user') {
          setUserData(searchResult.data);
        } else if (query.tab === 'javaApiDoc') {
          setJavaApiDocData(searchResult.data);
        }

      } else {
        message.error(searchResult.message);
      }
      setLoading(false)
    } catch (e) {
      message.error("搜索错误");
      setLoading(false);
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
              loading={true}
              tabs={{
                tabPosition,
                activeKey: query.tab,
                items: [
                  {
                    label: `帖子`,
                    key: 'post',
                    children: <PostResult data={postData} loadingState={loading} />,
                  },
                  {
                    label: `图片`,
                    key: 'picture',
                    children: <PictureResult data={pictureData} loadingState={loading} />,
                  },
                  {
                    label: `用户`,
                    key: 'user',
                    children: <UserResult data={userData} loadingState={loading} />,
                  },
                  {
                    label: `Java api文档`,
                    key: 'javaApiDoc',
                    children: <UserResult data={javaApiDocData} loadingState={loading} />,
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
