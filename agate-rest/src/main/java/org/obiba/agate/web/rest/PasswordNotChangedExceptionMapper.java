/*
 * Copyright (c) 2017 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.agate.web.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.obiba.agate.service.PasswordNotChangedException;
import org.obiba.jersey.exceptionmapper.AbstractErrorDtoExceptionMapper;
import org.obiba.web.model.ErrorDtos;

import com.google.protobuf.GeneratedMessage;


@Provider
public class PasswordNotChangedExceptionMapper extends AbstractErrorDtoExceptionMapper<PasswordNotChangedException> {

  @Override
  protected Response.Status getStatus() {
    return Response.Status.BAD_REQUEST;
  }

  @Override
  protected GeneratedMessage.ExtendableMessage<?> getErrorDto(PasswordNotChangedException e) {
    return ErrorDtos.ClientErrorDto.newBuilder() //
      .setCode(getStatus().getStatusCode()) //
      .setMessageTemplate("server.error.password.not-changed") //
      .setMessage(e.getMessage()) //
      .build();
  }

}
