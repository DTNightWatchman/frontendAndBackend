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
    alert(JSON.stringify(data))
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
              style={{ width: 220 }}
              cover={<img height={220} alt="example" src={item?.userAvatar}/>}
            >
              <Meta
                title= {item.userName}
                description={item.userProfile}
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
