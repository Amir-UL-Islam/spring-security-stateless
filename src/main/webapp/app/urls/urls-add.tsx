import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link, useNavigate } from 'react-router';
import { handleServerError, setYupDefaults } from 'app/common/utils';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { UrlsDTO } from 'app/urls/urls-model';
import axios from 'axios';
import InputRow from 'app/common/input-row/input-row';
import useDocumentTitle from 'app/common/use-document-title';
import * as yup from 'yup';


function getSchema() {
  setYupDefaults();
  return yup.object({
    endpoint: yup.string().emptyToNull().max(255).required(),
    method: yup.string().emptyToNull().max(255).required(),
    privilege: yup.number().integer().emptyToNull().required()
  });
}

export default function UrlsAdd() {
  const { t } = useTranslation();
  useDocumentTitle(t('urls.add.headline'));

  const navigate = useNavigate();
  const [privilegeValues, setPrivilegeValues] = useState<Map<number,string>>(new Map());

  const useFormResult = useForm({
    resolver: yupResolver(getSchema()),
  });

  const getMessage = (key: string) => {
    const messages: Record<string, string> = {
      URLS_ENDPOINT_UNIQUE: t('exists.urls.endpoint')
    };
    return messages[key];
  };

  const prepareRelations = async () => {
    try {
      const privilegeValuesResponse = await axios.get('/api/url/privilegeValues');
      setPrivilegeValues(privilegeValuesResponse.data);
    } catch (error: any) {
      handleServerError(error, navigate);
    }
  };

  useEffect(() => {
    prepareRelations();
  }, []);

  const createUrls = async (data: UrlsDTO) => {
    window.scrollTo(0, 0);
    try {
      await axios.post('/api/url', data);
      navigate('/urls', {
            state: {
              msgSuccess: t('urls.create.success')
            }
          });
    } catch (error: any) {
      handleServerError(error, navigate, useFormResult.setError, t, getMessage);
    }
  };

  return (<>
    <div className="d-flex flex-wrap mb-4">
      <h1 className="flex-grow-1">{t('urls.add.headline')}</h1>
      <div>
        <Link to="/urls" className="btn btn-secondary">{t('urls.add.back')}</Link>
      </div>
    </div>
    <form onSubmit={useFormResult.handleSubmit(createUrls)} noValidate>
      <InputRow useFormResult={useFormResult} object="urls" field="endpoint" required={true} />
      <InputRow useFormResult={useFormResult} object="urls" field="method" required={true} />
      <InputRow useFormResult={useFormResult} object="urls" field="privilege" required={true} type="select" options={privilegeValues} />
      <input type="submit" value={t('urls.add.headline')} className="btn btn-primary mt-4" />
    </form>
  </>);
}
