import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link, useNavigate } from 'react-router';
import { handleServerError } from 'app/common/utils';
import { PrivilegeDTO } from 'app/privilege/privilege-model';
import axios from 'axios';
import useDocumentTitle from 'app/common/use-document-title';


export default function PrivilegeList() {
  const { t } = useTranslation();
  useDocumentTitle(t('privilege.list.headline'));

  const [privileges, setPrivileges] = useState<PrivilegeDTO[]>([]);
  const navigate = useNavigate();

  const getAllPrivileges = async () => {
    try {
      const response = await axios.get('/api/privileges');
      setPrivileges(response.data);
    } catch (error: any) {
      handleServerError(error, navigate);
    }
  };

  const confirmDelete = async (id: number) => {
    if (!confirm(t('delete.confirm'))) {
      return;
    }
    try {
      await axios.delete('/api/privileges/' + id);
      navigate('/privileges', {
            state: {
              msgInfo: t('privilege.delete.success')
            }
          });
      getAllPrivileges();
    } catch (error: any) {
      if (error?.response?.data?.code === 'REFERENCED') {
        const messageParts = error.response.data.message.split(',');
        navigate('/privileges', {
              state: {
                msgError: t(messageParts[0]!, { id: messageParts[1]! })
              }
            });
        return;
      }
      handleServerError(error, navigate);
    }
  };

  useEffect(() => {
    getAllPrivileges();
  }, []);

  return (<>
    <div className="d-flex flex-wrap mb-4">
      <h1 className="flex-grow-1">{t('privilege.list.headline')}</h1>
      <div>
        <Link to="/privileges/add" className="btn btn-primary ms-2">{t('privilege.list.createNew')}</Link>
      </div>
    </div>
    {!privileges || privileges.length === 0 ? (
    <div>{t('privilege.list.empty')}</div>
    ) : (
    <div className="table-responsive">
      <table className="table table-striped table-hover align-middle">
        <thead>
          <tr>
            <th scope="col">{t('privilege.id.label')}</th>
            <th scope="col">{t('privilege.name.label')}</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {privileges.map((privilege) => (
          <tr key={privilege.id}>
            <td>{privilege.id}</td>
            <td>{privilege.name}</td>
            <td>
              <div className="float-end text-nowrap">
                <Link to={'/privileges/edit/' + privilege.id} className="btn btn-sm btn-secondary">{t('privilege.list.edit')}</Link>
                <span> </span>
                <button type="button" onClick={() => confirmDelete(privilege.id!)} className="btn btn-sm btn-secondary">{t('privilege.list.delete')}</button>
              </div>
            </td>
          </tr>
          ))}
        </tbody>
      </table>
    </div>
    )}
  </>);
}
