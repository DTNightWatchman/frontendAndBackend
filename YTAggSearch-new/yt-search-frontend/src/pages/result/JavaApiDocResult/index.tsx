import React, { useState } from 'react';
import { Button, List, Skeleton} from 'antd';
import { Divider } from 'antd';


export type Props = {
  loadingState: boolean
  data: [],
  loadMoreFunc: () => void
}


const App: React.FC<Props> = (props) => {
  const [initLoading] = useState(false);
  const [loading] = useState(false);
  const {data, loadingState, loadMoreFunc} = props;


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
        <Button onClick={loadMoreFunc}>loading more</Button>
      </div>
    ) : null;

  return (
    <List
      className="demo-loadmore-list"
      loading={loadingState}
      itemLayout="horizontal"
      loadMore={loadMore}
      dataSource={data}
      renderItem={(item: any) => (
        <List.Item>
          <Skeleton avatar title={false} loading={item.loading} active>
            <List.Item.Meta
              title={
                <div>
                  <a href={item.url}>{item.title}</a>
                  <Divider type="vertical" />
                </div>
              }
              description={<div style={{}} dangerouslySetInnerHTML={{ __html: item.desc }} />}
            />

          </Skeleton>

        </List.Item>
      )}

    />
  );
};

export default App;
