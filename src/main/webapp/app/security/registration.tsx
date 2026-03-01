import React from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router';
import { handleServerError, setYupDefaults } from 'app/common/utils';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { RegistrationRequest } from 'app/security/authentication-model';
import axios from 'axios';
import InputRow from 'app/common/input-row/input-row';
import useDocumentTitle from 'app/common/use-document-title';
import * as yup from 'yup';


function getSchema() {
  setYupDefaults();
  return yup.object({
    name: yup.string().emptyToNull().max(255),
    username: yup.string().emptyToNull().max(255).required(),
    password: yup.string().emptyToNull().max(72).required()
  });
}

export default function Registration() {
  const { t } = useTranslation();
  useDocumentTitle(t('authentication.login.headline'));

  const navigate = useNavigate();

  const useFormResult = useForm({
    resolver: yupResolver(getSchema()),
  });

  const getMessage = (key: string) => {
    const messages: Record<string, string> = {
      USERS_USERNAME_UNIQUE: t('registration.register.taken')
    };
    return messages[key];
  };

  const register = async (data: RegistrationRequest) => {
    window.scrollTo(0, 0);
    try {
      await axios.post('/register', data);
      navigate('/login', {
            state: {
              msgSuccess: t('registration.register.success')
            }
          });
    } catch (error: any) {
      handleServerError(error, navigate, useFormResult.setError, t, getMessage);
    }
  };

  return (<>
    <h1 className="mb-4">{t('registration.register.headline')}</h1>
    <form onSubmit={useFormResult.handleSubmit(register)} noValidate>
      <InputRow useFormResult={useFormResult} object="registrationRequest" field="name" />
      <InputRow useFormResult={useFormResult} object="registrationRequest" field="username" required={true} />
      <InputRow useFormResult={useFormResult} object="registrationRequest" field="password" required={true} type="password" />
      <input type="submit" value={t('registration.register.headline')} className="btn btn-primary mt-4" />
    </form>
  </>);
}
