import {
// @ts-ignore
  getGeneratorVoByIdUsingGet, useGeneratorUsingPost,
} from '@/services/backend/generatorController';
import { Link, useModel, useParams } from '@@/exports';
import { CreditCardOutlined, DownloadOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-components';
import {
  Button,
  Card,
  Col,
  Collapse,
  Form,
  Image,
  Input,
  message,
  Radio,
  Row,
  Space,
  Tag,
  Typography,
} from 'antd';
import { saveAs } from 'file-saver';
import React, { useEffect, useState } from 'react';

/**
 * 生成器使用页
 * @constructor
 */
const GeneratorDetailPage: React.FC = () => {
  const { id } = useParams();

  const [loading, setLoading] = useState<boolean>(false);
  const [downloading, setdownloading] = useState<boolean>(false);
  const [data, setData] = useState<API.GeneratorVO>({});
  const { initialState } = useModel('@@initialState');
  const { currentUser } = initialState ?? {};
  const [form] = Form.useForm();

  const models = data.modelConfig?.models ?? [];

  /**
   * 设置初始值
   * @param models
   */
  const modelInitialValues = (models: API.ModelInfo[]) => {
    let res = {};
    models?.forEach((mode) => {
      if (mode.groupKey) {
        res = {
          ...res,
          // @ts-ignore
          [mode.groupKey]: modelInitialValues(mode.models),
        };
      } else {
        res = {
          ...res,
          // @ts-ignore
          [mode.fieldName]: mode.defaultValue,
        };
      }
    });
    return res;
  };
  let defaultValue = modelInitialValues(models);
  const [formValues, setFormValues] = useState<any>({ ...defaultValue });
  const isEmptyObject = (obj: any) => Object.entries(obj).length === 0;
  /**
   * 加载数据
   */
  const loadData = async () => {
    if (!id) {
      return;
    }
    setLoading(true);
    try {
      const res = await getGeneratorVoByIdUsingGet({
        // @ts-ignore
        id,
      });
      setData(res.data || {});
    } catch (error: any) {
      message.error('获取数据失败，' + error.message);
    }
    setLoading(false);
  };
  useEffect(() => {
    loadData();
  }, [id]);

  /**
   * 标签列表视图
   * @param tags
   */
  const tagListView = (tags?: string[]) => {
    if (!tags) {
      return <></>;
    }

    return (
      <div style={{ marginBottom: 8 }}>
        {tags.map((tag: string) => {
          return <Tag key={tag}>{tag}</Tag>;
        })}
      </div>
    );
  };

  /**
   * 生成按钮
   */
  const downloadButton = data.distPath && currentUser && (
    <Button
      loading={downloading}
      type="primary"
      icon={<DownloadOutlined />}
      onClick={async () => {
        setdownloading(true);
        let value = form.getFieldsValue();
        // eslint-disable-next-line react-hooks/rules-of-hooks
        const blob = await useGeneratorUsingPost(
          {
            id: data.id,
            dataModel: value,
          },
          {
            responseType: 'blob',
          },
        );
        // 使用 file-saver 来保存文件
        const fullPath = data.distPath || '';
        saveAs(blob, fullPath.substring(fullPath.lastIndexOf('/') + 1));
        setdownloading(false);
      }}
    >
      生成代码
    </Button>
  );

  /**
   * 详情按钮
   */
  const detailButton = (
    <Link to={`/generator/detail/${data.id}`}>
      <Button icon={<CreditCardOutlined />}>查看详情</Button>
    </Link>
  );

  const renderGroupFields = (model: any) => {
    if (!model.groupKey) {
      return null;
    }
    // 处理分组字段
    // 如果有分组字段
    const conditionFieldName = model.condition; // 假设condition字段指定了未分组字段的fieldName
    // 检查condition字段的值，如果为false，则不展示该分组
    // 第一次进来 formValues 为空 因为useState 是异步的
    if (isEmptyObject(formValues)) {
      // @ts-ignore
      if (conditionFieldName && !defaultValue[conditionFieldName]) {
        return null;
      }
      console.log(123);
    } else {
      if (conditionFieldName && !formValues[conditionFieldName]) {
        return null;
      }

      console.log(456);
    }

    return (
      <Collapse
        key={model.groupKey}
        style={{ marginBottom: 24 }}
        items={[
          {
            key: model.groupKey,
            label: model.groupName,
            children: model.models?.map((subModel: any) => (
              <Form.Item
                key={subModel.fieldName}
                label={subModel.fieldName + `(${subModel.description})`}
                name={[model.groupKey, subModel.fieldName]}
              >
                <Input placeholder={subModel.description} />
              </Form.Item>
            )),
          },
        ]}
        bordered={false}
        defaultActiveKey={[model.groupKey]}
      />
    );
  };

  const renderIndividualFields = (model: any) => {
    if (model.groupKey) {
      return null;
    }
    return model.type === 'Boolean' ? (
      // 对于布尔字段使用 Radio.Group
      <Form.Item
        key={model.fieldName}
        label={model.description + `(${model.fieldName})`}
        name={model.fieldName}
      >
        <Radio.Group>
          <Radio value={true}>是</Radio>
          <Radio value={false}>否</Radio>
        </Radio.Group>
      </Form.Item>
    ) : (
      <Form.Item
        key={model.fieldName}
        label={model.description + `(${model.fieldName})`}
        name={model.fieldName}
      >
        <Input placeholder={model.description} />
      </Form.Item>
    );
  };

  return (
    <PageContainer title={<></>} loading={loading}>
      <Card>
        <Row justify="space-between" gutter={[32, 32]}>
          <Col flex="62%">
            <Space size="large" align="center">
              <Typography.Title level={4}>{data.name}</Typography.Title>
              {tagListView(data.tags)}
            </Space>
            <Form
              form={form}
              initialValues={defaultValue}
              onValuesChange={(changedValues, allValues) => setFormValues(allValues)}
            >
              {models.map((model) => renderIndividualFields(model))}
              {models.map((model) => renderGroupFields(model))}
            </Form>
            <Space size="middle">
              {downloadButton}
              {detailButton}
            </Space>
          </Col>
          <Col flex="320px">
            <Image src={data.picture} />
          </Col>
        </Row>
      </Card>
    </PageContainer>
  );
};
export default GeneratorDetailPage;
