import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link, useNavigate, useParams } from 'react-router';
import { handleServerError, setYupDefaults } from 'app/common/utils';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { RoleDTO } from 'app/role/role-model';
import axios from 'axios';
import InputRow from 'app/common/input-row/input-row';
import useDocumentTitle from 'app/common/use-document-title';
import * as yup from 'yup';


function getSchema() {
  setYupDefaults();
  return yup.object({
    name: yup.string().emptyToNull().max(255).required(),
    description: yup.string().emptyToNull().max(255).required(),
    privilege: yup.array(yup.number().required()).emptyToNull().json()
  });
}

export default function RoleEdit() {
  const { t } = useTranslation();
  useDocumentTitle(t('role.edit.headline'));

  const navigate = useNavigate();
  const [privilegeValues, setPrivilegeValues] = useState<Map<number,string>>(new Map());
  const params = useParams();
  const currentId = +params.id!;

  const useFormResult = useForm({
    resolver: yupResolver(getSchema()),
  });

  const getMessage = (key: string) => {
    const messages: Record<string, string> = {
      ROLE_NAME_UNIQUE: t('exists.role.name'),
      ROLE_DESCRIPTION_UNIQUE: t('exists.role.description')
    };
    return messages[key];
  };

  const prepareForm = async () => {
    try {
      const privilegeValuesResponse = await axios.get('/api/roles/privilegeValues');
      setPrivilegeValues(privilegeValuesResponse.data);
      const data = (await axios.get('/api/roles/' + currentId)).data;
      if (data.privilege) {
        data.privilege = JSON.stringify(data.privilege, undefined, 2);
      }
      useFormResult.reset(data);
    } catch (error: any) {
      handleServerError(error, navigate);
    }
  };

  useEffect(() => {
    prepareForm();
  }, []);

  const updateRole = async (data: RoleDTO) => {
    window.scrollTo(0, 0);
    try {
      await axios.put('/api/roles/' + currentId, data);
      navigate('/roles', {
            state: {
              msgSuccess: t('role.update.success')
            }
          });
    } catch (error: any) {
      handleServerError(error, navigate, useFormResult.setError, t, getMessage);
    }
  };

  return (<>
    <div className="d-flex flex-wrap mb-4">
      <h1 className="flex-grow-1">{t('role.edit.headline')}</h1>
      <div>
        <Link to="/roles" className="btn btn-secondary">{t('role.edit.back')}</Link>
      </div>
    </div>
    <form onSubmit={useFormResult.handleSubmit(updateRole)} noValidate>
      <InputRow useFormResult={useFormResult} object="role" field="id" disabled={true} type="number" />
      <InputRow useFormResult={useFormResult} object="role" field="name" required={true} />
      <InputRow useFormResult={useFormResult} object="role" field="description" required={true} />
      <InputRow useFormResult={useFormResult} object="role" field="privilege" type="multiselect" options={privilegeValues} />
      <input type="submit" value={t('role.edit.headline')} className="btn btn-primary mt-4" />
    </form>
  </>);
}
