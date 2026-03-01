import React from 'react';
import { useTranslation } from 'react-i18next';
import { Link, useNavigate } from 'react-router';
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

export default function PrivilegeAdd() {
  const { t } = useTranslation();
  useDocumentTitle(t('privilege.add.headline'));

  const navigate = useNavigate();

  const useFormResult = useForm({
    resolver: yupResolver(getSchema()),
  });

  const getMessage = (key: string) => {
    const messages: Record<string, string> = {
      PRIVILEGE_NAME_UNIQUE: t('exists.privilege.name')
    };
    return messages[key];
  };

  const createPrivilege = async (data: PrivilegeDTO) => {
    window.scrollTo(0, 0);
    try {
      await axios.post('/api/privileges', data);
      navigate('/privileges', {
            state: {
              msgSuccess: t('privilege.create.success')
            }
          });
    } catch (error: any) {
      handleServerError(error, navigate, useFormResult.setError, t, getMessage);
    }
  };

  return (<>
    <div className="d-flex flex-wrap mb-4">
      <h1 className="flex-grow-1">{t('privilege.add.headline')}</h1>
      <div>
        <Link to="/privileges" className="btn btn-secondary">{t('privilege.add.back')}</Link>
      </div>
    </div>
    <form onSubmit={useFormResult.handleSubmit(createPrivilege)} noValidate>
      <InputRow useFormResult={useFormResult} object="privilege" field="name" required={true} />
      <input type="submit" value={t('privilege.add.headline')} className="btn btn-primary mt-4" />
    </form>
  </>);
}
