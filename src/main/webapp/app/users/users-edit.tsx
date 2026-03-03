import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link, useNavigate, useParams } from 'react-router';
import { handleServerError, setYupDefaults } from 'app/common/utils';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { UsersDTO } from 'app/users/users-model';
import axios from 'axios';
import InputRow from 'app/common/input-row/input-row';
import useDocumentTitle from 'app/common/use-document-title';
import * as yup from 'yup';


function getSchema() {
  setYupDefaults();
  return yup.object({
    name: yup.string().emptyToNull().max(255),
    email: yup.string().emptyToNull().max(255),
    username: yup.string().emptyToNull().max(255).required(),
    password: yup.string().emptyToNull().max(255).required(),
    role: yup.array(yup.number().required()).emptyToNull().json()
  });
}

export default function UsersEdit() {
  const { t } = useTranslation();
  useDocumentTitle(t('users.edit.headline'));

  const navigate = useNavigate();
  const [roleValues, setRoleValues] = useState<Map<number,string>>(new Map());
  const params = useParams();
  const currentId = +params.id!;

  const useFormResult = useForm({
    resolver: yupResolver(getSchema()),
  });

  const getMessage = (key: string) => {
    const messages: Record<string, string> = {
      USERS_USERNAME_UNIQUE: t('exists.users.username')
    };
    return messages[key];
  };

  const prepareForm = async () => {
    try {
      const roleValuesResponse = await axios.get('/api/user/roleValues');
      setRoleValues(roleValuesResponse.data);
      const data = (await axios.get('/api/user/' + currentId)).data;
      if (data.role) {
        data.role = JSON.stringify(data.role, undefined, 2);
      }
      useFormResult.reset(data);
    } catch (error: any) {
      handleServerError(error, navigate);
    }
  };

  useEffect(() => {
    prepareForm();
  }, []);

  const updateUsers = async (data: UsersDTO) => {
    window.scrollTo(0, 0);
    try {
      await axios.put('/api/user/' + currentId, data);
      navigate('/Users', {
            state: {
              msgSuccess: t('users.update.success')
            }
          });
    } catch (error: any) {
      handleServerError(error, navigate, useFormResult.setError, t, getMessage);
    }
  };

  return (<>
    <div className="d-flex flex-wrap mb-4">
      <h1 className="flex-grow-1">{t('users.edit.headline')}</h1>
      <div>
        <Link to="/Users" className="btn btn-secondary">{t('users.edit.back')}</Link>
      </div>
    </div>
    <form onSubmit={useFormResult.handleSubmit(updateUsers)} noValidate>
      <InputRow useFormResult={useFormResult} object="users" field="id" disabled={true} type="number" />
      <InputRow useFormResult={useFormResult} object="users" field="name" />
      <InputRow useFormResult={useFormResult} object="users" field="email" />
      <InputRow useFormResult={useFormResult} object="users" field="username" required={true} />
      <InputRow useFormResult={useFormResult} object="users" field="password" required={true} type="password" />
      <InputRow useFormResult={useFormResult} object="users" field="role" type="multiselect" options={roleValues} />
      <input type="submit" value={t('users.edit.headline')} className="btn btn-primary mt-4" />
    </form>
  </>);
}
