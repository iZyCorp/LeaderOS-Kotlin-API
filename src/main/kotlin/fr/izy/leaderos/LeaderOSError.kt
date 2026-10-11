package fr.izy.leaderos

/**
 * Error codes sent by LeaderOS in the `error` field of a failed response.
 */
enum class LeaderOSError {
    // General
    USER_NOT_FOUND,
    INVALID_API_KEY,

    // Auth
    WRONG_PASSWORD,
    USERNAME_ALREADY_EXIST,
    EMAIL_ALREADY_EXIST,
    REGISTER_LIMIT,
    INVALID_USERNAME,
    INVALID_EMAIL,
    INVALID_PASSWORD,
    WRONG_CODE,
    SESSION_NOT_FOUND,
    TFA_VERIFICATION_FAILED,

    // Store
    INVALID_QUANTITY,
    INSUFFICIENT_BALANCE,
    OUT_OF_STOCK,
    PRODUCT_NOT_FOUND,
    REQUIRED_PRODUCT,
    REQUIRED_LINKED_ACCOUNT,
    DOWNGRADE_NOT_ALLOWED,
    INVALID_VARIABLE,

    // Donations
    INVALID_LIMIT,
    INVALID_TYPE,

    // Bazaar
    INSERT_ERROR,
    DELETE_ERROR,

    // Support
    TICKET_NOT_FOUND,

    // Credits
    INVALID_AMOUNT,
    NOT_ENOUGH_CREDITS,
    INVALID_TARGET,
    TARGET_USER_NOT_FOUND,

    /**
     * The endpoint does not exist on this website: check the configured [LeaderOSVersion]
     * and that the related LeaderOS module is enabled.
     */
    ROUTE_NOT_FOUND,

    /**
     * LeaderOS sent an error code this wrapper does not know yet (see [LeaderOSException.errorCode]),
     * or no error code at all.
     */
    UNKNOWN,

    /**
     * LeaderOS answered successfully but with a payload the wrapper could not read.
     */
    UNEXPECTED_RESPONSE;

    companion object {
        internal fun fromCode(code: String?): LeaderOSError =
            entries.firstOrNull { it.name == code && it != UNEXPECTED_RESPONSE } ?: UNKNOWN
    }
}
