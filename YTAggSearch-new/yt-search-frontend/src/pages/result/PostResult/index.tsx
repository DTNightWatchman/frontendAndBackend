import React, { useState } from 'react';
import { Button, List, Skeleton, Tag} from 'antd';
import { Divider } from 'antd';


export type Props = {
  loadingState: boolean
  data: []
}


const App: React.FC<Props> = (props) => {
  const [initLoading] = useState(false);
  const [loading] = useState(false);
  const {data, loadingState} = props;





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
        <Button >loading more</Button>
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
                  <a href="https://ant.design">{item.title}</a>
                  <Divider type="vertical" />

                    {item.tagList.map((tag: string) => (
                      <Tag style={{height: 20}} color={"blue"} key={tag}>
                        {tag}
                      </Tag>
                    ))}
                </div>
              }
              description={<div dangerouslySetInnerHTML={{ __html: item.content }} />}
            />

          </Skeleton>

        </List.Item>
      )}

    />
  );
};

export default App;
