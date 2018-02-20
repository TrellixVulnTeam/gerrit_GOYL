// Copyright (C) 2013 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.gerrit.server.restapi.account;

import com.google.gerrit.extensions.client.GeneralPreferencesInfo;
import com.google.gerrit.extensions.restapi.IdString;
import com.google.gerrit.extensions.restapi.ResourceNotFoundException;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.gerrit.extensions.restapi.RestReadView;
import com.google.gerrit.reviewdb.client.Account;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.account.AccountCache;
import com.google.gerrit.server.account.AccountResource;
import com.google.gerrit.server.account.AccountState;
import com.google.gerrit.server.permissions.GlobalPermission;
import com.google.gerrit.server.permissions.PermissionBackend;
import com.google.gerrit.server.permissions.PermissionBackendException;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class GetPreferences implements RestReadView<AccountResource> {
  private final Provider<CurrentUser> self;
  private final PermissionBackend permissionBackend;
  private final AccountCache accountCache;

  @Inject
  GetPreferences(
      Provider<CurrentUser> self, PermissionBackend permissionBackend, AccountCache accountCache) {
    this.self = self;
    this.permissionBackend = permissionBackend;
    this.accountCache = accountCache;
  }

  @Override
  public GeneralPreferencesInfo apply(AccountResource rsrc)
      throws RestApiException, PermissionBackendException {
    if (self.get() != rsrc.getUser()) {
      permissionBackend.currentUser().check(GlobalPermission.MODIFY_ACCOUNT);
    }

    Account.Id id = rsrc.getUser().getAccountId();
    return accountCache
        .get(id)
        .map(AccountState::getGeneralPreferences)
        .orElseThrow(() -> new ResourceNotFoundException(IdString.fromDecoded(id.toString())));
  }
}
