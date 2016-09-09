/*
 * Copyright (c) 2016 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.agate.config.metrics;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import com.google.common.base.Strings;

/**
 * A health indicator check for a component of your application
 */
public abstract class HealthCheckIndicator implements HealthIndicator {

  private static final Result HEALTHY = new Result(true, null, null);

  /**
   * Perform a check of the application component.
   *
   * @return if the component is healthy, a healthy Result; otherwise, an unhealthy Result
   * with a descriptive error message or/and exception
   * @throws Exception if there is an unhandled error during the health check; this will result in
   * a failed health check
   */
  protected abstract Result check();

  @Override
  final public Health health() {
    try {
      return buildHealth(check());
    } catch(Exception e) {
      return buildHealth(unhealthy(e));
    }
  }

  private Health buildHealth(Result result) {
    Health.Builder builder = new Health.Builder().status(result.isHealthy() ? Status.UP : Status.UNKNOWN)
      .withDetail("healthy", result.isHealthy());

    if (!Strings.isNullOrEmpty(result.getMessage())){
      builder.withDetail("message", result.getMessage());
    }

    if (!Strings.isNullOrEmpty(result.getException())) {
      builder.withDetail("exception", result.getException());
    }

    return builder.build();
  }

  /**
   * @return a healthy Result with no additional message
   */
  public static Result healthy() {
    return HEALTHY;
  }

  /**
   * @param message an informative message
   * @return a healthy Result with an additional message
   */
  public static Result healthy(String message) {
    return new Result(true, message, null);
  }

  /**
   * @param message an informative message describing how the health check indicator failed
   * @return an unhealthy Result with the given message
   */
  public static Result unhealthy(String message) {
    return new Result(false, message, null);
  }

  /**
   * @param error an exception thrown during the health check
   * @return an unhealthy Result with the given error
   */
  public static Result unhealthy(Throwable error) {
    return new Result(false, error.getMessage(), error);
  }

  /**
   * @param message an informative message describing how the health check indicator failed
   * @param error an exception thrown during the health check
   * @return an unhealthy Result with the given error
   */
  public static Result unhealthy(String message, Throwable error) {
    return new Result(false, message, error);
  }

  /**
   * The result of a HealthCheckIndicator
   */
  public static class Result {
    private final boolean healthy;

    private final String message;

    private Throwable exception;

    public Result(boolean healthy, String message) {
      this.healthy = healthy;
      this.message = message;
    }

    public Result(boolean healthy, String message, Throwable exception) {
      this.healthy = healthy;
      this.message = message;
      this.exception = exception;
    }

    public boolean isHealthy() {
      return healthy;
    }

    public String getMessage() {
      return message;
    }

    public String getException() {
      return ExceptionUtils.getFullStackTrace(exception);
    }
  }
}

