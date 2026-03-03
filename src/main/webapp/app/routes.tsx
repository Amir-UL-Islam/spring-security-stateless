import React from 'react';
import { createBrowserRouter, RouterProvider } from 'react-router';
import App from "./app";
import Home from './home/home';
import UsersList from './users/users-list';
import UsersAdd from './users/users-add';
import UsersEdit from './users/users-edit';
import RoleList from './role/role-list';
import RoleAdd from './role/role-add';
import RoleEdit from './role/role-edit';
import PrivilegeList from './privilege/privilege-list';
import PrivilegeAdd from './privilege/privilege-add';
import PrivilegeEdit from './privilege/privilege-edit';
import UrlsList from './urls/urls-list';
import UrlsAdd from './urls/urls-add';
import UrlsEdit from './urls/urls-edit';
import Authentication from './security/authentication';
import Registration from './security/registration';
import CompleteLogin from './security/complete-login';
import Error from './error/error';
import { ADMIN, USER } from 'app/security/authentication-provider';


export default function AppRoutes() {
  const router = createBrowserRouter([
    {
      element: <App />,
      children: [
        { path: '', element: <Home /> , handle: { roles: [ADMIN, USER] } },
        { path: 'Users', element: <UsersList /> , handle: { roles: [ADMIN, USER] } },
        { path: 'Users/add', element: <UsersAdd /> , handle: { roles: [ADMIN, USER] } },
        { path: 'Users/edit/:id', element: <UsersEdit /> , handle: { roles: [ADMIN, USER] } },
        { path: 'roles', element: <RoleList /> , handle: { roles: [ADMIN] } },
        { path: 'roles/add', element: <RoleAdd /> , handle: { roles: [ADMIN] } },
        { path: 'roles/edit/:id', element: <RoleEdit /> , handle: { roles: [ADMIN] } },
        { path: 'privileges', element: <PrivilegeList /> , handle: { roles: [ADMIN] } },
        { path: 'privileges/add', element: <PrivilegeAdd /> , handle: { roles: [ADMIN] } },
        { path: 'privileges/edit/:id', element: <PrivilegeEdit /> , handle: { roles: [ADMIN] } },
        { path: 'urls', element: <UrlsList /> , handle: { roles: [ADMIN] } },
        { path: 'urls/add', element: <UrlsAdd /> , handle: { roles: [ADMIN] } },
        { path: 'urls/edit/:id', element: <UrlsEdit /> , handle: { roles: [ADMIN] } },
        { path: 'login', element: <Authentication /> },
        { path: 'register', element: <Registration /> },
        { path: 'completeLogin', element: <CompleteLogin /> },
        { path: 'error', element: <Error /> },
        { path: '*', element: <Error /> }
      ]
    }
  ]);

  return (
      <RouterProvider router={router} />
  );
}