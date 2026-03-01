import React from 'react';
import { Link } from 'react-router';
import { Trans, useTranslation } from 'react-i18next';
import useDocumentTitle from 'app/common/use-document-title';
import './home.scss';


export default function Home() {
  const { t } = useTranslation();
  useDocumentTitle(t('home.index.headline'));

  return (<>
    <h1 className="mb-4">{t('home.index.headline')}</h1>
    <p><Trans i18nKey="home.index.text" components={{ a: <a />, strong: <strong /> }} /></p>
    <p className="mb-5">
      <span>{t('home.index.swagger.text')}</span>
      <span> </span>
      <a href={process.env.API_PATH + '/swagger-ui.html'} target="_blank">{t('home.index.swagger.link')}</a>.
    </p>
    <div className="col-md-4 mb-5">
      <h4 className="mb-3">{t('home.index.exploreEntities')}</h4>
      <div className="list-group">
        <Link to="/userss" className="list-group-item list-group-item-action">{t('users.list.headline')}</Link>
        <Link to="/roles" className="list-group-item list-group-item-action">{t('role.list.headline')}</Link>
        <Link to="/privileges" className="list-group-item list-group-item-action">{t('privilege.list.headline')}</Link>
        <Link to="/urls" className="list-group-item list-group-item-action">{t('urls.list.headline')}</Link>
      </div>
    </div>
  </>);
}
