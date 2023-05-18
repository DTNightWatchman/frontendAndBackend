import React, {useEffect, useRef, useState} from 'react';
import {Button, Card, List, message} from 'antd';

export type Props = {
  data: []
}

const App: React.FC<Props> = (props) => {
  const [initLoading, setInitLoading] = useState(true);
  const [loading, setLoading] = useState(false);
  const { data } = props;

  const { Meta } = Card;



  function onLoadMore() {
    alert("loading more")
  }

  return (
    <>
      <List
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
        renderItem={(item: any) => (
          <List.Item>
            <Card
              hoverable
              style={{ width: 220 }}
              cover={<img referrerPolicy={"no-referrer"} height={220} alt="example" src={item.url} />}
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
