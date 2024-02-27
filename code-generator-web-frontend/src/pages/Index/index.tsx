import {listGeneratorVoByPageFastUsingPost} from '@/services/backend/generatorController';
import {UserOutlined} from '@ant-design/icons';
import {PageContainer} from '@ant-design/pro-components';
import {ProFormSelect, QueryFilter} from '@ant-design/pro-form/lib';
import {Alert, Avatar, Card, Flex, Image, Input, List, message, Tag, Typography} from 'antd';
import moment from 'moment';
import React, {useEffect, useState} from 'react';
// @ts-ignore
import {Link} from "umi";
import Marquee from 'react-fast-marquee';

/**
 * 默认分页参数
 */
const DEFAULT_PAGE_PARAMS: PageRequest = {
  current: 1,
  pageSize: 12,
  sortField: 'createTime',
  sortOrder: 'descend',
};

/**
 * 主页
 * @constructor
 */
const IndexPage: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(false);
  const [dataList, setDataList] = useState<API.GeneratorVO[]>([]);
  const [total, setTotal] = useState<number>(0);
  // 搜索条件
  const [searchParams, setSearchParams] = useState<API.GeneratorQueryRequest>({
    ...DEFAULT_PAGE_PARAMS,
  });

  /**
   * 搜索
   */
  const doSearch = async () => {
    setLoading(true);
    try {
      const res = await listGeneratorVoByPageFastUsingPost(searchParams);
      setDataList(res.data?.records ?? []);
      setTotal(Number(res.data?.total) ?? 0);
    } catch (error: any) {
      message.error('获取数据失败，' + error.message);
    }
    setLoading(false);
  };

  useEffect(() => {
    doSearch();
  }, [searchParams]);

  /**
   * 标签列表
   * @param tags
   */
  const tagListView = (tags?: string[]) => {
    if (!tags) {
      return <></>;
    }

    return (
      <div style={{ marginBottom: 8 }}>
        {tags.map((tag) => (
          <Tag key={tag}>{tag}</Tag>
        ))}
      </div>
    );
  };

  return (
    <PageContainer title={<></>}>
      <Alert
        banner
        type="success"
        message={
          <Marquee pauseOnHover gradient={false}>
            欢迎各位使用蜂巢代码生成器，一起来制作出属于你的 CodeGenerator 吧！相信我，利用好这里，一定能够帮助你大幅提高开发效率！
          </Marquee>
        }
      />

      <div style={{ marginBottom: 16 }} />

      <Flex justify="center">
      <Input.Search
        placeholder="搜索代码生成器"
        allowClear
        enterButton="搜索"
        size="large"
        onChange={(e) => {
          searchParams.searchText = e.target.value;
        }}
        onSearch={(value: string) => {
          setSearchParams({
            ...DEFAULT_PAGE_PARAMS,
            searchText: value,
          });
        }}
      />
      </Flex>
      <div style={{ marginBottom: 16 }} />

      <QueryFilter
        span={12}
        labelWidth="auto"
        labelAlign="left"
        defaultCollapsed={false}
        style={{ padding: '16px 0' }}
        onFinish={async (values: API.GeneratorQueryRequest) => {
          setSearchParams({
            ...DEFAULT_PAGE_PARAMS,
            // @ts-ignore
            ...values,
            searchText: searchParams.searchText,
          });
        }}
      >
        <ProFormSelect label="标签" name="tags" mode="tags" />
      </QueryFilter>

      <div style={{ marginBottom: 24 }} />

      <List<API.GeneratorVO>
        rowKey="id"
        loading={loading}
        grid={{
          gutter: 16,
          xs: 1,
          sm: 2,
          md: 3,
          lg: 3,
          xl: 4,
          xxl: 4,
        }}
        dataSource={dataList}
        pagination={{
          current: searchParams.current,
          pageSize: searchParams.pageSize,
          total,
          onChange(current: number, pageSize: number) {
            setSearchParams({
              ...searchParams,
              current,
              pageSize,
            });
          },
        }}
        renderItem={(data) => (
          <List.Item>
            <Link to={`/generator/detail/${data.id}`}>
            <Card hoverable cover={<Image alt={data.name} src={data.picture} />}>
              <Card.Meta
                title={<a>{data.name}</a>}
                description={
                  <Typography.Paragraph ellipsis={{ rows: 2 }} style={{ height: 44 }}>
                    {data.description}
                  </Typography.Paragraph>
                }
              />
              {tagListView(data.tags)}
              <Flex justify="space-between" align="center">
                <Typography.Text type="secondary" style={{ fontSize: 12 }}>
                  {moment(data.createTime).fromNow()}
                </Typography.Text>
                <div>
                  <Avatar src={data.user?.userAvatar ?? <UserOutlined />} />
                </div>
              </Flex>
            </Card>
            </Link>
          </List.Item>
        )}
      />
    </PageContainer>
  );
};

export default IndexPage;
