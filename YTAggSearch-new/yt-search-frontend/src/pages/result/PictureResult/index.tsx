import React, {useState} from 'react';
import {Button, Card, List} from 'antd';
const App: React.FC = () => {
  const [initLoading, setInitLoading] = useState(true);
  const [loading, setLoading] = useState(false);

  const data = [
    {
      title: '小黑子 1',
    },
    {
      title: '小黑子 2',
    },
    {
      title: '小黑子 3',
    },
    {
      title: '小黑子 4',
    },
    {
      title: '小黑子 5',
    },
    {
      title: '小黑子 6',
    },
    {
      title: '小黑子 6',
    },
    {
      title: '小黑子  6',
    },
  ];

  const { Meta } = Card;


  function onLoadMore() {
    alert("laoding more")
  }

  const loadMore =
    !initLoading && !loading ? (
      <div
        style={{
          textAlign: 'center',
          marginTop: 12,
          height: 32,
          lineHeight: '32px',
        }}
      >
        <Button onClick={onLoadMore}>loading more</Button>
      </div>
    ) : null;

  return (
    <>
      <List
        loadMore={loadMore}
        grid={{
          gutter: 16,
          xs: 1,
          sm: 2,
          md: 2,
          lg: 3,
          xl: 4,
          xxl: 5,
        }}
        dataSource={data}
        renderItem={(item) => (
          <List.Item>
            <Card
              hoverable
              style={{ width: 220 }}
              cover={<img alt="example" src="https://imgo.hackhome.com/img2022/8/29/17/504481876.jpg" />}
            >
              <Meta
                title= {item.title}
                description="www.instagram.com"
              />
            </Card>
          </List.Item>
        )}
      />
      <div style={{ textAlign: "center" }}>
        <Button onClick={onLoadMore}>loading more</Button>
      </div>
    </>
  )
}

export default App;
