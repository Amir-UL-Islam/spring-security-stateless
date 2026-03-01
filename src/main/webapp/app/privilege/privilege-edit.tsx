import React, { useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { Link, useNavigate, useParams } from 'react-router';
import { handleServerError, setYupDefaults } from 'app/common/utils';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { PrivilegeDTO } from 'app/privilege/privilege-model';
import axios from 'axios';
import InputRow from 'app/common/input-row/input-row';
import useDocumentTitle from 'app/common/use-document-title';
import * as yup from 'yup';


function getSchema() {
  setYupDefaults();
  return yup.object({
    name: yup.string().emptyToNull().max(255).required()
  });
}

export default function PrivilegeEdit() {
  const { t } = useTranslation();
  useDocumentTitle(t('privilege.edit.headline'));

  const navigate = useNavigate();
  const params = useParams();
  const currentId = +params.id!;

  const useFormResult = useForm({
    resolver: yupResolver(getSchema()),
  });

  const getMessage = (key: string) => {
    const messages: Record<string, string> = {
      PRIVILEGE_NAME_UNIQUE: t('exists.privilege.name')
    };
    return messages[key];
  };

  const prepareForm = async () => {
    try {
      const data = (await axios.get('/api/privileges/' + currentId)).data;
      useFormResult.reset(data);
    } catch (error: any) {
      handleServerError(error, navigate);
    }
  };

  useEffect(() => {
    prepareForm();
  }, []);

  const updatePrivilege = async (data: PrivilegeDTO) => {
    window.scrollTo(0, 0);
    try {
      await axios.put('/api/privileges/' + currentId, data);
      navigate('/privileges', {
            state: {
              msgSuccess: t('privilege.update.success')
            }
          });
    } catch (error: any) {
      handleServerError(error, navigate, useFormResult.setError, t, getMessage);
    }
  };

  return (<>
    <div className="d-flex flex-wrap mb-4">
      <h1 className="flex-grow-1">{t('privilege.edit.headline')}</h1>
      <div>
        <Link to="/privileges" className="btn btn-secondary">{t('privilege.edit.back')}</Link>
      </div>
    </div>
    <form onSubmit={useFormResult.handleSubmit(updatePrivilege)} noValidate>
      <InputRow useFormResult={useFormResult} object="privilege" field="id" disabled={true} type="number" />
      <InputRow useFormResult={useFormResult} object="privilege" field="name" required={true} />
      <input type="submit" value={t('privilege.edit.headline')} className="btn btn-primary mt-4" />
    </form>
  </>);
}
