import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link, useNavigate, useSearchParams } from 'react-router';
import { handleServerError, getListParams } from 'app/common/utils';
import { RoleDTO } from 'app/role/role-model';
import { PagedModel, Pagination } from 'app/common/list-helper/pagination';
import axios from 'axios';
import SearchFilter from 'app/common/list-helper/search-filter';
import Sorting from 'app/common/list-helper/sorting';
import useDocumentTitle from 'app/common/use-document-title';


export default function RoleList() {
  const { t } = useTranslation();
  useDocumentTitle(t('role.list.headline'));

  const [roles, setRoles] = useState<PagedModel<RoleDTO>|undefined>(undefined);
  const navigate = useNavigate();
  const [searchParams, ] = useSearchParams();
  const listParams = getListParams();
  const sortOptions = {
    'id,ASC': t('role.list.sort.id,ASC'), 
    'name,ASC': t('role.list.sort.name,ASC'), 
    'description,ASC': t('role.list.sort.description,ASC')
  };

  const getAllRoles = async () => {
    try {
      const response = await axios.get('/api/roles?' + listParams);
      setRoles(response.data);
    } catch (error: any) {
      handleServerError(error, navigate);
    }
  };

  const confirmDelete = async (id: number) => {
    if (!confirm(t('delete.confirm'))) {
      return;
    }
    try {
      await axios.delete('/api/roles/' + id);
      navigate('/roles', {
            state: {
              msgInfo: t('role.delete.success')
            }
          });
      getAllRoles();
    } catch (error: any) {
      handleServerError(error, navigate);
    }
  };

  useEffect(() => {
    getAllRoles();
  }, [searchParams]);

  return (<>
    <div className="d-flex flex-wrap mb-4">
      <h1 className="flex-grow-1">{t('role.list.headline')}</h1>
      <div>
        <Link to="/roles/add" className="btn btn-primary ms-2">{t('role.list.createNew')}</Link>
      </div>
    </div>
    {((roles && roles.page.totalElements !== 0) || searchParams.get('filter')) && (
    <div className="row">
      <SearchFilter placeholder={t('role.list.filter')} />
      <Sorting sortOptions={sortOptions} rowClass="offset-lg-4" />
    </div>
    )}
    {!roles || roles.page.totalElements === 0 ? (
    <div>{t('role.list.empty')}</div>
    ) : (<>
    <div className="table-responsive">
      <table className="table table-striped table-hover align-middle">
        <thead>
          <tr>
            <th scope="col">{t('role.id.label')}</th>
            <th scope="col">{t('role.name.label')}</th>
            <th scope="col">{t('role.description.label')}</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {roles.content.map((role) => (
          <tr key={role.id}>
            <td>{role.id}</td>
            <td>{role.name}</td>
            <td>{role.description}</td>
            <td>
              <div className="float-end text-nowrap">
                <Link to={'/roles/edit/' + role.id} className="btn btn-sm btn-secondary">{t('role.list.edit')}</Link>
                <span> </span>
                <button type="button" onClick={() => confirmDelete(role.id!)} className="btn btn-sm btn-secondary">{t('role.list.delete')}</button>
              </div>
            </td>
          </tr>
          ))}
        </tbody>
      </table>
    </div>
    <Pagination page={roles.page} />
    </>)}
  </>);
}
