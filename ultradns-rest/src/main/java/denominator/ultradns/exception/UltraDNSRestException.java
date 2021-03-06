package denominator.ultradns.exception;

import feign.FeignException;

import java.util.Set;

public class UltraDNSRestException extends FeignException {

  /**
   * System Error.
   */
  public static final int SYSTEM_ERROR = 9999;
  /**
   * Zone does not exist in the system.
   */
  public static final int ZONE_NOT_FOUND = 1801;
  /**
   * Zone already exists in the system.
   */
  public static final int ZONE_ALREADY_EXISTS = 1802;
  /**
   * No resource record with GUID found in the system.
   */
  public static final int RESOURCE_RECORD_NOT_FOUND = 2103;
  /**
   * Resource record exists with the same name and type.
   */
  public static final int RESOURCE_RECORD_ALREADY_EXISTS = 2111;

  // there are 51002 potential codes. These are the ones we are handling.
  /**
   * No Pool or Multiple pools of same type exists for the PoolName.
   */
  public static final int DIRECTIONALPOOL_NOT_FOUND = 2142;
  /**
   * Invalid zone name.
   */
  public static final int INVALID_ZONE_NAME = 2507;
  /**
   * Directional Pool Record does not exist in the system.
   */
  public static final int DIRECTIONALPOOL_RECORD_NOT_FOUND = 2705;
  /**
   * Pool does not exist in the system.
   */
  public static final int POOL_NOT_FOUND = 2911;
  /**
   * Pool already created for the given rrGUID.
   */
  public static final int POOL_ALREADY_EXISTS = 2912;
  /**
   * Group does not exist.
   */
  public static final int GROUP_NOT_FOUND = 4003;
  /**
   * Directional feature not Enabled or Directional migration is not done.
   */
  public static final int DIRECTIONAL_NOT_ENABLED = 4006;
  /**
   * Resource Record already exists.
   */
  public static final int POOL_RECORD_ALREADY_EXISTS = 4009;
  /**
   * Invalid_grant:Token not found, expired or invalid.
   */
  public static final int INVALID_GRANT = 60001;
  /**
   * Data not found.
   */
  public static final int DATA_NOT_FOUND = 70002;
  /**
   * Invalid input: record data - Invalid address: 1.1.1.1.
   */
  public static final int INVALID_ADDRESS_IN_RECORD_DATA = 53005;
  /**
   * Cannot find resource record data for the input zone, record type and owner combination.
   */
  public static final int RESOURCE_RECORD_POOL_NOT_FOUND = 56001;
  /**
   * No such path in target JSON document.
   */
  public static final int PATH_NOT_FOUND_TO_PATCH = 170001;

  private static final long serialVersionUID = 1L;
  private final int code;

  public UltraDNSRestException(String message, int code) {
    super(message);
    this.code = code;
  }

  /**
   * Process UltraDNSRestException based on the code.
   * If the code in argument matches the exception code
   * then it will by handled, otherwise it will be rethrown.
   *
   * @param e UltraDNSRestException
   * @param code Ultra error code
     */
  public static void processUltraDnsException(final UltraDNSRestException e, final int code) {
    if (code != e.code()) {
      throw e;
    }
  }

  /**
   * Process UltraDNSRestException based on set of codes.
   * If the exception code any of the code in set
   * then it will by handled, otherwise it will be rethrown.
   *
   * @param e UltraDNSRestException
   * @param codes Set of Ultra error code
   */
  public static void processUltraDnsException(final UltraDNSRestException e, final Set<Integer> codes) {
    if (codes != null && !codes.isEmpty()) {
      if (!codes.contains(e.code())) {
        throw e;
      }
    } else {
      throw e;
    }
  }

  public int code() {
    return code;
  }

  public static class Message {

    private int errorCode;
    private String errorMessage;

    public void setErrorCode(int errorCode) {
      this.errorCode = errorCode;
    }

    public void setErrorMessage(String errorMessage) {
      this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
      return errorCode;
    }

    public String getErrorMessage() {
      return errorMessage;
    }

    @Override
    public String toString() {
      return errorCode + ": " + errorMessage;
    }
  }

}
