import React from 'react';
import {Button, Card, List} from 'antd';

export type Props = {
  loadingState: boolean
  data: []
}

const App: React.FC<Props> = (props) => {

  const { data , loadingState} = props;

  const { Meta } = Card;

  function onLoadMore() {
    alert("loading more")
  }

  return (
    <>
      <List
        loading={loadingState}
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
                description={item.url}
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
