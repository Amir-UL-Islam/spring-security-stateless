import React, { useEffect, useState, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import { Link, useNavigate } from 'react-router';
import { handleServerError, setYupDefaults } from 'app/common/utils';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { AuthenticationRequest } from 'app/security/authentication-model';
import axios from 'axios';
import useAuthentication from 'app/security/use-authentication';
import InputRow from 'app/common/input-row/input-row';
import useDocumentTitle from 'app/common/use-document-title';
import * as yup from 'yup';


function getSchema() {
  setYupDefaults();
  return yup.object({
    username: yup.string().emptyToNull().max(255).required(),
    password: yup.string().emptyToNull().max(72).required()
  });
}

export default function Authentication() {
  const { t } = useTranslation();
  useDocumentTitle(t('authentication.login.headline'));

  const navigate = useNavigate();
  const authenticationContext = useAuthentication();
  const [randomState, setRandomState] = useState<string|null>(null);
  const [popup, setPopup] = useState<Window|null>(null);
  const popupInterval = useRef<number|undefined>(undefined);

  const useFormResult = useForm({
    resolver: yupResolver(getSchema()),
  });

  const login = async (data: AuthenticationRequest) => {
    window.scrollTo(0, 0);
    try {
      const response = await axios.post('/authenticate', data);
      navigate(authenticationContext.login(response.data));
    } catch (error: any) {
      if (error.status === 401) {
        useFormResult.reset();
        navigate('/login', {
              state: {
                msgError: t('authentication.login.error')
              }
            });
        return;
      }
      handleServerError(error, navigate, useFormResult.setError, t);
    }
  };

  const getRandomString = () => {
    const charset = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    const randomValues = new Uint8Array(12);
    crypto.getRandomValues(randomValues);
    return Array.from(randomValues)
        .map((x) => charset[x % charset.length])
        .join('');
  };

  const startLogin = (providerId: string, targetUrl: string) => {
    const width = 600;
    const height = 450;
    const left = (window.screen.width / 2) - (width / 2);
    const top = (window.screen.height / 2) - (height / 2);
    const redirectUrl = `${window.location.origin}/completeLogin?provider=${providerId}`;
    const randomString = getRandomString();
    setRandomState(randomString);
    console.log(redirectUrl)
    setPopup(window.open(
          `${targetUrl}&redirect_uri=${redirectUrl}&state=${randomString}`,
          'login',
          `width=${width},height=${height},left=${left},top=${top}`
        ));
  };

  const loginGoogle = () => {
    startLogin('google', `https://accounts.google.com/o/oauth2/v2/auth?client_id=${process.env.GOOGLE_CLIENT_ID}&response_type=code&scope=profile`);
  };

  const completeLogin = async (providerId: string, code: string) => {
    let authPath;
    if (providerId === 'google') {
      authPath = '/authenticateGoogle';
    }
    try {
      const response = await axios.post(authPath!, { code });
      navigate(authenticationContext.login(response.data));
    } catch (error: any) {
      handleServerError(error, navigate);
    }
  };

  const checkPopup = () => {
    try {
      // if there is an open popup, check if a redirect back to the app has happened
      if (popup && popup.location.href.indexOf(window.location.origin) === 0) {
        let searchParams = new URLSearchParams(popup.location.search);
        if (popup.location.href.indexOf(window.location.origin) === 0 &&
            searchParams.get('state') === randomState && searchParams.get('code')) {
          completeLogin(searchParams.get('provider')!, searchParams.get('code')!);
        }
        popup.close();
        setRandomState(null);
        setPopup(null);
      }
    } catch (error: any) {
      // ignore CORS errors
    }
  };

  useEffect(() => {
    popupInterval.current = window.setInterval(checkPopup, 500) as number;
    return () => clearInterval(popupInterval.current);
  }, [popup, randomState]);

  return (<>
    <h1 className="mb-4">{t('authentication.login.headline')}</h1>
    <form onSubmit={useFormResult.handleSubmit(login)} noValidate>
      <InputRow useFormResult={useFormResult} object="authentication" field="username" required={true} />
      <InputRow useFormResult={useFormResult} object="authentication" field="password" required={true} type="password" />
      <input type="submit" value={t('authentication.login.headline')} className="btn btn-primary mt-4" />
    </form>
    <p className="mt-5">
      <span>{t('authentication.registerNow.text')}</span>
      <span> </span>
      <Link to="/register">{t('authentication.registerNow.link')}</Link>
    </p>
    <h4 className="mt-5 mb-4">{t('authentication.social.headline')}</h4>
    <div className="col-md-4">
      <a onClick={() => loginGoogle()} className="btn btn-secondary w-100 mb-2">
        <img src="/images/google.svg" width="20" height="20" className="align-text-bottom me-1" />
        <span>{t('authentication.social.google')}</span>
      </a>
    </div>
  </>);
}
